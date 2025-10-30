package com.ais.proyecto_final.controller;

import com.ais.proyecto_final.dto.order.OrderRequestDTO;
import com.ais.proyecto_final.dto.order.OrderResponseDTO;
import com.ais.proyecto_final.dto.order.OrderStatusUpdateDTO;
import com.ais.proyecto_final.entity.OrderStatus;
import com.ais.proyecto_final.service.order.OrderService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/orders")
@AllArgsConstructor
public class OrderController {
    private final OrderService orderService;

    // POST /api/orders crea pedido con líneas, cálculo de total y reducción de stock
    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@RequestBody @Valid OrderRequestDTO orderRequest) {
        OrderResponseDTO createdOrder = orderService.createOrder(orderRequest);
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }

    // GET /api/orders lista pedidos con filtros y paginación
    @GetMapping
    public ResponseEntity<Page<OrderResponseDTO>> findAllOrders(
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate,
            @RequestParam(required = false) OrderStatus status,
            Pageable pageable) {

        Page<OrderResponseDTO> orders = orderService.findAllOrders(customerId, fromDate, toDate, status, pageable);
        return ResponseEntity.ok(orders);
    }

    // GET /api/orders/{id} detalle con líneas
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    // PUT /api/orders/{id}/status cambio de estado siguiendo las transiciones válidas
    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponseDTO> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody @Valid OrderStatusUpdateDTO dto) {
        OrderResponseDTO updated = orderService.updateOrderStatus(id, dto);
        return ResponseEntity.ok(updated);
    }
}