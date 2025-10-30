package com.ais.proyecto_final.service;

import com.ais.proyecto_final.dto.product.ProductRequestDTO;
import com.ais.proyecto_final.dto.product.ProductResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    ProductResponseDTO createProduct(ProductRequestDTO productDto);
// page
    List<ProductResponseDTO> findAllProducts();

    ProductResponseDTO getProductById(Long id);

    ProductResponseDTO updateProduct(Long id, ProductRequestDTO dto);

    void deleteProductById(Long id);

    //delete HARD
     void hardDeleteProductById(Long id);


}