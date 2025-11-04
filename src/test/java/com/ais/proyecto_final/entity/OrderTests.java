package com.ais.proyecto_final.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderTests {

    private final Customer mockCustomer = Customer.builder().id(1L).fullName("Mock").email("mock@test.com").build();
    private final Address mockAddress = Address.builder().id(1L).line1("Shipping").build();
    private final OrderItem mockItem = OrderItem.builder().id(1L).quantity(1).build();


    @Test
    void lombokAllArgsConstructorWorks() {
        LocalDateTime date = LocalDateTime.now();
        BigDecimal total = new BigDecimal("50.00");
        List<OrderItem> items = new ArrayList<>(List.of(mockItem));


        Order order = new Order(1L, mockCustomer, mockAddress, date, OrderStatus.PAID, total, items);

        assertEquals(1L, order.getId());
        assertEquals(mockCustomer, order.getCustomer());
        assertEquals(mockAddress, order.getShippingAddress());
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(total, order.getTotal());
        assertFalse(order.getItems().isEmpty());
    }

    @Test
    void lombokNoArgsConstructorAndSettersWorks() {
        Order order = new Order();
        LocalDateTime date = LocalDateTime.now();

        order.setId(2L);
        order.setCustomer(mockCustomer);
        order.setShippingAddress(mockAddress);
        order.setOrderDate(date);
        order.setStatus(OrderStatus.SHIPPED);
        order.setTotal(new BigDecimal("100.00"));
        order.setItems(new ArrayList<>());

        assertEquals(2L, order.getId());
        assertEquals(mockCustomer, order.getCustomer());
        assertEquals(mockAddress, order.getShippingAddress());
        assertEquals(date, order.getOrderDate());
        assertEquals(OrderStatus.SHIPPED, order.getStatus());


        assertNotNull(order.getItems());
        assertTrue(order.getItems().isEmpty());
    }

    @Test
    void lombokBuilderWorks() {
        Order order = Order.builder()
                .id(3L)

                .status(OrderStatus.CREATED)
                .build();

        assertEquals(3L, order.getId());
        assertEquals(OrderStatus.CREATED, order.getStatus());

        assertNotNull(order.getItems());
        assertTrue(order.getItems().isEmpty());
    }

    @Test
    void lombokEqualsAndHashCodeWorks_BasedOnId() {
        Order o1 = Order.builder().id(1L).total(BigDecimal.TEN).build();
        Order o2 = Order.builder().id(1L).total(BigDecimal.ONE).build();
        Order o3 = Order.builder().id(2L).total(BigDecimal.TEN).build();

        assertEquals(o1, o2);
        assertEquals(o1.hashCode(), o2.hashCode());

        assertNotEquals(o1, o3);
        assertNotEquals(o1.hashCode(), o3.hashCode());

        assertTrue(o1.equals(o1));
        assertFalse(o1.equals(null));
        assertFalse(o1.equals(new Object()));
    }

    @Test
    void toStringWorks() {
        Order order = Order.builder().id(1L).status(OrderStatus.CANCELLED).build();
        assertNotNull(order.toString());
    }

    @Test
    void builderToString() {
        assertNotNull(Order.builder().toString());
    }
}