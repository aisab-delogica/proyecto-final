package com.ais.proyecto_final.service.order;

import com.ais.proyecto_final.dto.order.OrderRequestDTO;
import com.ais.proyecto_final.dto.order.OrderResponseDTO;
import com.ais.proyecto_final.dto.order.OrderStatusUpdateDTO;
import com.ais.proyecto_final.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface OrderService {
    OrderResponseDTO createOrder(OrderRequestDTO orderRequest);
    Page<OrderResponseDTO> findAllOrders(Long customerId, LocalDate fromDate, LocalDate toDate, OrderStatus status, Pageable pageable);
    OrderResponseDTO getOrderById(Long id);
    OrderResponseDTO updateOrderStatus(Long id, OrderStatusUpdateDTO dto);
}