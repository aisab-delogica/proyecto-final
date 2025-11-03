package com.ais.proyecto_final.service.stock;

public interface StockService {
    void reduceStock(Long productId, Integer quantity);
    void returnStock(Long productId, Integer quantity);
}