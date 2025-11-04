package com.ais.proyecto_final.repository;

import com.ais.proyecto_final.entity.Address;
import com.ais.proyecto_final.entity.Customer;
import com.ais.proyecto_final.entity.Order;
import com.ais.proyecto_final.entity.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class OrderRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OrderRepository orderRepository;

    private Customer c1;
    private Customer c2;
    private Address a1;
    private Address a2;
    private Order o1_c1_created_yesterday;
    private Order o2_c1_shipped_today;
    private Order o3_c2_created_today;

    @BeforeEach
    void setUp() {
        c1 = Customer.builder().email("c1@test.com").fullName("C1").build();
        c2 = Customer.builder().email("c2@test.com").fullName("C2").build();
        entityManager.persist(c1);
        entityManager.persist(c2);

        a1 = Address.builder().customer(c1).line1("L1").city("C").postalCode("1").country("C").build();
        a2 = Address.builder().customer(c2).line1("L2").city("C").postalCode("2").country("C").build();
        entityManager.persist(a1);
        entityManager.persist(a2);

        LocalDateTime today = LocalDate.now().atTime(12, 0);
        LocalDateTime yesterday = LocalDate.now().minusDays(1).atTime(12, 0);

        o1_c1_created_yesterday = Order.builder()
                .customer(c1).shippingAddress(a1)
                .orderDate(yesterday)
                .status(OrderStatus.CREATED)
                .total(BigDecimal.TEN)
                .build();

        o2_c1_shipped_today = Order.builder()
                .customer(c1).shippingAddress(a1)
                .orderDate(today)
                .status(OrderStatus.SHIPPED)
                .total(BigDecimal.TEN)
                .build();

        o3_c2_created_today = Order.builder()
                .customer(c2).shippingAddress(a2)
                .orderDate(today)
                .status(OrderStatus.CREATED)
                .total(BigDecimal.TEN)
                .build();

        entityManager.persist(o1_c1_created_yesterday);
        entityManager.persist(o2_c1_shipped_today);
        entityManager.persist(o3_c2_created_today);
        entityManager.flush();
    }

    @Test
    void filterOrders_ByCustomerId() {
        Specification<Order> spec = OrderSpecification.filterOrders(c1.getId(), null, null, null);
        Page<Order> result = orderRepository.findAll(spec, PageRequest.of(0, 5));
        assertEquals(2, result.getTotalElements());
    }

    @Test
    void filterOrders_ByStatus() {
        Specification<Order> spec = OrderSpecification.filterOrders(null, null, null, OrderStatus.CREATED);
        Page<Order> result = orderRepository.findAll(spec, PageRequest.of(0, 5));
        assertEquals(2, result.getTotalElements());
    }

    @Test
    void filterOrders_ByFromDate() {
        LocalDate today = LocalDate.now();
        Specification<Order> spec = OrderSpecification.filterOrders(null, today, null, null);
        Page<Order> result = orderRepository.findAll(spec, PageRequest.of(0, 5));
        assertEquals(2, result.getTotalElements());
    }

    @Test
    void filterOrders_ByToDate() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        Specification<Order> spec = OrderSpecification.filterOrders(null, null, yesterday, null);
        Page<Order> result = orderRepository.findAll(spec, PageRequest.of(0, 5));
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void filterOrders_ByCustomerAndStatus() {
        Specification<Order> spec = OrderSpecification.filterOrders(c1.getId(), null, null, OrderStatus.SHIPPED);
        Page<Order> result = orderRepository.findAll(spec, PageRequest.of(0, 5));
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void filterOrders_NoResults() {
        Specification<Order> spec = OrderSpecification.filterOrders(c2.getId(), null, null, OrderStatus.SHIPPED);
        Page<Order> result = orderRepository.findAll(spec, PageRequest.of(0, 5));
        assertEquals(0, result.getTotalElements());
    }

    @Test
    void filterOrders_AllFilters() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        Specification<Order> spec = OrderSpecification.filterOrders(c1.getId(), yesterday, yesterday, OrderStatus.CREATED);
        Page<Order> result = orderRepository.findAll(spec, PageRequest.of(0, 5));
        assertEquals(1, result.getTotalElements());
    }
}