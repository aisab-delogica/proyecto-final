    package com.ais.proyecto_final.service.product;

    import com.ais.proyecto_final.dto.product.ProductRequestDTO;
    import com.ais.proyecto_final.dto.product.ProductResponseDTO;
    import com.ais.proyecto_final.entity.Product;
    import com.ais.proyecto_final.exceptions.DuplicateResourceException;
    import com.ais.proyecto_final.exceptions.OrderBusinessException;
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
        void shouldFindAllProductsWithoutFilters() {
            
            Pageable pageable = PageRequest.of(0, 10);
            Page<Product> productPage = new PageImpl<>(List.of(productEntity), pageable, 1);
            when(productRepository.findAll(pageable)).thenReturn(productPage);
            when(productMapper.toResponseDto(any(Product.class))).thenReturn(productResponseDTO);

            
            ProductResponseDTO result = productService.findAllProducts(null, null, pageable).getContent().get(0);

            
            assertEquals(PRODUCT_ID, result.getId());
            verify(productRepository, times(1)).findAll(pageable);
            verify(productRepository, never()).findByNameContainingIgnoreCaseAndActive(any(), any(), any());
        }

        @Test
        void shouldFindAllProductsWithAllFilters() {
            
            Pageable pageable = PageRequest.of(0, 10);
            Page<Product> productPage = new PageImpl<>(List.of(productEntity), pageable, 1);
            String nameFilter = "Test";
            Boolean activeFilter = true;

            when(productRepository.findByNameContainingIgnoreCaseAndActive(nameFilter, activeFilter, pageable))
                    .thenReturn(productPage);
            when(productMapper.toResponseDto(any(Product.class))).thenReturn(productResponseDTO);

            
            productService.findAllProducts(nameFilter, activeFilter, pageable);

            
            verify(productRepository, times(1)).findByNameContainingIgnoreCaseAndActive(nameFilter, activeFilter, pageable);
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

            
            
            assertTrue(productEntity.isActive() == false);
            
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
        void shouldReduceStockSuccessfully() {
            
            int initialStock = productEntity.getStock(); 
            int quantityToReduce = 3;

            when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(productEntity));
            when(productRepository.save(any(Product.class))).thenReturn(productEntity);

            
            productService.reduceStock(PRODUCT_ID, quantityToReduce);

            
            assertEquals(initialStock - quantityToReduce, productEntity.getStock());
            verify(productRepository, times(1)).save(productEntity);
        }

        @Test
        void shouldThrowOrderBusinessExceptionWhenReducingStockInsufficient() {
            
            int quantityToReduce = 10; 

            when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(productEntity));

            
            assertThrows(OrderBusinessException.class, () -> productService.reduceStock(PRODUCT_ID, quantityToReduce));
            verify(productRepository, never()).save(any(Product.class));
        }

        @Test
        void shouldReturnStockSuccessfully() {
            
            productEntity.setStock(5); 
            int quantityToReturn = 8;
            int expectedStock = 13;

            when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(productEntity));
            when(productRepository.save(any(Product.class))).thenReturn(productEntity);

            
            productService.returnStock(PRODUCT_ID, quantityToReturn);

            
            assertEquals(expectedStock, productEntity.getStock());
            verify(productRepository, times(1)).save(productEntity);
        }

        @Test
        void shouldGetProductEntityByIdSuccessfully() {
            
            when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(productEntity));

            
            Optional<Product> result = productService.getProductEntityById(PRODUCT_ID);

            
            assertTrue(result.isPresent());
            assertEquals(PRODUCT_ID, result.get().getId());
        }
    }