package com.ais.proyecto_final.dto.product;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductRequestDTO {

    @NotBlank(message = "El sku es obligatorio")
    @Size(max = 40, message = "El sku no puede tener más de 40 caracteres")
    private String sku;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 160, message = "El nombre no puede tener más de 160 caracteres")
    private String name;

    @Size(max = 2000, message = "La descripción no puede tener más de 2000 caracteres")
    private String description;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor que cero")
    private BigDecimal price;

    @NotNull(message = "El stock es obligatorio")
    @PositiveOrZero(message = "El stock no puede estar por debajo de cero")
    private Integer stock;

    @NotNull(message = "El estado del producto es obligatorio")
    private boolean active = true;
}