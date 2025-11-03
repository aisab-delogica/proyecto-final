package com.ais.proyecto_final.dto.order;

import com.ais.proyecto_final.entity.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
/* {
  "id": 10,
  "orderDate": "2025-09-15T10:12:30Z",
  "status": "CREATED",
  "customerId": 1,
  "shippingAddressId": 3,
  "items": [
    {"productId": 1, "quantity": 2, "unitPrice": 7.50, "lineTotal": 15.00},
    {"productId": 2, "quantity": 1, "unitPrice": 12.00, "lineTotal": 12.00}
  ],
  "total": 27.00
} */
@Data
@Builder
public class OrderResponseDTO {
    private Long id;
    private LocalDateTime orderDate;
    private OrderStatus status;
    private Long customerId;
    private Long shippingAddressId;
    private BigDecimal total;
    private List<LineItemResponseDTO> items;
}