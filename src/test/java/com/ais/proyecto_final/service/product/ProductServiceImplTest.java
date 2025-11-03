package com.ais.proyecto_final.service.product;


import com.ais.proyecto_final.dto.product.ProductRequestDTO;
import com.ais.proyecto_final.dto.product.ProductResponseDTO;
import com.ais.proyecto_final.entity.Product;
import com.ais.proyecto_final.exceptions.DuplicateResourceException;
import com.ais.proyecto_final.mappers.ProductMapper;
import com.ais.proyecto_final.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    private ProductRequestDTO productRequestDTO;
    private Product productEntity;
    private ProductResponseDTO productResponseDTO;

    @BeforeEach
    void setUp() {
        productRequestDTO = ProductRequestDTO.builder()
                .name("Test Product")
                .sku("SKU-001")
                .price(new BigDecimal("10.00"))
                .stock(5)
                .build();

        productEntity = Product.builder()
                .id(1L)
                .name("Test Product")
                .sku("SKU-001")
                .price(new BigDecimal("10.00"))
                .stock(5)
                .active(true)
                .build();

        productResponseDTO = ProductResponseDTO.builder()
                .id(1L)
                .name("Test Product")
                .sku("SKU-001")
                .active(true)
                .build();
    }


    @Test
    void createProduct_ShouldSaveAndReturnProduct_Success() {
        when(productRepository.existsBySku(productRequestDTO.getSku())).thenReturn(false);
        when(productMapper.dtoToEntity(any(ProductRequestDTO.class))).thenReturn(productEntity);
        when(productRepository.save(any(Product.class))).thenReturn(productEntity);
        when(productMapper.toResponseDto(any(Product.class))).thenReturn(productResponseDTO);

        ProductResponseDTO result = productService.createProduct(productRequestDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("SKU-001", result.getSku());
        verify(productRepository, times(1)).save(productEntity);
    }

    @Test
    void createProduct_ShouldThrowException_DuplicateSku() {
        when(productRepository.existsBySku(productRequestDTO.getSku())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> productService.createProduct(productRequestDTO));
        verify(productRepository, never()).save(any());
    }


    @Test
    void updateProduct_ShouldUpdateAndReturnProduct_Success() {
        ProductRequestDTO updateDto = ProductRequestDTO.builder().name("Updated Name").build();
        Product existingProduct = productEntity;

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        // simular que el SKU no está duplicado
        when(productRepository.existsBySkuAndIdNot(updateDto.getSku(), 1L)).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(existingProduct);
        when(productMapper.toResponseDto(any(Product.class))).thenReturn(productResponseDTO);

        ProductResponseDTO result = productService.updateProduct(1L, updateDto);

        assertNotNull(result);
        verify(productRepository, times(1)).save(existingProduct);

        verify(productMapper, times(1)).updateEntityFromDto(updateDto, existingProduct);
    }

    @Test
    void updateProduct_ShouldThrowException_NotFound() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> productService.updateProduct(99L, productRequestDTO));
        verify(productRepository, never()).save(any());
    }

    @Test
    void updateProduct_ShouldThrowException_DuplicateSku_OnDifferentProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(productEntity));
        when(productRepository.existsBySkuAndIdNot(productEntity.getSku(), 1L)).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> productService.updateProduct(1L, productRequestDTO));
        verify(productRepository, never()).save(any());
    }



    @Test
    void deleteProductById_ShouldSetProductToInactive_SoftDelete() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(productEntity));

        productService.deleteProductById(1L);
        assertFalse(productEntity.isActive());
        verify(productRepository, times(1)).save(productEntity);
    }

    @Test
    void hardDeleteProductById_ShouldDeleteProduct_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(productEntity));

        productService.hardDeleteProductById(1L);

        verify(productRepository, times(1)).delete(productEntity);
    }

    @Test
    void deleteProductById_ShouldThrowException_NotFound() {

        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());


        assertThrows(EntityNotFoundException.class, () -> productService.deleteProductById(99L));
        verify(productRepository, never()).save(any());
    }


}
