package com.ais.proyecto_final.dto.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;
/* {
  "customerId": 1,
  "shippingAddressId": 3,
  "items": [
    {"productId": 1, "quantity": 2},
    {"productId": 2, "quantity": 1}
  ]
} */
@Data
public class OrderRequestDTO {
    @NotNull(message = "El ID del cliente es obligatorio")
    @Positive(message = "El ID del cliente debe ser positivo")
    private Long customerId;

    @NotNull(message = "El ID de la dirección de envío es obligatorio")
    @Positive(message = "El ID de la dirección de envío debe ser positivo")
    private Long shippingAddressId;

    @Valid
    @NotEmpty(message = "El pedido debe contener al menos un artículo")
    private List<LineItemRequestDTO> items;
}