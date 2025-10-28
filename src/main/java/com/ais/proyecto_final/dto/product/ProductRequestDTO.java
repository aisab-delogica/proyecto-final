package com.ais.proyecto_final.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductRequestDTO {

    @NotBlank
    @Size(max = 40)
    private String sku;

    @NotBlank
    @Size(max = 160)
    private String name;

    @Size(max = 2000)
    private String description;

    @Positive
    private BigDecimal price;

    @PositiveOrZero
    private Integer stock;

    private boolean active = true;
}