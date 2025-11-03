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

        Customer customer = customerRepository.findById(orderRequest.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException("Cliente " + orderRequest.getCustomerId() + " no encontrado."));

        Address shippingAddress = addressRepository.findById(orderRequest.getShippingAddressId())
                .orElseThrow(() -> new EntityNotFoundException("Dirección " + orderRequest.getShippingAddressId() + " no encontrada."));

        if (!shippingAddress.getCustomer().getId().equals(customer.getId())) {
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

        BigDecimal totalOrder = BigDecimal.ZERO;

        for (var itemRequest : orderRequest.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Producto " + itemRequest.getProductId() + " no encontrado."));

            stockService.reduceStock(product.getId(), itemRequest.getQuantity());

            OrderItem orderItem = orderItemMapper.toEntity(itemRequest);
            orderItem.setOrder(persistedOrder);
            orderItem.setProduct(product);
            orderItem.setUnitPrice(product.getPrice());

            BigDecimal lineTotal = product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            totalOrder = totalOrder.add(lineTotal);

            persistedOrder.getItems().add(orderItem);

        }

        persistedOrder.setTotal(totalOrder);
        Order savedOrder = orderRepository.save(persistedOrder);

        return orderMapper.toResponseDto(savedOrder);
    }



    @Transactional
    @Override
    public Page<OrderResponseDTO> findAllOrders(Long customerId, LocalDate fromDate, LocalDate toDate, OrderStatus status, Pageable pageable) {
        Specification<Order> spec = OrderSpecification.filterOrders(customerId, fromDate, toDate, status);
        Page<Order> ordersPage = orderRepository.findAll(spec, pageable);
        return ordersPage.map(orderMapper::toResponseDto);
    }

    @Transactional
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

        if (newStatus == OrderStatus.CANCELLED && (currentStatus == OrderStatus.CREATED || currentStatus == OrderStatus.PAID)) {
            returnStockToInventory(existingOrder);
        }

        existingOrder.setStatus(newStatus);
        Order updated = orderRepository.save(existingOrder);
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
                stockService.returnStock(product.getId(), item.getQuantity());
            }
        }
    }
}