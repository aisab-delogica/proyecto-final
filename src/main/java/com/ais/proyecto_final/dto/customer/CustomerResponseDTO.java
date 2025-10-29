package com.ais.proyecto_final.dto.customer;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CustomerResponseDTO {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<AddressResponseDTO> addresses;
}
