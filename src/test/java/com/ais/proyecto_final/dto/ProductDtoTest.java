package com.ais.proyecto_final.dto;

import com.ais.proyecto_final.dto.product.ProductRequestDTO;
import com.ais.proyecto_final.dto.product.ProductResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ProductDtoTest {

    private final LocalDateTime FIXED_TIME_A = LocalDateTime.of(2025, 1, 1, 10, 0);
    private final LocalDateTime FIXED_TIME_B = LocalDateTime.of(2025, 1, 2, 10, 0);
    private final BigDecimal PRICE_A = new BigDecimal("99.99");
    private final BigDecimal PRICE_B = new BigDecimal("199.99");

    private ProductRequestDTO requestDto1;
    private ProductRequestDTO requestDto2;
    private ProductResponseDTO responseDto1;
    private ProductResponseDTO responseDto2;

    @BeforeEach
    void setUp() {
        requestDto1 = ProductRequestDTO.builder()
                .sku("SKU123").name("Product A").description("Desc A")
                .price(PRICE_A).stock(100).active(true).build();

        requestDto2 = ProductRequestDTO.builder()
                .sku("SKU123").name("Product A").description("Desc A")
                .price(PRICE_A).stock(100).active(true).build();

        responseDto1 = ProductResponseDTO.builder()
                .id(1L).sku("SKU1").name("Name 1").description("Desc 1")
                .price(PRICE_A).stock(50).active(true)
                .createdAt(FIXED_TIME_A).updatedAt(FIXED_TIME_A).build();

        responseDto2 = ProductResponseDTO.builder()
                .id(1L).sku("SKU1").name("Name 1").description("Desc 1")
                .price(PRICE_A).stock(50).active(true)
                .createdAt(FIXED_TIME_A).updatedAt(FIXED_TIME_A).build();
    }


    @Test
    void productRequestDTO_EqualsAndHashCodeContract() {
        assertEquals(requestDto1, requestDto2, "Dos DTOs idénticos deben ser iguales");
        assertEquals(requestDto1.hashCode(), requestDto2.hashCode(), "Dos DTOs idénticos deben tener el mismo hashCode");

        assertNotEquals(null, requestDto1, "El DTO no debe ser igual a null");
        assertNotEquals(new Object(), requestDto1, "El DTO no debe ser igual a un objeto de otra clase");
        assertEquals(requestDto1, requestDto1, "El DTO debe ser igual a sí mismo");

        assertNotEquals(requestDto1, ProductRequestDTO.builder()
                .sku("SKU_DIFF").name("Product A").description("Desc A")
                .price(PRICE_A).stock(100).active(true).build());

        assertNotEquals(requestDto1, ProductRequestDTO.builder()
                .sku("SKU123").name("NAME_DIFF").description("Desc A")
                .price(PRICE_A).stock(100).active(true).build());

        assertNotEquals(requestDto1, ProductRequestDTO.builder()
                .sku("SKU123").name("Product A").description("DESC_DIFF")
                .price(PRICE_A).stock(100).active(true).build());

        assertNotEquals(requestDto1, ProductRequestDTO.builder()
                .sku("SKU123").name("Product A").description("Desc A")
                .price(PRICE_B).stock(100).active(true).build());

        assertNotEquals(requestDto1, ProductRequestDTO.builder()
                .sku("SKU123").name("Product A").description("Desc A")
                .price(PRICE_A).stock(999).active(true).build());

        assertNotEquals(requestDto1, ProductRequestDTO.builder()
                .sku("SKU123").name("Product A").description("Desc A")
                .price(PRICE_A).stock(100).active(false).build());
    }

    @Test
    void productRequestDTO_GettersSettersAndToString() {

        requestDto1.setSku("SKU456");
        assertEquals("SKU456", requestDto1.getSku());

        requestDto1.setName("Product B");
        assertEquals("Product B", requestDto1.getName());

        requestDto1.setDescription("Desc B");
        assertEquals("Desc B", requestDto1.getDescription());

        requestDto1.setPrice(PRICE_B);
        assertEquals(PRICE_B, requestDto1.getPrice());

        requestDto1.setStock(50);
        assertEquals(50, requestDto1.getStock());

        requestDto1.setActive(false);
        assertFalse(requestDto1.getActive());

        assertNotNull(requestDto1.toString());
    }

    @Test
    void productRequestDTOBuilder_ToString() {
        assertNotNull(ProductRequestDTO.builder().name("Test").toString());
    }

    @Test
    void productResponseDTO_EqualsAndHashCodeContract() {
        assertEquals(responseDto1, responseDto2, "Dos DTOs idénticos deben ser iguales");
        assertEquals(responseDto1.hashCode(), responseDto2.hashCode(), "Dos DTOs idénticos deben tener el mismo hashCode");

        assertNotEquals(null, responseDto1, "El DTO no debe ser igual a null");
        assertNotEquals(new Object(), responseDto1, "El DTO no debe ser igual a un objeto de otra clase");
        assertEquals(responseDto1, responseDto1, "El DTO debe ser igual a sí mismo");

        assertNotEquals(responseDto1, ProductResponseDTO.builder()
                .id(99L).sku("SKU1").name("Name 1").description("Desc 1")
                .price(PRICE_A).stock(50).active(true)
                .createdAt(FIXED_TIME_A).updatedAt(FIXED_TIME_A).build());

        assertNotEquals(responseDto1, ProductResponseDTO.builder()
                .id(1L).sku("SKU_DIFF").name("Name 1").description("Desc 1")
                .price(PRICE_A).stock(50).active(true)
                .createdAt(FIXED_TIME_A).updatedAt(FIXED_TIME_A).build());

        assertNotEquals(responseDto1, ProductResponseDTO.builder()
                .id(1L).sku("SKU1").name("NAME_DIFF").description("Desc 1")
                .price(PRICE_A).stock(50).active(true)
                .createdAt(FIXED_TIME_A).updatedAt(FIXED_TIME_A).build());

        assertNotEquals(responseDto1, ProductResponseDTO.builder()
                .id(1L).sku("SKU1").name("Name 1").description("DESC_DIFF")
                .price(PRICE_A).stock(50).active(true)
                .createdAt(FIXED_TIME_A).updatedAt(FIXED_TIME_A).build());

        assertNotEquals(responseDto1, ProductResponseDTO.builder()
                .id(1L).sku("SKU1").name("Name 1").description("Desc 1")
                .price(PRICE_B).stock(50).active(true)
                .createdAt(FIXED_TIME_A).updatedAt(FIXED_TIME_A).build());

        assertNotEquals(responseDto1, ProductResponseDTO.builder()
                .id(1L).sku("SKU1").name("Name 1").description("Desc 1")
                .price(PRICE_A).stock(999).active(true)
                .createdAt(FIXED_TIME_A).updatedAt(FIXED_TIME_A).build());

        assertNotEquals(responseDto1, ProductResponseDTO.builder()
                .id(1L).sku("SKU1").name("Name 1").description("Desc 1")
                .price(PRICE_A).stock(50).active(false)
                .createdAt(FIXED_TIME_A).updatedAt(FIXED_TIME_A).build());

        assertNotEquals(responseDto1, ProductResponseDTO.builder()
                .id(1L).sku("SKU1").name("Name 1").description("Desc 1")
                .price(PRICE_A).stock(50).active(true)
                .createdAt(FIXED_TIME_B).updatedAt(FIXED_TIME_A).build());

        assertNotEquals(responseDto1, ProductResponseDTO.builder()
                .id(1L).sku("SKU1").name("Name 1").description("Desc 1")
                .price(PRICE_A).stock(50).active(true)
                .createdAt(FIXED_TIME_A).updatedAt(FIXED_TIME_B).build());
    }

    @Test
    void productResponseDTO_GettersSettersAndToString() {
        responseDto1.setId(2L);
        assertEquals(2L, responseDto1.getId());

        responseDto1.setSku("SKU_NEW");
        assertEquals("SKU_NEW", responseDto1.getSku());

        responseDto1.setName("Name 2");
        assertEquals("Name 2", responseDto1.getName());

        responseDto1.setDescription("Desc 2");
        assertEquals("Desc 2", responseDto1.getDescription());

        responseDto1.setPrice(PRICE_B);
        assertEquals(PRICE_B, responseDto1.getPrice());

        responseDto1.setStock(0);
        assertEquals(0, responseDto1.getStock());

        responseDto1.setActive(false);
        assertFalse(responseDto1.isActive());

        responseDto1.setCreatedAt(FIXED_TIME_B);
        assertEquals(FIXED_TIME_B, responseDto1.getCreatedAt());

        responseDto1.setUpdatedAt(FIXED_TIME_B);
        assertEquals(FIXED_TIME_B, responseDto1.getUpdatedAt());

        assertNotNull(responseDto1.toString());
    }

    @Test
    void productResponseDTOBuilder_ToString() {
        assertNotNull(ProductResponseDTO.builder().name("Test").toString());
    }
}