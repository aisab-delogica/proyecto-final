package com.ais.proyecto_final.repository;

import com.ais.proyecto_final.entity.Product;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsBySku(String sku);
 Page<Product> findByNameContainingIgnoreCaseAndActive(String name, Boolean active, Pageable pageable);
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<Product> findByActive(Boolean active, Pageable pageable);


    Object existsBySkuAndIdNot(@NotBlank(message = "El sku es obligatorio") @Size(max = 40, message = "El sku no puede tener más de 40 caracteres") String sku, long l);
}


