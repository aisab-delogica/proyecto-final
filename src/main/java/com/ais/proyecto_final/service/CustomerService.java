package com.ais.proyecto_final.service;


import com.ais.proyecto_final.entity.Customer;
import com.ais.proyecto_final.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CustomerService {
    @Autowired
    private final CustomerRepository customerRepository;

    @Transactional
    public Customer createCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    @Transactional
    public List<Customer> findAllCustomers() {
        return customerRepository.findAll();
    }
    @Transactional
    public Optional<Customer> getCustomerById(Long id) {
        return customerRepository.findById(id);
    }
}
