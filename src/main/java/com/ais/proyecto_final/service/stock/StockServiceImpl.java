package com.ais.proyecto_final.service.stock;

import com.ais.proyecto_final.entity.Product;
import com.ais.proyecto_final.exceptions.OrderBusinessException;
import com.ais.proyecto_final.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class StockServiceImpl implements StockService {

    private final ProductRepository productRepository;

    @Transactional
    @Override
    public void reduceStock(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Producto " + productId + " no existe."));

        if (!product.isActive()) {
            throw new OrderBusinessException("El producto '" + product.getName() + "' (ID: " + product.getId() + ") no está activo.");
        }
        if (product.getStock() < quantity) {
            throw new OrderBusinessException("Stock insuficiente para '" + product.getName() + "'. Stock actual: " + product.getStock() + ", Solicitado: " + quantity);
        }

        product.setStock(product.getStock() - quantity);
        productRepository.save(product);
    }

    @Transactional
    @Override
    public void returnStock(Long productId, Integer quantity) {
        // Encontramos el producto. Si no existe, EntityNotFoundException es apropiado.
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Producto " + productId + " no existe al intentar devolver stock."));

        product.setStock(product.getStock() + quantity);
        productRepository.save(product);
    }
}