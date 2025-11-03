package com.ais.proyecto_final.mappers;

import com.ais.proyecto_final.dto.order.LineItemRequestDTO;
import com.ais.proyecto_final.dto.order.LineItemResponseDTO;
import com.ais.proyecto_final.dto.order.OrderRequestDTO;
import com.ais.proyecto_final.dto.order.OrderResponseDTO;
import com.ais.proyecto_final.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderMapperTest {

    
    
    @InjectMocks
    private OrderMapperImpl orderMapper;

    @Mock
    private OrderItemMapper orderItemMapper;

    private final Long CUSTOMER_ID = 1L;
    private final Long ADDRESS_ID = 5L;
    private final Long PRODUCT_ID = 10L;

    private final BigDecimal TOTAL_PRICE = new BigDecimal("100.00");
    private final LocalDateTime ORDER_DATE = LocalDateTime.now();


    @BeforeEach
    void setUp() {
    }

    @Test
    void toEntity_ShouldMapOrderRequestDTOToOrderEntity() {
        LineItemRequestDTO itemRequest = LineItemRequestDTO.builder().productId(PRODUCT_ID).quantity(2).build();
        OrderRequestDTO requestDTO = OrderRequestDTO.builder()
                .customerId(CUSTOMER_ID)
                .shippingAddressId(ADDRESS_ID)
                .items(List.of(itemRequest))
                .build();

        when(orderItemMapper.toEntity(any(LineItemRequestDTO.class))).thenReturn(new OrderItem());

        Order orderEntity = orderMapper.toEntity(requestDTO);

        assertNotNull(orderEntity);
        assertNull(orderEntity.getTotal());

        assertNotNull(orderEntity.getItems());
        assertEquals(1, orderEntity.getItems().size());
    }

    @Test
    void toResponseDto_ShouldMapOrderEntityToOrderResponseDTO() {
        Customer customer = Customer.builder().id(CUSTOMER_ID).build();
        Address address = Address.builder().id(ADDRESS_ID).build();
        OrderItem item = OrderItem.builder().id(20L).quantity(2).build();

        Order orderEntity = Order.builder()
                .id(1L)
                .customer(customer)
                .shippingAddress(address)
                .orderDate(ORDER_DATE)
                .status(OrderStatus.PAID)
                .total(TOTAL_PRICE)
                .items(List.of(item))
                .build();

        when(orderItemMapper.toResponseDto(any(OrderItem.class))).thenReturn(LineItemResponseDTO.builder().productId(PRODUCT_ID).build());

        OrderResponseDTO responseDTO = orderMapper.toResponseDto(orderEntity);

        assertNotNull(responseDTO);
        assertEquals(1L, responseDTO.getId());
        assertEquals(CUSTOMER_ID, responseDTO.getCustomerId());
        assertEquals(ADDRESS_ID, responseDTO.getShippingAddressId());
        assertEquals(TOTAL_PRICE, responseDTO.getTotal());
        assertEquals(OrderStatus.PAID, responseDTO.getStatus());

        assertNotNull(responseDTO.getItems());
        assertEquals(1, responseDTO.getItems().size());
        assertEquals(PRODUCT_ID, responseDTO.getItems().get(0).getProductId());
    }
}