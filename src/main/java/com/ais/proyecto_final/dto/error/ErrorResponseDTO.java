package com.ais.proyecto_final.dto.error;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;
import java.util.List;

@Data
@Builder
public class ErrorResponseDTO {
    private Instant timestamp;
    private String path;
    private int status;
    private String error;
    private String code;
    private String message;
    private List<ErrorDetailDTO> details;
}