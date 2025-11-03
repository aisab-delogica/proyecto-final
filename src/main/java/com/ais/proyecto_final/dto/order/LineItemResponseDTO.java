package com.ais.proyecto_final.dto.order;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class LineItemResponseDTO {
    private Long productId;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal lineTotal; // unitPrice * quantity
}