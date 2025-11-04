package com.ais.proyecto_final.service.stock;

import com.ais.proyecto_final.entity.Product;
import com.ais.proyecto_final.exceptions.OrderBusinessException;
import com.ais.proyecto_final.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private StockServiceImpl stockService;

    private Product testProduct;
    private final Long PRODUCT_ID = 1L;

    @BeforeEach
    void setUp() {
        testProduct = Product.builder()
                .id(PRODUCT_ID)
                .sku("SKU001")
                .name("Test Product")
                .price(new BigDecimal("100.00"))
                .stock(5)
                .active(true)
                .build();
    }

    @Test
    void reduceStock_ShouldReduceStockSuccessfully_WhenStockIsSufficient() {
        int initialStock = testProduct.getStock();
        int quantityToReduce = 3;

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        stockService.reduceStock(PRODUCT_ID, quantityToReduce);

        assertEquals(initialStock - quantityToReduce, testProduct.getStock());
        verify(productRepository, times(1)).save(testProduct);
    }

    @Test
    void reduceStock_ShouldThrowOrderBusinessException_WhenStockIsInsufficient() {
        int quantityToReduce = 10; // Más stock del disponible (5)

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(testProduct));

        assertThrows(OrderBusinessException.class, () -> stockService.reduceStock(PRODUCT_ID, quantityToReduce));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void reduceStock_ShouldThrowOrderBusinessException_WhenProductIsInactive() {
        testProduct.setActive(false);
        int quantityToReduce = 1;

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(testProduct));

        assertThrows(OrderBusinessException.class, () -> stockService.reduceStock(PRODUCT_ID, quantityToReduce));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void reduceStock_ShouldThrowEntityNotFoundException_WhenProductDoesNotExist() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> stockService.reduceStock(99L, 1));
    }

    @Test
    void returnStock_ShouldIncreaseStockSuccessfully() {
        testProduct.setStock(5);
        int quantityToReturn = 8;
        int expectedStock = 13;

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        stockService.returnStock(PRODUCT_ID, quantityToReturn);

        assertEquals(expectedStock, testProduct.getStock());
        verify(productRepository, times(1)).save(testProduct);
    }

    @Test
    void returnStock_ShouldThrowEntityNotFoundException_WhenProductDoesNotExist() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> stockService.returnStock(99L, 1));
    }
}