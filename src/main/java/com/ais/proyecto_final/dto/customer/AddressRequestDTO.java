package com.ais.proyecto_final.dto.customer;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddressRequestDTO {
    @NotBlank(message = "La linea 1 de la dirección no puede estar vacía")
    private String line1;
    private String line2;
    @NotBlank(message = "La ciudad es obligatoria")
    private String city;
    @NotBlank(message = "El codigo postal es obligatorio")
    private String postalCode;
    @NotBlank(message = "El país es obligatorio")
    private String country;
    private boolean isDefault;
}