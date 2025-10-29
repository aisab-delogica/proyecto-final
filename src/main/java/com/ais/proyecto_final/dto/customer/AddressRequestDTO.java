package com.ais.proyecto_final.dto.customer;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddressRequestDTO {
    @NotBlank
    private String line1;
    private String line2;
    @NotBlank private String city;
    @NotBlank private String postalCode;
    @NotBlank private String country;
    private boolean isDefault;
}