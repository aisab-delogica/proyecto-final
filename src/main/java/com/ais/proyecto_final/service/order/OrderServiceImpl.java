package com.ais.proyecto_final.service.order;

import com.ais.proyecto_final.dto.order.OrderRequestDTO;
import com.ais.proyecto_final.dto.order.OrderResponseDTO;
import com.ais.proyecto_final.dto.order.OrderStatusUpdateDTO;
import com.ais.proyecto_final.entity.*;
import com.ais.proyecto_final.exceptions.OrderBusinessException;
import com.ais.proyecto_final.mappers.OrderItemMapper;
import com.ais.proyecto_final.mappers.OrderMapper;
import com.ais.proyecto_final.repository.AddressRepository;
import com.ais.proyecto_final.repository.CustomerRepository;
import com.ais.proyecto_final.repository.OrderRepository;
import com.ais.proyecto_final.repository.ProductRepository;
import com.ais.proyecto_final.repository.OrderSpecification;
import com.ais.proyecto_final.service.stock.StockService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final StockService stockService;

    @Transactional
    @Override
    public OrderResponseDTO createOrder(OrderRequestDTO orderRequest) {
        log.info("Intentando crear el pedido del cliente con ID: {}", orderRequest.getCustomerId());

        Customer customer = customerRepository.findById(orderRequest.getCustomerId())
                .orElseThrow(() -> {
                    log.warn("No se pudo crear el pedido. No se encontró el cliente con ID: {}", orderRequest.getCustomerId());
                    return new EntityNotFoundException("Cliente " + orderRequest.getCustomerId() + " no encontrado.");
                });

        Address shippingAddress = addressRepository.findById(orderRequest.getShippingAddressId())
                .orElseThrow(() -> {
                    log.warn("No se pudo crear el pedido. No se encontró la dirección con ID: {}", orderRequest.getShippingAddressId());
                    return new EntityNotFoundException("Dirección " + orderRequest.getShippingAddressId() + " no encontrada.");
                });

        if (!shippingAddress.getCustomer().getId().equals(customer.getId())) {
            log.warn("No se pudo crear el pedido. La dirección con ID: {} no pertenece al cliente con ID: {}", shippingAddress.getId(), customer.getId());
            throw new OrderBusinessException("La dirección de envío no pertenece al cliente especificado.");
        }

        Order newOrder = orderMapper.toEntity(orderRequest);
        newOrder.setItems(new ArrayList<>());
        newOrder.setCustomer(customer);
        newOrder.setShippingAddress(shippingAddress);
        newOrder.setStatus(OrderStatus.CREATED);
        newOrder.setOrderDate(LocalDateTime.now());
        newOrder.setTotal(BigDecimal.ZERO);

        Order persistedOrder = orderRepository.save(newOrder);
        log.info("Pedido creado con ID: {}. Procesando los productos...", persistedOrder.getId());

        BigDecimal totalOrder = BigDecimal.ZERO;

        try {
            for (var itemRequest : orderRequest.getItems()) {
                log.info("Procesando producto. ID del producto: {}, Cantidad: {}", itemRequest.getProductId(), itemRequest.getQuantity());
                Product product = productRepository.findById(itemRequest.getProductId())
                        .orElseThrow(() -> {
                            log.warn("Error en el pedido {}. No se encontró el producto con ID: {}", persistedOrder.getId(), itemRequest.getProductId());
                            return new EntityNotFoundException("Producto " + itemRequest.getProductId() + " no encontrado.");
                        });

                stockService.reduceStock(product.getId(), itemRequest.getQuantity());

                OrderItem orderItem = orderItemMapper.toEntity(itemRequest);
                orderItem.setOrder(persistedOrder);
                orderItem.setProduct(product);
                orderItem.setUnitPrice(product.getPrice());

                BigDecimal lineTotal = product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
                totalOrder = totalOrder.add(lineTotal);

                persistedOrder.getItems().add(orderItem);
            }
        } catch (OrderBusinessException | EntityNotFoundException e) {
            log.warn("Deshaciendo la creación del pedido {}. Motivo: {}", persistedOrder.getId(), e.getMessage());
            throw e;
        }

        persistedOrder.setTotal(totalOrder);
        Order savedOrder = orderRepository.save(persistedOrder);
        log.info("Pedido ID: {} creado con éxito. Total: {}", savedOrder.getId(), savedOrder.getTotal());

        return orderMapper.toResponseDto(savedOrder);
    }

    @Transactional
    @Override
    public Page<OrderResponseDTO> findAllOrders(Long customerId, LocalDate fromDate, LocalDate toDate, OrderStatus status, Pageable pageable) {
        log.info("Buscando todos los pedidos. Filtros: clienteId={}, desde={}, hasta={}, estado={}, página={}",
                customerId, fromDate, toDate, status, pageable.getPageNumber());
        Specification<Order> spec = OrderSpecification.filterOrders(customerId, fromDate, toDate, status);
        Page<Order> ordersPage = orderRepository.findAll(spec, pageable);
        log.info("Se encontraron {} pedidos en la página {}", ordersPage.getNumberOfElements(), pageable.getPageNumber());
        return ordersPage.map(orderMapper::toResponseDto);
    }

    @Transactional
    @Override
    public OrderResponseDTO getOrderById(Long id) {
        log.info("Buscando el pedido con ID: {}", id);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("No se encontró el pedido con ID: {}", id);
                    return new EntityNotFoundException("Pedido " + id + " no encontrado.");
                });
        log.info("Pedido con ID: {} encontrado", id);
        return orderMapper.toResponseDto(order);
    }

    @Transactional
    @Override
    public OrderResponseDTO updateOrderStatus(Long id, OrderStatusUpdateDTO dto) {
        OrderStatus newStatus = dto.getStatus();
        log.info("Intentando actualizar el estado del pedido {} a {}", id, newStatus);
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("No se pudo actualizar el estado. No se encontró el pedido con ID: {}", id);
                    return new EntityNotFoundException("Pedido " + id + " no encontrado.");
                });

        OrderStatus currentStatus = existingOrder.getStatus();

        if (!isTransitionValid(currentStatus, newStatus)) {
            log.warn("Transición de estado no válida para el pedido {}. De {} a {}", id, currentStatus, newStatus);
            throw new OrderBusinessException(
                    String.format("Transición de estado inválida: de %s a %s.", currentStatus, newStatus));
        }

        if (newStatus == OrderStatus.CANCELLED && (currentStatus == OrderStatus.CREATED || currentStatus == OrderStatus.PAID)) {
            log.info("Se está cancelando el pedido {}. Devolviendo stock al inventario.", id);
            returnStockToInventory(existingOrder);
        }

        existingOrder.setStatus(newStatus);
        Order updated = orderRepository.save(existingOrder);
        log.info("Pedido {} actualizado correctamente al estado {}", id, newStatus);
        return orderMapper.toResponseDto(updated);
    }

    private boolean isTransitionValid(OrderStatus current, OrderStatus next) {
        if (current == next) return true;
        if (current == OrderStatus.SHIPPED || current == OrderStatus.CANCELLED) {
            return false;
        }

        return switch (current) {
            case CREATED -> (next == OrderStatus.PAID || next == OrderStatus.CANCELLED);
            case PAID -> (next == OrderStatus.SHIPPED || next == OrderStatus.CANCELLED);
            default -> false;
        };
    }

    private void returnStockToInventory(Order order) {
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            if (product != null) {
                log.info("Devolviendo {} unidades del producto {} (Pedido {})", item.getQuantity(), product.getId(), order.getId());
                stockService.returnStock(product.getId(), item.getQuantity());
            }
        }
    }
}
