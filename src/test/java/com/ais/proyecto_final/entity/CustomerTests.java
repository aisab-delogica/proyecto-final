package com.ais.proyecto_final.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class CustomerTests {

    @Test
    void prePersistSetsCreatedAtAndUpdatedAt() {
        Customer customer = new Customer();
        customer.onCreate();
        assertNotNull(customer.getCreatedAt());
        assertEquals(customer.getCreatedAt(), customer.getUpdatedAt());
    }

    @Test
    void preUpdateUpdatesUpdatedAt() throws InterruptedException {
        Customer customer = new Customer();
        customer.onCreate();
        LocalDateTime initialUpdatedAt = customer.getUpdatedAt();

        Thread.sleep(10);
        customer.onUpdate();

        assertNotNull(customer.getUpdatedAt());
        assertTrue(customer.getUpdatedAt().isAfter(initialUpdatedAt));
    }

    @Test
    void lombokBuilderAndGettersWorks() {
        Customer customer = Customer.builder()
                .id(1L)
                .fullName("Ana López")
                .email("ana.lopez@example.com")
                .phone("987654321")
                .addresses(Collections.emptyList())
                .build();

        assertEquals(1L, customer.getId());
        assertEquals("Ana López", customer.getFullName());

        customer.setFullName("Ana G. López");
        assertEquals("Ana G. López", customer.getFullName());
    }

    @Test
    void testSetters() {
        Customer customer = new Customer();
        LocalDateTime time = LocalDateTime.now();
        List<Address> addresses = new ArrayList<>();

        customer.setId(10L);
        customer.setAddresses(addresses);
        customer.setCreatedAt(time);
        customer.setUpdatedAt(time);

        assertEquals(10L, customer.getId());
        assertEquals(addresses, customer.getAddresses());
        assertEquals(time, customer.getCreatedAt());
        assertEquals(time, customer.getUpdatedAt());
    }

    @Test
    void lombokAllArgsConstructorWorks() {
        LocalDateTime time = LocalDateTime.now();
        List<Address> addresses = new ArrayList<>();
        Customer customer = new Customer(1L, "All Args", "all@args.com", "55555", addresses, time, time);
        assertEquals("All Args", customer.getFullName());
        assertNotNull(customer.getAddresses());
    }

    @Test
    void lombokNoArgsConstructorWorks() {
        Customer customer = new Customer();
        assertNotNull(customer);
    }

    @Test
    void lombokEqualsAndHashCodeWorks_BasedOnId() {
        Customer customer1 = Customer.builder().id(1L).email("a@b.com").fullName("A").build();
        Customer customer2 = Customer.builder().id(1L).email("x@y.com").fullName("X").build();
        Customer customer3 = Customer.builder().id(2L).email("a@b.com").fullName("A").build();
        Customer customer4 = Customer.builder().id(1L).email("a@b.com").fullName("A").build();


        assertEquals(customer1, customer2);
        assertEquals(customer1.hashCode(), customer2.hashCode());


        assertNotEquals(customer1, customer3);
        assertNotEquals(customer1.hashCode(), customer3.hashCode());


        assertTrue(customer1.equals(customer1));


        assertFalse(customer1.equals(null));


        assertFalse(customer1.equals(new Object()));


        assertEquals(customer1, customer4);
    }

    @Test
    void lombokToStringCallDoesNotThrow() {
        Customer customer = Customer.builder()
                .id(1L)
                .fullName("Test ToString")
                .email("to@string.com")
                .build();


        assertNotNull(customer.toString());
    }

    @Test
    void builderToString() {
        assertNotNull(Customer.builder().toString());
    }

    @Test
    void addressesListIsInitializedByBuilderDefault() {
        Customer customer = Customer.builder().build();
        assertNotNull(customer.getAddresses());
        assertTrue(customer.getAddresses().isEmpty());
    }

    @Test
    void addressesListIsInitializedByNoArgsConstructor() {
        Customer customer = new Customer();
        assertNotNull(customer.getAddresses());
        assertTrue(customer.getAddresses().isEmpty());
    }
}