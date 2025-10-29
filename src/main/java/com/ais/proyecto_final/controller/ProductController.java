package com.ais.proyecto_final.controller;

import com.ais.proyecto_final.dto.product.ProductRequestDTO;
import com.ais.proyecto_final.dto.product.ProductResponseDTO;
import com.ais.proyecto_final.service.ProductService;
import com.ais.proyecto_final.service.ProductServiceImpl;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@AllArgsConstructor
public class ProductController {

    private final ProductService productService;
    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(@RequestBody @Valid ProductRequestDTO productRequest) {
        ProductResponseDTO createdProduct = productService.createProduct(productRequest);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        List<ProductResponseDTO> products = productService.findAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    // put
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(@PathVariable Long id, @RequestBody @Valid ProductRequestDTO dto) {
        ProductResponseDTO updated = productService.updateProduct(id, dto);
        return ResponseEntity.ok(updated);
    }


    // delete
    @DeleteMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> deleteProductById(@PathVariable Long id) {
        productService.deleteProductById(id);
        return ResponseEntity.noContent().build();
    }
}
