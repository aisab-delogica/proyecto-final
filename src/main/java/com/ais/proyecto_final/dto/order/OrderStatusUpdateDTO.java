package com.ais.proyecto_final.dto.order;

import com.ais.proyecto_final.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderStatusUpdateDTO {
    @NotNull(message = "El estado es obligatorio")
    private OrderStatus status;

}