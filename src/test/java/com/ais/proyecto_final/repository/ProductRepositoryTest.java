package com.ais.proyecto_final.repository;

import com.ais.proyecto_final.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest // Configura H2 en memoria, escanea @Entity y Repositories
class ProductRepositoryTest {

    @Autowired
    private TestEntityManager entityManager; // Utilidad para insertar datos de prueba

    @Autowired
    private ProductRepository productRepository;

    private Product p1_active_laptop;
    private Product p2_active_teclado;
    private Product p3_inactive_monitor;

    @BeforeEach
    void setUp() {
        // Creamos y persistimos datos de prueba
        p1_active_laptop = Product.builder()
                .sku("SKU001")
                .name("Laptop Gamer")
                .price(new BigDecimal("1200.00"))
                .stock(10)
                .active(true)
                .build();
        entityManager.persist(p1_active_laptop);

        p2_active_teclado = Product.builder()
                .sku("SKU002")
                .name("Teclado Mecánico")
                .price(new BigDecimal("150.00"))
                .stock(50)
                .active(true)
                .build();
        entityManager.persist(p2_active_teclado);

        p3_inactive_monitor = Product.builder()
                .sku("SKU003")
                .name("Monitor Curvo (Inactivo)")
                .price(new BigDecimal("300.00"))
                .stock(5)
                .active(false) // Inactivo
                .build();
        entityManager.persist(p3_inactive_monitor);

        entityManager.flush(); // Sincroniza la BD
    }

    @Test
    void existsBySku_ShouldReturnTrue_WhenSkuExists() {
        assertTrue(productRepository.existsBySku("SKU001"));
    }

    @Test
    void existsBySku_ShouldReturnFalse_WhenSkuDoesNotExist() {
        assertFalse(productRepository.existsBySku("SKU-DOES-NOT-EXIST"));
    }

    @Test
    void findAll_ShouldFilterByNameAndActive() {
        // Caso 1: Buscar "Laptop" y activo=true (Debe encontrar 1)
        Specification<Product> specLaptop = ProductSpecification.nameContains("Laptop")
                .and(ProductSpecification.isActive(true));

        Page<Product> resultLaptop = productRepository.findAll(specLaptop, PageRequest.of(0, 10));
        assertEquals(1, resultLaptop.getTotalElements());
        assertEquals("SKU001", resultLaptop.getContent().get(0).getSku());

        // Caso 2: Buscar por nombre "o" (debería encontrar "Laptop", "Teclado" y "Monitor") y activo=null (todos)
        Specification<Product> specNameO = ProductSpecification.nameContains("o")
                .and(ProductSpecification.isActive(null));

        Page<Product> resultNameO = productRepository.findAll(specNameO, PageRequest.of(0, 10));
        // CORRECCIÓN: "Laptop", "Teclado" y "Monitor" contienen "o"
        assertEquals(3, resultNameO.getTotalElements());

        // Caso 3: Buscar por nombre "o" y activo=false (Debe encontrar 1)
        Specification<Product> specNameOInactive = ProductSpecification.nameContains("o")
                .and(ProductSpecification.isActive(false));

        Page<Product> resultNameOInactive = productRepository.findAll(specNameOInactive, PageRequest.of(0, 10));
        assertEquals(1, resultNameOInactive.getTotalElements());
        assertEquals("SKU003", resultNameOInactive.getContent().get(0).getSku());
    }
}