package com.ais.proyecto_final.dto;

import com.ais.proyecto_final.dto.product.ProductRequestDTO;
import com.ais.proyecto_final.dto.product.ProductResponseDTO;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ProductDtoTest {

    private final LocalDateTime FIXED_TIME = LocalDateTime.of(2025, 1, 1, 10, 0);
    private final BigDecimal PRICE_A = new BigDecimal("99.99");
    private final BigDecimal PRICE_B = new BigDecimal("199.99");

    
    @Test
    void productRequestDTO_ShouldCoverAllLombokMethods() {
        ProductRequestDTO dto1 = ProductRequestDTO.builder()
                .sku("SKU123").name("Product A").description("Desc A")
                .price(PRICE_A).stock(100).active(true).build();

        assertNotNull(dto1);
        assertEquals("SKU123", dto1.getSku());
        assertEquals("Product A", dto1.getName());
        assertEquals(PRICE_A, dto1.getPrice());
        assertTrue(dto1.getActive());

        
        dto1.setSku("SKU456");
        dto1.setPrice(PRICE_B);
        dto1.setActive(false);
        assertEquals("SKU456", dto1.getSku());
        assertEquals(PRICE_B, dto1.getPrice());
        assertFalse(dto1.getActive());

        
        ProductRequestDTO dto2 = ProductRequestDTO.builder()
                .sku("SKU456").name("Product A").description("Desc A")
                .price(PRICE_B).stock(100).active(false).build();

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotNull(dto1.toString());
    }

    
    @Test
    void productResponseDTO_ShouldCoverAllLombokMethods() {
        ProductResponseDTO dto1 = ProductResponseDTO.builder()
                .id(5L).sku("SKU1").name("Name 1").description("Desc 1")
                .price(PRICE_A).stock(50).active(true)
                .createdAt(FIXED_TIME).updatedAt(FIXED_TIME).build();

        assertNotNull(dto1);
        assertEquals(5L, dto1.getId());
        assertEquals("SKU1", dto1.getSku());
        assertEquals(PRICE_A, dto1.getPrice());
        assertEquals(FIXED_TIME, dto1.getCreatedAt());
        assertTrue(dto1.isActive());

        
        dto1.setActive(false);
        dto1.setStock(0);
        assertFalse(dto1.isActive());
        assertEquals(0, dto1.getStock());

        
        ProductResponseDTO dto2 = ProductResponseDTO.builder()
                .id(5L).sku("SKU1").name("Name 1").description("Desc 1")
                .price(PRICE_A).stock(0).active(false)
                .createdAt(FIXED_TIME).updatedAt(FIXED_TIME).build();

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotNull(dto1.toString());
    }
}