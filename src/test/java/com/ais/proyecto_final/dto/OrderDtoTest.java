package com.ais.proyecto_final.dto;

import com.ais.proyecto_final.dto.order.*;
import com.ais.proyecto_final.entity.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class OrderDtoTest {

    private final LocalDateTime FIXED_TIME = LocalDateTime.of(2025, 1, 1, 10, 0);
    private final BigDecimal PRICE_A = new BigDecimal("10.00");
    private final BigDecimal PRICE_B = new BigDecimal("20.00");
    private final List<LineItemRequestDTO> reqItems = List.of(LineItemRequestDTO.builder().productId(1L).quantity(1).build());
    private final List<LineItemResponseDTO> resItems = List.of(LineItemResponseDTO.builder().productId(1L).build());

    // --- Instancias de prueba ---
    private LineItemRequestDTO lineReq1;
    private LineItemRequestDTO lineReq2;
    private LineItemResponseDTO lineRes1;
    private LineItemResponseDTO lineRes2;
    private OrderRequestDTO orderReq1;
    private OrderRequestDTO orderReq2;
    private OrderResponseDTO orderRes1;
    private OrderResponseDTO orderRes2;
    private OrderStatusUpdateDTO statusUpdate1;
    private OrderStatusUpdateDTO statusUpdate2;

    @BeforeEach
    void setUp() {
        lineReq1 = LineItemRequestDTO.builder().productId(1L).quantity(5).build();
        lineReq2 = LineItemRequestDTO.builder().productId(1L).quantity(5).build();

        lineRes1 = LineItemResponseDTO.builder().productId(3L).quantity(2).unitPrice(PRICE_A).lineTotal(PRICE_B).build();
        lineRes2 = LineItemResponseDTO.builder().productId(3L).quantity(2).unitPrice(PRICE_A).lineTotal(PRICE_B).build();

        orderReq1 = OrderRequestDTO.builder().customerId(10L).shippingAddressId(20L).items(reqItems).build();
        orderReq2 = OrderRequestDTO.builder().customerId(10L).shippingAddressId(20L).items(reqItems).build();

        orderRes1 = OrderResponseDTO.builder()
                .id(1L).orderDate(FIXED_TIME).status(OrderStatus.PAID).total(PRICE_A)
                .customerId(10L).shippingAddressId(20L).items(resItems).build();
        orderRes2 = OrderResponseDTO.builder()
                .id(1L).orderDate(FIXED_TIME).status(OrderStatus.PAID).total(PRICE_A)
                .customerId(10L).shippingAddressId(20L).items(resItems).build();

        statusUpdate1 = OrderStatusUpdateDTO.builder().status(OrderStatus.CANCELLED).build();
        statusUpdate2 = OrderStatusUpdateDTO.builder().status(OrderStatus.CANCELLED).build();
    }

    // --- 1. LineItemRequestDTO ---

    @Test
    void lineItemRequestDTO_EqualsAndHashCodeContract() {
        assertEquals(lineReq1, lineReq2);
        assertEquals(lineReq1.hashCode(), lineReq2.hashCode());

        assertNotEquals(lineReq1, null);
        assertNotEquals(lineReq1, new Object());
        assertEquals(lineReq1, lineReq1);

        assertNotEquals(lineReq1, LineItemRequestDTO.builder().productId(99L).quantity(5).build());
        assertNotEquals(lineReq1, LineItemRequestDTO.builder().productId(1L).quantity(99).build());
    }

    @Test
    void lineItemRequestDTO_GettersSettersAndToString() {
        lineReq1.setProductId(2L);
        lineReq1.setQuantity(10);
        assertEquals(2L, lineReq1.getProductId());
        assertEquals(10, lineReq1.getQuantity());
        assertNotNull(lineReq1.toString());
    }

    @Test
    void lineItemRequestDTOBuilder_ToString() {
        assertNotNull(LineItemRequestDTO.builder().toString());
    }

    // --- 2. LineItemResponseDTO ---

    @Test
    void lineItemResponseDTO_EqualsAndHashCodeContract() {
        assertEquals(lineRes1, lineRes2);
        assertEquals(lineRes1.hashCode(), lineRes2.hashCode());

        assertNotEquals(lineRes1, null);
        assertNotEquals(lineRes1, new Object());
        assertEquals(lineRes1, lineRes1);

        assertNotEquals(lineRes1, LineItemResponseDTO.builder().productId(99L).quantity(2).unitPrice(PRICE_A).lineTotal(PRICE_B).build());
        assertNotEquals(lineRes1, LineItemResponseDTO.builder().productId(3L).quantity(99).unitPrice(PRICE_A).lineTotal(PRICE_B).build());
        assertNotEquals(lineRes1, LineItemResponseDTO.builder().productId(3L).quantity(2).unitPrice(PRICE_B).lineTotal(PRICE_B).build());
        assertNotEquals(lineRes1, LineItemResponseDTO.builder().productId(3L).quantity(2).unitPrice(PRICE_A).lineTotal(PRICE_A).build());
    }

    @Test
    void lineItemResponseDTO_GettersSettersAndToString() {
        lineRes1.setProductId(1L);
        lineRes1.setQuantity(5);
        lineRes1.setUnitPrice(BigDecimal.ONE);
        lineRes1.setLineTotal(BigDecimal.TEN);

        assertEquals(1L, lineRes1.getProductId());
        assertEquals(5, lineRes1.getQuantity());
        assertEquals(BigDecimal.ONE, lineRes1.getUnitPrice());
        assertEquals(BigDecimal.TEN, lineRes1.getLineTotal());
        assertNotNull(lineRes1.toString());
    }

    @Test
    void lineItemResponseDTOBuilder_ToString() {
        assertNotNull(LineItemResponseDTO.builder().toString());
    }

    // --- 3. OrderRequestDTO ---

    @Test
    void orderRequestDTO_EqualsAndHashCodeContract() {
        assertEquals(orderReq1, orderReq2);
        assertEquals(orderReq1.hashCode(), orderReq2.hashCode());

        assertNotEquals(orderReq1, null);
        assertNotEquals(orderReq1, new Object());
        assertEquals(orderReq1, orderReq1);

        assertNotEquals(orderReq1, OrderRequestDTO.builder().customerId(99L).shippingAddressId(20L).items(reqItems).build());
        assertNotEquals(orderReq1, OrderRequestDTO.builder().customerId(10L).shippingAddressId(99L).items(reqItems).build());
        assertNotEquals(orderReq1, OrderRequestDTO.builder().customerId(10L).shippingAddressId(20L).items(Collections.emptyList()).build());
    }

    @Test
    void orderRequestDTO_GettersSettersAndToString() {
        orderReq1.setCustomerId(11L);
        orderReq1.setShippingAddressId(22L);
        orderReq1.setItems(Collections.emptyList());

        assertEquals(11L, orderReq1.getCustomerId());
        assertEquals(22L, orderReq1.getShippingAddressId());
        assertTrue(orderReq1.getItems().isEmpty());
        assertNotNull(orderReq1.toString());
    }

    @Test
    void orderRequestDTOBuilder_ToString() {
        assertNotNull(OrderRequestDTO.builder().toString());
    }

    // --- 4. OrderResponseDTO ---

    @Test
    void orderResponseDTO_EqualsAndHashCodeContract() {
        assertEquals(orderRes1, orderRes2);
        assertEquals(orderRes1.hashCode(), orderRes2.hashCode());

        assertNotEquals(orderRes1, null);
        assertNotEquals(orderRes1, new Object());
        assertEquals(orderRes1, orderRes1);

        assertNotEquals(orderRes1, OrderResponseDTO.builder().id(99L).orderDate(FIXED_TIME).status(OrderStatus.PAID).total(PRICE_A).customerId(10L).shippingAddressId(20L).items(resItems).build());
        assertNotEquals(orderRes1, OrderResponseDTO.builder().id(1L).orderDate(LocalDateTime.now()).status(OrderStatus.PAID).total(PRICE_A).customerId(10L).shippingAddressId(20L).items(resItems).build());
        assertNotEquals(orderRes1, OrderResponseDTO.builder().id(1L).orderDate(FIXED_TIME).status(OrderStatus.CREATED).total(PRICE_A).customerId(10L).shippingAddressId(20L).items(resItems).build());
        assertNotEquals(orderRes1, OrderResponseDTO.builder().id(1L).orderDate(FIXED_TIME).status(OrderStatus.PAID).total(PRICE_B).customerId(10L).shippingAddressId(20L).items(resItems).build());
        assertNotEquals(orderRes1, OrderResponseDTO.builder().id(1L).orderDate(FIXED_TIME).status(OrderStatus.PAID).total(PRICE_A).customerId(99L).shippingAddressId(20L).items(resItems).build());
        assertNotEquals(orderRes1, OrderResponseDTO.builder().id(1L).orderDate(FIXED_TIME).status(OrderStatus.PAID).total(PRICE_A).customerId(10L).shippingAddressId(99L).items(resItems).build());
        assertNotEquals(orderRes1, OrderResponseDTO.builder().id(1L).orderDate(FIXED_TIME).status(OrderStatus.PAID).total(PRICE_A).customerId(10L).shippingAddressId(20L).items(Collections.emptyList()).build());
    }

    @Test
    void orderResponseDTO_GettersSettersAndToString() {
        orderRes1.setId(2L);
        orderRes1.setOrderDate(LocalDateTime.now());
        orderRes1.setStatus(OrderStatus.SHIPPED);
        orderRes1.setTotal(PRICE_B);
        orderRes1.setCustomerId(2L);
        orderRes1.setShippingAddressId(3L);
        orderRes1.setItems(Collections.emptyList());

        assertEquals(2L, orderRes1.getId());
        assertEquals(OrderStatus.SHIPPED, orderRes1.getStatus());
        assertEquals(PRICE_B, orderRes1.getTotal());
        assertEquals(2L, orderRes1.getCustomerId());
        assertEquals(3L, orderRes1.getShippingAddressId());
        assertTrue(orderRes1.getItems().isEmpty());
        assertNotNull(orderRes1.toString());
    }

    @Test
    void orderResponseDTOBuilder_ToString() {
        assertNotNull(OrderResponseDTO.builder().toString());
    }

    // --- 5. OrderStatusUpdateDTO ---

    @Test
    void orderStatusUpdateDTO_EqualsAndHashCodeContract() {
        assertEquals(statusUpdate1, statusUpdate2);
        assertEquals(statusUpdate1.hashCode(), statusUpdate2.hashCode());

        assertNotEquals(statusUpdate1, null);
        assertNotEquals(statusUpdate1, new Object());
        assertEquals(statusUpdate1, statusUpdate1);

        assertNotEquals(statusUpdate1, OrderStatusUpdateDTO.builder().status(OrderStatus.CREATED).build());
    }

    @Test
    void orderStatusUpdateDTO_GettersSettersAndToString() {
        statusUpdate1.setStatus(OrderStatus.CREATED);
        assertEquals(OrderStatus.CREATED, statusUpdate1.getStatus());
        assertNotNull(statusUpdate1.toString());
    }

    @Test
    void orderStatusUpdateDTOBuilder_ToString() {
        assertNotNull(OrderStatusUpdateDTO.builder().toString());
    }

    @Test
    void orderStatusUpdateDTO_NoArgsConstructor() {
        // Este test es para cubrir el constructor vacío que necesita Jackson
        // y que tuviste que añadir para arreglar el error 500
        OrderStatusUpdateDTO dto = new OrderStatusUpdateDTO();
        assertNotNull(dto);
        assertNull(dto.getStatus());
    }
}