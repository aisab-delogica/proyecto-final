package com.ais.proyecto_final.service.order;

import com.ais.proyecto_final.dto.order.OrderRequestDTO;
import com.ais.proyecto_final.dto.order.OrderResponseDTO;
import com.ais.proyecto_final.dto.order.OrderStatusUpdateDTO;
import com.ais.proyecto_final.entity.*;
import com.ais.proyecto_final.exceptions.OrderBusinessException;
import com.ais.proyecto_final.mappers.OrderMapper;
import com.ais.proyecto_final.mappers.OrderItemMapper;
import com.ais.proyecto_final.repository.OrderRepository;
import com.ais.proyecto_final.repository.specs.OrderSpecification; // Importar la Specification
import com.ais.proyecto_final.service.customer.CustomerService;
import com.ais.proyecto_final.service.product.ProductService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper; // Se necesita para mapeo manual
    private final CustomerService customerService; // Asume método getCustomerEntityById
    private final ProductService productService; // Asume métodos getProductEntityById, reduceStock, returnStock

    @Transactional
    @Override
    public OrderResponseDTO createOrder(OrderRequestDTO orderRequest) {

        // 1. VALIDAR CLIENTE Y DIRECCIÓN
        Customer customer = customerService.getCustomerEntityById(orderRequest.getCustomerId()) // Asume que el servicio devuelve la Entidad
                .orElseThrow(() -> new EntityNotFoundException("Cliente " + orderRequest.getCustomerId() + " no encontrado."));

        Address shippingAddress = customerService.getAddressEntityById(orderRequest.getShippingAddressId()) // Asume que el servicio devuelve la Entidad
                .orElseThrow(() -> new EntityNotFoundException("Dirección " + orderRequest.getShippingAddressId() + " no encontrada."));

        if (!shippingAddress.getCustomer().getId().equals(customer.getId())) {
            throw new OrderBusinessException("La dirección de envío no pertenece al cliente especificado.");
        }


        // 2. CREAR LÍNEAS DE PEDIDO Y VALIDAR STOCK/PRECIO
        Order newOrder = orderMapper.toEntity(orderRequest);
        newOrder.setCustomer(customer);
        newOrder.setShippingAddress(shippingAddress);
        newOrder.setStatus(OrderStatus.CREATED);

        BigDecimal totalOrder = BigDecimal.ZERO;
        List<OrderItem> orderItems = newOrder.getItems();
        orderItems.clear();

        for (var itemRequest : orderRequest.getItems()) {
            Product product = productService.getProductEntityById(itemRequest.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Producto " + itemRequest.getProductId() + " no encontrado."));

            if (!product.isActive()) {
                throw new OrderBusinessException("El producto '" + product.getName() + "' no está activo.");
            }
            if (product.getStock() < itemRequest.getQuantity()) {
                throw new OrderBusinessException("Stock insuficiente para el producto '" + product.getName() + "'. Stock actual: " + product.getStock());
            }

            // CREAR OrderItem y calcular totales
            OrderItem orderItem = orderItemMapper.toEntity(itemRequest);
            orderItem.setOrder(newOrder);
            orderItem.setProduct(product);
            orderItem.setUnitPrice(product.getPrice()); // Precio inmutable
            orderItem.setLineTotal(product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity())));

            totalOrder = totalOrder.add(orderItem.getLineTotal());
            orderItems.add(orderItem);
        }

        newOrder.setTotal(totalOrder);


        // 3. REDUCIR STOCK y GUARDAR
        for (var item : orderItems) {
            productService.reduceStock(item.getProduct().getId(), item.getQuantity()); // Asume método transaccional en ProductService
        }

        Order savedOrder = orderRepository.save(newOrder);
        return orderMapper.toResponseDto(savedOrder);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<OrderResponseDTO> findAllOrders(Long customerId, LocalDate fromDate, LocalDate toDate, OrderStatus status, Pageable pageable) {

        // USO DE SPECIFICATION PARA FILTRADO
        Specification<Order> spec = OrderSpecification.filterOrders(customerId, fromDate, toDate, status);

        Page<Order> ordersPage = orderRepository.findAll(spec, pageable);

        return ordersPage.map(orderMapper::toResponseDto);
    }

    @Transactional(readOnly = true)
    @Override
    public OrderResponseDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido " + id + " no encontrado."));

        return orderMapper.toResponseDto(order);
    }

    @Transactional
    @Override
    public OrderResponseDTO updateOrderStatus(Long id, OrderStatusUpdateDTO dto) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido " + id + " no encontrado."));

        OrderStatus newStatus = dto.getStatus();
        OrderStatus currentStatus = existingOrder.getStatus();

        if (!isTransitionValid(currentStatus, newStatus)) {
            throw new OrderBusinessException(
                    String.format("Transición de estado inválida: de %s a %s.", currentStatus, newStatus));
        }

        // Si se cancela, devolver stock
        if (newStatus == OrderStatus.CANCELLED && currentStatus != OrderStatus.CANCELLED) {
            returnStockToInventory(existingOrder);
        }

        existingOrder.setStatus(newStatus);
        Order updated = orderRepository.save(existingOrder);

        return orderMapper.toResponseDto(updated);
    }

    // --- MÉTODOS AUXILIARES ---

    private boolean isTransitionValid(OrderStatus current, OrderStatus next) {
        if (current == next) return true;
        if (current == OrderStatus.CANCELLED || current == OrderStatus.SHIPPED || current == OrderStatus.DELIVERED) {
            // No se permiten cambios desde estados finales
            return false;
        }

        return switch (current) {
            case CREATED -> next == OrderStatus.PAID || next == OrderStatus.CANCELLED;
            case PAID -> next == OrderStatus.SHIPPED || next == OrderStatus.CANCELLED;
            default -> false; // Incluye el caso DELIVERED, SHIPPED, etc.
        };
    }

    private void returnStockToInventory(Order order) {
        for (OrderItem item : order.getItems()) {
            // Asume método transaccional en ProductService para devolver stock
            productService.returnStock(item.getProduct().getId(), item.getQuantity());
        }
    }

    // Asumir métodos de servicio para entidades (necesarios para el mapeo)
    // Estos métodos DEBES implementarlos en CustomerServiceImpl y ProductServiceImpl.
    // Ejemplos de firmas esperadas:
    /*
    public Optional<Customer> getCustomerEntityById(Long id);
    public Optional<Address> getAddressEntityById(Long id);
    public Optional<Product> getProductEntityById(Long id);
    public void reduceStock(Long productId, Integer quantity);
    public void returnStock(Long productId, Integer quantity);
    */
}