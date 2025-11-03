package com.ais.proyecto_final.repository;

import com.ais.proyecto_final.entity.Product;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    boolean existsBySku(String sku);

    Object existsBySkuAndIdNot(@NotBlank(message = "El sku es obligatorio") @Size(max = 40, message = "El sku no puede tener más de 40 caracteres") String sku, long l);
}