package com.ais.proyecto_final.service;


import com.ais.proyecto_final.dto.product.ProductRequestDTO;
import com.ais.proyecto_final.dto.product.ProductResponseDTO;
import com.ais.proyecto_final.entity.Product;
import com.ais.proyecto_final.mappers.ProductMapper;
import com.ais.proyecto_final.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    @Transactional
    public ProductResponseDTO createProduct(ProductRequestDTO productDto) {
        if (productRepository.existsBySku(productDto.getSku())) {
            throw new IllegalArgumentException("El SKU " + productDto.getSku() + " ya existe.");
        }

        Product product = productMapper.dtoToEntity(productDto);
        Product saved = productRepository.save(product);
        return productMapper.toResponseDto(saved);
    }

    @Transactional
    public List<ProductResponseDTO> findAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(productMapper::toResponseDto)
                .collect(Collectors.toList());
    }
    @Transactional
    public ProductResponseDTO getProductById(Long id) {
        return productRepository.findById(id)
                .map(productMapper::toResponseDto)
                .orElseThrow(() -> new EntityNotFoundException("Producto " + id + " no existe."));
    }

    // delete
    @Transactional
    public void deleteProductById(Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("Producto " + id + " no existe.");
        }
    }


    // put
    @Transactional
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO dto) {
        Product existing = productRepository.findById(id).orElse(null);
        if (existing == null) {
            throw new EntityNotFoundException("Producto " + id + " no existe.");
        }
        if (!existing.getSku().equals(dto.getSku()) &&
                productRepository.existsBySku(dto.getSku())) {
            throw new IllegalArgumentException("SKU ya existe para otro producto");
        }

        productMapper.updateEntityFromDto(dto, existing);
        Product updated = productRepository.save(existing);

        return productMapper.toResponseDto(updated);
    }


}
