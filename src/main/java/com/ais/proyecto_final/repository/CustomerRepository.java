package com.ais.proyecto_final.repository;

import com.ais.proyecto_final.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface
CustomerRepository extends JpaRepository<Customer, Long> {
    boolean existsByEmail(String email);
    Page<Customer> findByEmailContainingIgnoreCase(String email, Pageable pageable);
}
