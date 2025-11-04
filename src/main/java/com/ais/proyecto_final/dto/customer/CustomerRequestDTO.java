package com.ais.proyecto_final.dto.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CustomerRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 120, message = "El nombre no puede tener más de 120 caracteres")
    private String fullName;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email no es valido")
    @Size(max = 160, message = "El email no puede tener más de 160 caracteres")
    private String email;

    @NotBlank(message = "El teléfono es obligatorio")
    @Size(max = 15, message = "El teléfono no puede tener más de 15 caracteres")
    private String phone;
}