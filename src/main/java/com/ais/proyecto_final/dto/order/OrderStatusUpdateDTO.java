package com.ais.proyecto_final.dto.order;

import com.ais.proyecto_final.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderStatusUpdateDTO {
    @NotNull(message = "El estado es obligatorio")
    private OrderStatus status;

}