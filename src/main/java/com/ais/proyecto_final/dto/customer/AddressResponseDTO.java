package com.ais.proyecto_final.dto.customer;

import lombok.Data;

@Data
public class AddressResponseDTO {
    private Long id;
    private String line1;
    private String line2;
    private String city;
    private String postalCode;
    private String country;
    private boolean isDefault;
}
