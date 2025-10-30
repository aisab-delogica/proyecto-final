package com.ais.proyecto_final.service.product;

import com.ais.proyecto_final.dto.product.ProductRequestDTO;
import com.ais.proyecto_final.dto.product.ProductResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {

    ProductResponseDTO createProduct(ProductRequestDTO productDto);
// page
 Page<ProductResponseDTO> findAllProducts(String name, Boolean active, Pageable pageable);

    ProductResponseDTO getProductById(Long id);

    ProductResponseDTO updateProduct(Long id, ProductRequestDTO dto);

    void deleteProductById(Long id);

    //delete HARD
     void hardDeleteProductById(Long id);


}