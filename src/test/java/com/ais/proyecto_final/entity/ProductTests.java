package com.ais.proyecto_final.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ProductTests {


    @Test
    void lombokAllArgsConstructorWorks() {
        LocalDateTime now = LocalDateTime.now();
        BigDecimal price = new BigDecimal("10.50");
        Product product = new Product(1L, "SKU123", "Product A", "Description A", price, 100, true, now, now);

        assertEquals(1L, product.getId());
        assertEquals("SKU123", product.getSku());
        assertEquals("Product A", product.getName());
        assertEquals(price, product.getPrice());
        assertEquals(100, product.getStock());
        assertTrue(product.isActive());
        assertEquals(now, product.getCreatedAt());
        assertEquals(now, product.getUpdatedAt());
    }

    @Test
    void lombokNoArgsConstructorAndSettersWorks() {
        Product product = new Product();
        LocalDateTime time = LocalDateTime.now();

        product.setId(2L);
        product.setSku("SKU456");
        product.setStock(50);
        product.setActive(false);
        product.setPrice(new BigDecimal("20.00"));
        product.setCreatedAt(time);
        product.setUpdatedAt(time);


        assertEquals(2L, product.getId());
        assertEquals("SKU456", product.getSku());
        assertEquals(50, product.getStock());
        assertFalse(product.isActive());
        assertEquals(time, product.getCreatedAt());
        assertEquals(time, product.getUpdatedAt());
    }

    @Test
    void lombokBuilderWorks() {
        Product product = Product.builder()
                .id(3L)
                .name("Builder Product")
                .stock(5)
                .build();

        assertEquals(3L, product.getId());
        assertEquals("Builder Product", product.getName());
        assertEquals(5, product.getStock());
    }

    @Test
    void lombokEqualsAndHashCodeWorks_BasedOnId() {
        Product p1 = Product.builder().id(1L).sku("A").build();
        Product p2 = Product.builder().id(1L).sku("B").build();
        Product p3 = Product.builder().id(2L).sku("A").build();

        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());
        assertNotEquals(p1, p3);
        assertNotEquals(p1.hashCode(), p3.hashCode());


        assertTrue(p1.equals(p1));

        assertFalse(p1.equals(null));

        assertFalse(p1.equals(new Object()));
    }


    @Test
    void prePersistSetsCreatedAtAndUpdatedAt() {
        Product product = new Product();
        product.onCreate();
        assertNotNull(product.getCreatedAt());
        assertEquals(product.getCreatedAt(), product.getUpdatedAt());
    }

    @Test
    void preUpdateUpdatesUpdatedAt() throws InterruptedException {
        Product product = new Product();
        product.onCreate();
        LocalDateTime initialUpdatedAt = product.getUpdatedAt();

        Thread.sleep(10);
        product.onUpdate();

        assertNotNull(product.getUpdatedAt());
        assertTrue(product.getUpdatedAt().isAfter(initialUpdatedAt));
    }

    @Test
    void toStringWorks() {
        Product product = Product.builder().id(1L).name("Test").build();
        assertNotNull(product.toString());
    }

    @Test
    void builderToString() {
        assertNotNull(Product.builder().toString());
    }
}