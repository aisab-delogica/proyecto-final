package com.ais.proyecto_final.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class OrderItemTests {

    
    private final Order mockOrder = Order.builder().id(1L).status(OrderStatus.PAID).build();
    private final Product mockProduct = Product.builder().id(1L).name("Mock Product").build();

    
    @Test
    void lombokAllArgsConstructorWorks() {
        BigDecimal price = new BigDecimal("25.99");
        OrderItem item = new OrderItem(1L, mockOrder, mockProduct, 5, price);

        assertEquals(1L, item.getId());
        assertEquals(mockOrder, item.getOrder());
        assertEquals(mockProduct, item.getProduct());
        assertEquals(5, item.getQuantity());
        assertEquals(price, item.getUnitPrice());
    }

    @Test
    void lombokNoArgsConstructorAndSettersWorks() {
        OrderItem item = new OrderItem();
        item.setId(2L);
        item.setOrder(mockOrder);
        item.setQuantity(10);
        item.setUnitPrice(new BigDecimal("1.00"));

        assertEquals(2L, item.getId());
        assertEquals(mockOrder, item.getOrder());
        assertEquals(10, item.getQuantity());
    }

    @Test
    void lombokBuilderWorks() {
        OrderItem item = OrderItem.builder()
                .id(3L)
                .product(mockProduct)
                .quantity(1)
                .build();

        assertEquals(3L, item.getId());
        assertEquals(mockProduct, item.getProduct());
        assertEquals(1, item.getQuantity());
    }

    @Test
    void lombokEqualsAndHashCodeWorks_BasedOnId() {
        OrderItem item1 = OrderItem.builder().id(1L).quantity(10).build();
        OrderItem item2 = OrderItem.builder().id(1L).quantity(5).build();
        OrderItem item3 = OrderItem.builder().id(2L).quantity(10).build();

        assertEquals(item1, item2);
        assertEquals(item1.hashCode(), item2.hashCode());
        assertNotEquals(item1, item3);
    }

    @Test
    void toStringWorks() {
        OrderItem item = OrderItem.builder().id(1L).quantity(1).build();
        assertNotNull(item.toString());
    }
}
