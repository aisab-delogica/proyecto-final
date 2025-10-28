package com.ais.proyecto_final.service;


import com.ais.proyecto_final.entity.Order;
import com.ais.proyecto_final.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class OrderService {
    @Autowired
    private final OrderRepository orderRepository;

    @Transactional
    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }

    @Transactional
    public List<Order> findAllOrders() {
        return orderRepository.findAll();
    }

    @Transactional
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

}
