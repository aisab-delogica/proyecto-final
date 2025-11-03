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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    private ProductRequestDTO productRequestDTO;
    private Product productEntity;
    private ProductResponseDTO productResponseDTO;
    private final Long PRODUCT_ID = 1L;

    @BeforeEach
    void setUp() {
        productRequestDTO = ProductRequestDTO.builder()
                .sku("SKU001")
                .name("Test Product")
                .price(new BigDecimal("100.00"))
                .stock(5)
                .active(true)
                .build();

        productEntity = Product.builder()
                .id(PRODUCT_ID)
                .sku("SKU001")
                .name("Test Product")
                .price(new BigDecimal("100.00"))
                .stock(5)
                .active(true)
                .build();

        productResponseDTO = ProductResponseDTO.builder()
                .id(PRODUCT_ID)
                .sku("SKU001")
                .name("Test Product")
                .price(new BigDecimal("100.00"))
                .stock(5)
                .active(true)
                .build();
    }



    @Test
    void shouldCreateProductSuccessfully() {

        when(productRepository.existsBySku(anyString())).thenReturn(false);
        when(productMapper.dtoToEntity(any(ProductRequestDTO.class))).thenReturn(productEntity);
        when(productRepository.save(any(Product.class))).thenReturn(productEntity);
        when(productMapper.toResponseDto(any(Product.class))).thenReturn(productResponseDTO);


        ProductResponseDTO result = productService.createProduct(productRequestDTO);


        assertNotNull(result);
        assertEquals(PRODUCT_ID, result.getId());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void shouldThrowDuplicateResourceExceptionWhenCreatingProductWithExistingSku() {

        when(productRepository.existsBySku(anyString())).thenReturn(true);


        assertThrows(DuplicateResourceException.class, () -> productService.createProduct(productRequestDTO));
        verify(productRepository, never()).save(any(Product.class));
    }



    @Test
    void shouldFindAllProductsUsingSpecification() {

        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(List.of(productEntity), pageable, 1);

        // Probamos que ahora llama a findAll con *cualquier* Specification
        when(productRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(productPage);
        when(productMapper.toResponseDto(any(Product.class))).thenReturn(productResponseDTO);

        // Llamamos al método (con o sin filtros)
        ProductResponseDTO result = productService.findAllProducts("Test", true, pageable).getContent().get(0);

        assertEquals(PRODUCT_ID, result.getId());
        // Verificamos que se llamó al método findAll con la especificación
        verify(productRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }



    @Test
    void shouldGetProductByIdSuccessfully() {

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(productEntity));
        when(productMapper.toResponseDto(any(Product.class))).thenReturn(productResponseDTO);


        ProductResponseDTO result = productService.getProductById(PRODUCT_ID);


        assertNotNull(result);
        assertEquals(PRODUCT_ID, result.getId());
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenGettingProductById() {

        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());


        assertThrows(EntityNotFoundException.class, () -> productService.getProductById(99L));
    }



    @Test
    void shouldUpdateProductSuccessfully() {

        String newName = "Nuevo Nombre";
        BigDecimal newPrice = new BigDecimal("200.00");

        ProductRequestDTO updateDTO = ProductRequestDTO.builder()
                .sku(productEntity.getSku())
                .name(newName)
                .price(newPrice)
                .stock(10)
                .active(false)
                .build();


        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(productEntity));


        doAnswer(invocation -> {
            Product entity = invocation.getArgument(1);


            entity.setName(newName);
            entity.setPrice(newPrice);
            entity.setStock(updateDTO.getStock());
            entity.setActive(updateDTO.getActive());

            return null;
        }).when(productMapper).updateEntityFromDto(eq(updateDTO), any(Product.class));


        when(productRepository.save(any(Product.class))).thenReturn(productEntity);


        ProductResponseDTO updatedResponseDTO = ProductResponseDTO.builder()
                .id(PRODUCT_ID).name(newName).price(newPrice).stock(10).active(false)
                .sku(productEntity.getSku()).build();

        when(productMapper.toResponseDto(any(Product.class))).thenReturn(updatedResponseDTO);


        ProductResponseDTO result = productService.updateProduct(PRODUCT_ID, updateDTO);


        assertNotNull(result);
        assertEquals(newName, result.getName());
        assertTrue(result.getPrice().compareTo(newPrice) == 0);

        verify(productRepository, times(1)).findById(PRODUCT_ID);
        verify(productRepository, times(1)).save(productEntity);
        verify(productRepository, never()).existsBySku(anyString());
    }

    @Test
    void shouldThrowDuplicateResourceExceptionOnUpdateIfNewSkuExists() {


        ProductRequestDTO updateDTO = ProductRequestDTO.builder().sku("SKU_NEW").name("New Name").price(new BigDecimal("200.00")).stock(10).active(false).build();

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(productEntity));


        when(productRepository.existsBySku(updateDTO.getSku())).thenReturn(true);


        assertThrows(DuplicateResourceException.class, () -> productService.updateProduct(PRODUCT_ID, updateDTO));

        verify(productRepository, times(1)).findById(PRODUCT_ID);
        verify(productRepository, times(1)).existsBySku(updateDTO.getSku());
        verify(productRepository, never()).save(any(Product.class));
    }


    @Test
    void shouldSoftDeleteProductSuccessfully() {

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(productEntity));


        productService.deleteProductById(PRODUCT_ID);



        assertFalse(productEntity.isActive());

        verify(productRepository, times(1)).save(productEntity);
    }

    @Test
    void shouldHardDeleteProductSuccessfully() {

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(productEntity));
        doNothing().when(productRepository).delete(productEntity);


        productService.hardDeleteProductById(PRODUCT_ID);


        verify(productRepository, times(1)).delete(productEntity);
    }

    @Test
    void shouldThrowEntityNotFoundExceptionOnDeleteIfProductDoesNotExist() {

        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());


        assertThrows(EntityNotFoundException.class, () -> productService.deleteProductById(99L));


        assertThrows(EntityNotFoundException.class, () -> productService.hardDeleteProductById(99L));
    }


    @Test
    void shouldGetProductEntityByIdSuccessfully() {

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(productEntity));


        Optional<Product> result = productService.getProductEntityById(PRODUCT_ID);


        assertTrue(result.isPresent());
        assertEquals(PRODUCT_ID, result.get().getId());
    }
}