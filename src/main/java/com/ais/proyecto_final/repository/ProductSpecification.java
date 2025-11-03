package com.ais.proyecto_final.repository;

import com.ais.proyecto_final.entity.Product;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class ProductSpecification {

    /**
     * Devuelve un Specification que filtra por el nombre del producto (case-insensitive).
     * Si el nombre es nulo o vacío, no aplica el filtro.
     */
    public static Specification<Product> nameContains(String name) {
        return (root, query, cb) -> {
            if (StringUtils.hasText(name)) {
                return cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
            }
            return cb.conjunction();
        };
    }

    /**
     * Devuelve un Specification que filtra por el estado 'active' del producto.
     * Si el estado es nulo, no aplica el filtro.
     */
    public static Specification<Product> isActive(Boolean active) {
        return (root, query, cb) -> {
            if (active != null) {
                return cb.equal(root.get("active"), active);
            }
            return cb.conjunction();
        };
    }
}