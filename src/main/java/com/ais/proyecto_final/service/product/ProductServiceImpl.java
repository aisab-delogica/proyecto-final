package com.ais.proyecto_final.service.product;


import com.ais.proyecto_final.dto.product.ProductRequestDTO;
import com.ais.proyecto_final.dto.product.ProductResponseDTO;
import com.ais.proyecto_final.entity.Product;
import com.ais.proyecto_final.exceptions.DuplicateResourceException;
import com.ais.proyecto_final.exceptions.OrderBusinessException;
import com.ais.proyecto_final.mappers.ProductMapper;
import com.ais.proyecto_final.repository.ProductRepository;
import com.ais.proyecto_final.repository.ProductSpecification;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    @Transactional
    public ProductResponseDTO createProduct(ProductRequestDTO productDto) {
        if (productRepository.existsBySku(productDto.getSku())) {
            throw new DuplicateResourceException("El sku " + productDto.getSku() + " ya existe.");
        }

        Product product = productMapper.dtoToEntity(productDto);
        Product saved = productRepository.save(product);
        return productMapper.toResponseDto(saved);
    }


    @Transactional
    public Page<ProductResponseDTO> findAllProducts(String name, Boolean active, Pageable pageable) {

     Specification<Product> spec = ProductSpecification.nameContains(name)
                .and(ProductSpecification.isActive(active));

        Page<Product> productsPage = productRepository.findAll(spec, pageable);

        return productsPage.map(productMapper::toResponseDto);
    }

    @Transactional
    public ProductResponseDTO getProductById(Long id) {
        return productRepository.findById(id)
                .map(productMapper::toResponseDto)
                .orElseThrow(() -> new EntityNotFoundException("Producto " + id + " no existe"));
    }

    //soft delete (logico)
    @Transactional
    public void deleteProductById(Long id) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto " + id + " no existe"));
        existing.setActive(false);
        productRepository.save(existing);
    }

    //hard delete (fisico)
    @Transactional
    public void hardDeleteProductById(Long id) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto " + id + " no existe"));
        productRepository.delete(existing);
    }


    // put
    @Transactional
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO dto) {
        // mejor que con un if (respeta la regla del optional)
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto " + id + " no existe."));

        if (!existing.getSku().equals(dto.getSku()) &&
                productRepository.existsBySku(dto.getSku())) {
            throw new DuplicateResourceException("SKU ya existe para otro producto");
        }

        productMapper.updateEntityFromDto(dto, existing);
        Product updated = productRepository.save(existing);

        return productMapper.toResponseDto(updated);
    }


    // orders

    @Override
    public Optional<Product> getProductEntityById(Long id) {
        return productRepository.findById(id);
    }




}
