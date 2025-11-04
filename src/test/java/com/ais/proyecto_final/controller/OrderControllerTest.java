package com.ais.proyecto_final.controller;

import com.ais.proyecto_final.dto.order.LineItemRequestDTO;
import com.ais.proyecto_final.dto.order.OrderRequestDTO;
import com.ais.proyecto_final.dto.order.OrderResponseDTO;
import com.ais.proyecto_final.dto.order.OrderStatusUpdateDTO;
import com.ais.proyecto_final.entity.OrderStatus;
import com.ais.proyecto_final.service.order.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    private OrderRequestDTO orderRequest;
    private OrderResponseDTO orderResponse;
    private OrderStatusUpdateDTO statusUpdate;

    @BeforeEach
    void setUp() {
        LineItemRequestDTO validItem = LineItemRequestDTO.builder()
                .productId(1L)
                .quantity(1)
                .build();
        orderResponse = OrderResponseDTO.builder()
                .id(1L)
                .customerId(1L)
                .shippingAddressId(1L)
                .status(OrderStatus.CREATED)
                .total(new BigDecimal("100.00"))
                .items(List.of())
                .build();

        orderRequest = OrderRequestDTO.builder()
                .customerId(1L)
                .shippingAddressId(1L)
                .items(List.of(validItem))
                .build();

        statusUpdate = OrderStatusUpdateDTO.builder()
                .status(OrderStatus.PAID)
                .build();
    }

    @Test
    void createOrder_ShouldReturn201() throws Exception {
        
        when(orderService.createOrder(any(OrderRequestDTO.class))).thenReturn(orderResponse);

        
        mockMvc.perform(post("/api/orders")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void findAllOrders_ShouldReturnPage() throws Exception {
        
        Page<OrderResponseDTO> responsePage = new PageImpl<>(List.of(orderResponse));
        when(orderService.findAllOrders(any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(responsePage);

        
        mockMvc.perform(get("/api/orders")
                        .param("page", "0")
                        .param("size", "5")
                        .param("status", "CREATED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L));
    }

    @Test
    void getOrderById_ShouldReturn200_WhenFound() throws Exception {
        
        when(orderService.getOrderById(1L)).thenReturn(orderResponse);

        
        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getOrderById_ShouldReturn404_WhenNotFound() throws Exception {
        
        when(orderService.getOrderById(99L)).thenThrow(new EntityNotFoundException("Pedido no encontrado"));

        
        mockMvc.perform(get("/api/orders/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateOrderStatus_ShouldReturn200() throws Exception {
        
        orderResponse.setStatus(OrderStatus.PAID);
        when(orderService.updateOrderStatus(eq(1L), any(OrderStatusUpdateDTO.class))).thenReturn(orderResponse);

        
        mockMvc.perform(put("/api/orders/1/status")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAID"));
    }
}