package com.ais.proyecto_final.repository;

import com.ais.proyecto_final.entity.Order;
import com.ais.proyecto_final.entity.OrderStatus;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrderSpecification {

    public static Specification<Order> filterOrders(
            Long customerId, LocalDate fromDate, LocalDate toDate, OrderStatus status) {

        return (Root<Order> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (customerId != null) {
                // Asume que la entidad Order tiene la relación 'customer'
                predicates.add(cb.equal(root.get("customer").get("id"), customerId));
            }

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            if (fromDate != null) {
                // orderDate es LocalDateTime, usamos el inicio del día
                predicates.add(cb.greaterThanOrEqualTo(root.get("orderDate"), fromDate.atStartOfDay()));
            }

            if (toDate != null) {
                // orderDate es LocalDateTime, usamos el final del día
                predicates.add(cb.lessThanOrEqualTo(root.get("orderDate"), toDate.plusDays(1).atStartOfDay()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}