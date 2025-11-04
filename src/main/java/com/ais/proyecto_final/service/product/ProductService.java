package com.ais.proyecto_final.service.product;

import com.ais.proyecto_final.dto.product.ProductRequestDTO;
import com.ais.proyecto_final.dto.product.ProductResponseDTO;
import com.ais.proyecto_final.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ProductService {

    ProductResponseDTO createProduct(ProductRequestDTO productDto);

    Page<ProductResponseDTO> findAllProducts(String name, Boolean active, Pageable pageable);

    ProductResponseDTO getProductById(Long id);

    ProductResponseDTO updateProduct(Long id, ProductRequestDTO dto);

    void deleteProductById(Long id);

    void hardDeleteProductById(Long id);

    Optional<Product> getProductEntityById(Long id);

    void reduceStock(Long productId, Integer quantity);

    void returnStock(Long productId, Integer quantity);

}