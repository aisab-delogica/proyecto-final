package com.ais.proyecto_final.dto;

import com.ais.proyecto_final.dto.order.*;
import com.ais.proyecto_final.entity.OrderStatus;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class OrderDtoTest {

    private final LocalDateTime FIXED_TIME = LocalDateTime.of(2025, 1, 1, 10, 0);
    private final BigDecimal PRICE_1 = new BigDecimal("10.00");
    private final BigDecimal PRICE_2 = new BigDecimal("20.00");

    
    @Test
    void lineItemRequestDTO_ShouldCoverAllLombokMethods() {
        LineItemRequestDTO dto1 = LineItemRequestDTO.builder().productId(1L).quantity(5).build();
        assertNotNull(dto1);
        assertEquals(1L, dto1.getProductId());
        assertEquals(5, dto1.getQuantity());

        
        dto1.setProductId(2L);
        dto1.setQuantity(10);
        assertEquals(2L, dto1.getProductId());
        assertEquals(10, dto1.getQuantity());

        
        LineItemRequestDTO dto2 = LineItemRequestDTO.builder().productId(2L).quantity(10).build();
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotNull(dto1.toString());
    }

    
    @Test
    void lineItemResponseDTO_ShouldCoverAllLombokMethods() {
        LineItemResponseDTO dto1 = LineItemResponseDTO.builder()
                .productId(3L).quantity(2).unitPrice(PRICE_1).lineTotal(PRICE_2).build();

        assertNotNull(dto1);
        assertEquals(3L, dto1.getProductId());
        assertEquals(2, dto1.getQuantity());
        assertEquals(PRICE_1, dto1.getUnitPrice());
        assertEquals(PRICE_2, dto1.getLineTotal());

        
        dto1.setQuantity(5);
        assertEquals(5, dto1.getQuantity());

        
        LineItemResponseDTO dto2 = LineItemResponseDTO.builder()
                .productId(3L).quantity(5).unitPrice(PRICE_1).lineTotal(PRICE_2).build();

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotNull(dto1.toString());
    }

    
    @Test
    void orderRequestDTO_ShouldCoverAllLombokMethods() {
        LineItemRequestDTO item = LineItemRequestDTO.builder().productId(1L).quantity(1).build();
        List<LineItemRequestDTO> items = List.of(item);

        OrderRequestDTO dto1 = OrderRequestDTO.builder()
                .customerId(10L).shippingAddressId(20L).items(items).build();

        assertNotNull(dto1);
        assertEquals(10L, dto1.getCustomerId());
        assertEquals(20L, dto1.getShippingAddressId());
        assertEquals(1, dto1.getItems().size());

        
        dto1.setCustomerId(11L);
        dto1.setItems(Collections.emptyList());
        assertEquals(11L, dto1.getCustomerId());
        assertTrue(dto1.getItems().isEmpty());

        
        OrderRequestDTO dto2 = OrderRequestDTO.builder()
                .customerId(11L).shippingAddressId(20L).items(Collections.emptyList()).build();

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotNull(dto1.toString());
    }

    
    @Test
    void orderResponseDTO_ShouldCoverAllLombokMethods() {
        LineItemResponseDTO item = LineItemResponseDTO.builder().productId(1L).build();
        List<LineItemResponseDTO> items = List.of(item);

        OrderResponseDTO dto1 = OrderResponseDTO.builder()
                .id(1L).orderDate(FIXED_TIME).status(OrderStatus.PAID).total(PRICE_1)
                .customerId(10L).shippingAddressId(20L).items(items).build();

        assertNotNull(dto1);
        assertEquals(1L, dto1.getId());
        assertEquals(OrderStatus.PAID, dto1.getStatus());
        assertEquals(PRICE_1, dto1.getTotal());
        assertEquals(FIXED_TIME, dto1.getOrderDate());

        
        dto1.setStatus(OrderStatus.SHIPPED);
        dto1.setTotal(PRICE_2);
        assertEquals(OrderStatus.SHIPPED, dto1.getStatus());
        assertEquals(PRICE_2, dto1.getTotal());

        
        OrderResponseDTO dto2 = OrderResponseDTO.builder()
                .id(1L).orderDate(FIXED_TIME).status(OrderStatus.SHIPPED).total(PRICE_2)
                .customerId(10L).shippingAddressId(20L).items(items).build();

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotNull(dto1.toString());
    }

    
    @Test
    void orderStatusUpdateDTO_ShouldCoverAllLombokMethods() {
        OrderStatusUpdateDTO dto1 = OrderStatusUpdateDTO.builder().status(OrderStatus.CANCELLED).build();

        assertNotNull(dto1);
        assertEquals(OrderStatus.CANCELLED, dto1.getStatus());

        
        dto1.setStatus(OrderStatus.CREATED);
        assertEquals(OrderStatus.CREATED, dto1.getStatus());

        
        OrderStatusUpdateDTO dto2 = OrderStatusUpdateDTO.builder().status(OrderStatus.CREATED).build();
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotNull(dto1.toString());
    }
}