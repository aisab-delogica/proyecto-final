package com.ais.proyecto_final.controller;

import com.ais.proyecto_final.dto.product.ProductRequestDTO;
import com.ais.proyecto_final.dto.product.ProductResponseDTO;
import com.ais.proyecto_final.service.product.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// --- Importaciones de Seguridad ---
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
// --- Importaciones Estándar ---
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser // <-- CORRECCIÓN: Autentica TODOS los tests en esta clase
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    private ProductRequestDTO productRequest;
    private ProductResponseDTO productResponse;
    private String productRequestJson;

    @BeforeEach
    void setUp() throws Exception {
        productRequest = ProductRequestDTO.builder()
                .sku("SKU001")
                .name("Test Product")
                .description("Test Desc")
                .price(new BigDecimal("10.00"))
                .stock(100)
                .active(true)
                .build();

        productResponse = ProductResponseDTO.builder()
                .id(1L)
                .sku("SKU001")
                .name("Test Product")
                .description("Test Desc")
                .price(new BigDecimal("10.00"))
                .stock(100)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        productRequestJson = objectMapper.writeValueAsString(productRequest);
    }

    // --- Tests que estaban fallando (corregidos) ---

    @Test
    void getProductById_ShouldReturn200_WhenFound() throws Exception {
        // Given
        when(productService.getProductById(1L)).thenReturn(productResponse);

        // When & Then
        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.sku").value("SKU001"));
    }

    @Test
    void getAllProducts_ShouldReturnPage_WhenCalledWithFilters() throws Exception {
        // Given
        // (Datos del log: name=Test, active=true, page=0, size=5)
        Page<ProductResponseDTO> responsePage = new PageImpl<>(List.of(productResponse));
        when(productService.findAllProducts(eq("Test"), eq(true), any(Pageable.class)))
                .thenReturn(responsePage);

        // When & Then
        mockMvc.perform(get("/api/products")
                        .param("name", "Test")
                        .param("active", "true")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L));
    }

    @Test
    void getProductById_ShouldReturn404_WhenNotFound() throws Exception {
        // Given
        // (Datos del log: ID 99)
        when(productService.getProductById(99L))
                .thenThrow(new EntityNotFoundException("Producto 99 no existe"));

        // When & Then
        mockMvc.perform(get("/api/products/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createProduct_ShouldReturn400_WhenSkuIsBlank() throws Exception {
        // Given
        // (Datos del log: SKU vacío)
        String badJsonRequest = "{\"sku\":\"\",\"name\":\"Test Product\",\"description\":null,\"price\":10,\"stock\":100,\"active\":true}";

        // When & Then
        mockMvc.perform(post("/api/products")
                        .with(csrf()) // <-- CORRECCIÓN: Añade token CSRF para el 403
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badJsonRequest))
                .andExpect(status().isBadRequest()); // Ahora sí espera 400
    }

    // --- Nuevos Tests (para mejorar cobertura) ---

    @Test
    void createProduct_ShouldReturn201_WhenValid() throws Exception {
        // Given
        when(productService.createProduct(any(ProductRequestDTO.class))).thenReturn(productResponse);

        // When & Then
        mockMvc.perform(post("/api/products")
                        .with(csrf()) // <-- CORRECCIÓN: CSRF
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productRequestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void updateProduct_ShouldReturn200_WhenValid() throws Exception {
        // Given
        when(productService.updateProduct(eq(1L), any(ProductRequestDTO.class))).thenReturn(productResponse);

        // When & Then
        mockMvc.perform(put("/api/products/1")
                        .with(csrf()) // <-- CORRECCIÓN: CSRF
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productRequestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void updateProduct_ShouldReturn404_WhenNotFound() throws Exception {
        // Given
        when(productService.updateProduct(eq(99L), any(ProductRequestDTO.class)))
                .thenThrow(new EntityNotFoundException("Producto 99 no existe"));

        // When & Then
        mockMvc.perform(put("/api/products/99")
                        .with(csrf()) // <-- CORRECCIÓN: CSRF
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productRequestJson))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteProductById_ShouldReturn204_WhenFound() throws Exception {
        // Given
        doNothing().when(productService).deleteProductById(1L);

        // When & Then
        mockMvc.perform(delete("/api/products/1")
                        .with(csrf())) // <-- CORRECCIÓN: CSRF
                .andExpect(status().isNoContent());
    }

    @Test
    void hardDeleteProductById_ShouldReturn204_WhenFound() throws Exception {
        // Given
        doNothing().when(productService).hardDeleteProductById(1L);

        // When & Then
        mockMvc.perform(delete("/api/products/1/hard")
                        .with(csrf())) // <-- CORRECCIÓN: CSRF
                .andExpect(status().isNoContent());
    }
}