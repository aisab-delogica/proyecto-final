package com.ais.proyecto_final.dto.error;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorDetailDTO {
    private String field;
    private String message;
}