package com.ais.proyecto_final.mappers;

import com.ais.proyecto_final.dto.product.ProductRequestDTO;
import com.ais.proyecto_final.dto.product.ProductResponseDTO;
import com.ais.proyecto_final.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ProductMapperTest {

    @Autowired
    private ProductMapper productMapper;

    private final ProductRequestDTO requestDTO = ProductRequestDTO.builder()
            .sku("SKU001")
            .name("Laptop Gaming")
            .description("Potente laptop para juegos")
            .price(new BigDecimal("1200.50"))
            .stock(10)
            .active(true)
            .build();

    private final Product productEntity = Product.builder()
            .id(1L)
            .sku("SKU002")
            .name("Monitor 4K")
            .description("Monitor de alta resolución")
            .price(new BigDecimal("450.00"))
            .stock(5)
            .active(false)
            .createdAt(LocalDateTime.now().minusDays(1))
            .updatedAt(LocalDateTime.now().minusDays(1))
            .build();

    /**
     * Prueba: Convertir de DTO de Solicitud a Entidad (Creación)
     */
    @Test
    void shouldMapRequestDtoToEntity() {
        Product entity = productMapper.dtoToEntity(requestDTO);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isNull(); 
        assertThat(entity.getSku()).isEqualTo(requestDTO.getSku());
        assertThat(entity.getName()).isEqualTo(requestDTO.getName());
        assertThat(entity.getPrice()).isEqualByComparingTo(requestDTO.getPrice());
        assertThat(entity.getCreatedAt()).isNull(); 
    }

    /**
     * Prueba: Convertir de Entidad a DTO de Respuesta
     */
    @Test
    void shouldMapEntityToResponseDto() {
        ProductResponseDTO responseDTO = productMapper.toResponseDto(productEntity);

        assertThat(responseDTO).isNotNull();
        assertThat(responseDTO.getId()).isEqualTo(productEntity.getId());
        assertThat(responseDTO.getSku()).isEqualTo(productEntity.getSku());
        assertThat(responseDTO.getName()).isEqualTo(productEntity.getName());
        assertThat(responseDTO.isActive()).isEqualTo(productEntity.isActive());
        assertThat(responseDTO.getCreatedAt()).isEqualTo(productEntity.getCreatedAt());
    }

    /**
     * Prueba: Actualizar Entidad existente desde DTO de Solicitud
     */
    @Test
    void shouldUpdateEntityFromDto() {
        Product existingProduct = productEntity;
        String newName = "Monitor 8K Pro";
        BigDecimal newPrice = new BigDecimal("800.00");

        ProductRequestDTO updateDto = ProductRequestDTO.builder()
                .sku("SKU_UPDATED") 
                .name(newName)
                .description("Descripción actualizada")
                .price(newPrice)
                .stock(20)
                .active(true)
                .build();

        productMapper.updateEntityFromDto(updateDto, existingProduct);

        
        assertThat(existingProduct.getName()).isEqualTo(newName);
        assertThat(existingProduct.getPrice()).isEqualByComparingTo(newPrice);
        assertThat(existingProduct.getStock()).isEqualTo(20);
        assertThat(existingProduct.isActive()).isTrue();

        
        assertThat(existingProduct.getId()).isEqualTo(productEntity.getId());
        assertThat(existingProduct.getCreatedAt()).isEqualTo(productEntity.getCreatedAt());
        
    }
}