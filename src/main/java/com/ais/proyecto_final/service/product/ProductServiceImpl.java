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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Transactional
    public ProductResponseDTO createProduct(ProductRequestDTO productDto) {
        log.info("Creando producto con sku: {}", productDto.getSku());
        if (productRepository.existsBySku(productDto.getSku())) {
            log.warn("Product creation failed: SKU {} already exists.", productDto.getSku());
            throw new DuplicateResourceException("El sku " + productDto.getSku() + " ya existe.");
        }

        Product product = productMapper.dtoToEntity(productDto);
        Product saved = productRepository.save(product);
        log.info("Producto creado con id: {} y sku: {}", saved.getId(), saved.getSku());
        return productMapper.toResponseDto(saved);
    }


    @Transactional
    public Page<ProductResponseDTO> findAllProducts(String name, Boolean active, Pageable pageable) {
        log.info("Buscando productos con filtro nombre: {}, estado activo: {}, página: {}", name, active, pageable.getPageNumber());
        Specification<Product> spec = ProductSpecification.nameContains(name)
                .and(ProductSpecification.isActive(active));

        Page<Product> productsPage = productRepository.findAll(spec, pageable);
        log.info("Found {} products on page {}", productsPage.getNumberOfElements(), pageable.getPageNumber());
        return productsPage.map(productMapper::toResponseDto);
    }

    @Transactional
    public ProductResponseDTO getProductById(Long id) {
        log.info("Buscando producto con id: {}", id);
        return productRepository.findById(id)
                .map(product -> {
                    log.info("Producto encontrado con el id: {}", id);
                    return productMapper.toResponseDto(product);
                })
                .orElseThrow(() -> {
                    log.warn("Producto no encontrado para el id: {}.", id);
                    return new EntityNotFoundException("Producto " + id + " no existe");
                });
    }

    //soft delete (logico)
    @Transactional
    public void deleteProductById(Long id) {
        log.info("Dando de baja producto con id: {}", id);
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Fallo en el proceso de dar de baja, no se encuentra producto con id: {}.", id);
                    return new EntityNotFoundException("Producto " + id + " no existe");
                });
        existing.setActive(false);
        productRepository.save(existing);
        log.info("Producto con id: {} ha sido dado de baja.", id);
    }

    //hard delete (fisico)
    @Transactional
    public void hardDeleteProductById(Long id) {
        log.info("ELIMINANDO producto con ID: {}", id);
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Borrado fallido. No se ha encontrado producto con id: {} .", id);
                    return new EntityNotFoundException("Producto " + id + " no existe");
                });
        productRepository.delete(existing);
        log.info("Producto con id: {} ha sido eliminado de la BD.", id);
    }


    // put
    @Transactional
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO dto) {
        log.info("Actualizando producto con id: {}", id);
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Fallo en el update. No se ha encontrado producto con id: {}.", id);
                    return new EntityNotFoundException("Producto " + id + " no existe.");
                });

        if (!existing.getSku().equals(dto.getSku()) &&
                productRepository.existsBySku(dto.getSku())) {
            log.warn("Fallo en el update para ID: {}. Nuevo SKU: {} ya existe.", id, dto.getSku());
            throw new DuplicateResourceException("SKU ya existe para otro producto");
        }

        productMapper.updateEntityFromDto(dto, existing);
        Product updated = productRepository.save(existing);
        log.info("Actualizado producto con id: {} .", updated.getId());
        return productMapper.toResponseDto(updated);
    }


    // orders

    @Override
    public Optional<Product> getProductEntityById(Long id) {
        log.info("Buscando producto con ID: {}", id);
        return productRepository.findById(id);
    }
}