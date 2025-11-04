package com.ais.proyecto_final.repository;

import com.ais.proyecto_final.entity.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CustomerRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        Customer c1 = Customer.builder()
                .fullName("Ana García")
                .email("ana.garcia@test.com")
                .phone("111222")
                .build();
        entityManager.persist(c1);

        Customer c2 = Customer.builder()
                .fullName("Juan Perez")
                .email("juan.perez@test.com")
                .phone("333444")
                .build();
        entityManager.persist(c2);

        entityManager.flush();
    }

    @Test
    void existsByEmail_ShouldReturnTrue_WhenEmailExists() {
        assertTrue(customerRepository.existsByEmail("ana.garcia@test.com"));
    }

    @Test
    void existsByEmail_ShouldReturnFalse_WhenEmailDoesNotExist() {
        assertFalse(customerRepository.existsByEmail("nobody@test.com"));
    }

    @Test
    void findByEmailContainingIgnoreCase_ShouldReturnMatchingCustomers() {
        Pageable pageable = PageRequest.of(0, 10);

        // Caso 1: Búsqueda exacta (pero mayúsculas)
        Page<Customer> resultAna = customerRepository.findByEmailContainingIgnoreCase("ANA.GARCIA", pageable);
        assertEquals(1, resultAna.getTotalElements());
        assertEquals("Ana García", resultAna.getContent().get(0).getFullName());

        // Caso 2: Búsqueda parcial
        Page<Customer> resultTest = customerRepository.findByEmailContainingIgnoreCase("@test.com", pageable);
        assertEquals(2, resultTest.getTotalElements());

        // Caso 3: Búsqueda parcial "an"
        Page<Customer> resultAn = customerRepository.findByEmailContainingIgnoreCase("an", pageable);
        // CORRECCIÓN: "ana.garcia" y "juan.perez" contienen "an"
        assertEquals(2, resultAn.getTotalElements());

        // Caso 4: Sin resultados
        Page<Customer> resultNone = customerRepository.findByEmailContainingIgnoreCase("xyz", pageable);
        assertEquals(0, resultNone.getTotalElements());
    }
}