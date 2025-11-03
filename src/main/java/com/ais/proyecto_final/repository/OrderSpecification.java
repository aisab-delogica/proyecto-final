package com.ais.proyecto_final.repository;

import com.ais.proyecto_final.entity.Order;
import com.ais.proyecto_final.entity.OrderStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrderSpecification {

    public static Specification<Order> filterOrders(
            Long customerId, LocalDate fromDate, LocalDate toDate, OrderStatus status) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (customerId != null) {
                predicates.add(cb.equal(root.get("customer").get("id"), customerId));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (fromDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("orderDate"), fromDate.atStartOfDay()));
            }
            if (toDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("orderDate"), toDate.plusDays(1).atStartOfDay().minusNanos(1)));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}