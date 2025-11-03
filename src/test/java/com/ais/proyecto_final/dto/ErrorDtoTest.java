package com.ais.proyecto_final.dto;

import com.ais.proyecto_final.dto.error.ErrorDetailDTO;
import com.ais.proyecto_final.dto.error.ErrorResponseDTO;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ErrorDtoTest {

    private final Instant FIXED_TIME = Instant.parse("2023-10-27T10:00:00Z");

    @Test
    void errorDetailDTO_ShouldCoverAllLombokMethods() {
        
        ErrorDetailDTO dto1 = ErrorDetailDTO.builder()
                .field("email")
                .message("must not be null")
                .build();

        assertNotNull(dto1);
        assertEquals("email", dto1.getField());
        assertEquals("must not be null", dto1.getMessage());

        
        dto1.setField("password");
        dto1.setMessage("size must be > 6");
        assertEquals("password", dto1.getField());
        assertEquals("size must be > 6", dto1.getMessage());

        
        ErrorDetailDTO dto2 = ErrorDetailDTO.builder().field("a").message("b").build();
        ErrorDetailDTO dto3 = ErrorDetailDTO.builder().field("a").message("b").build();
        ErrorDetailDTO dto4 = ErrorDetailDTO.builder().field("c").message("d").build();

        assertEquals(dto2, dto3);
        assertEquals(dto2.hashCode(), dto3.hashCode());
        assertNotEquals(dto2, dto4);
        assertNotNull(dto1.toString());
    }

    @Test
    void errorResponseDTO_ShouldCoverAllLombokMethods() {
        ErrorDetailDTO detail = ErrorDetailDTO.builder().field("name").message("required").build();
        List<ErrorDetailDTO> detailsList = List.of(detail);

        ErrorResponseDTO dto1 = ErrorResponseDTO.builder()
                .timestamp(FIXED_TIME)
                .path("/api/users")
                .status(400)
                .error("Validation Error")
                .code("ERR-400-01")
                .message("Request validation failed")
                .details(detailsList)
                .build();

        assertNotNull(dto1);
        assertEquals(FIXED_TIME, dto1.getTimestamp());
        assertEquals(400, dto1.getStatus());
        assertEquals(1, dto1.getDetails().size());

        
        dto1.setStatus(500);
        assertEquals(500, dto1.getStatus());
        dto1.setDetails(List.of());
        assertEquals(0, dto1.getDetails().size());

        
        ErrorResponseDTO dto2 = ErrorResponseDTO.builder().status(404).code("E1").build();
        ErrorResponseDTO dto3 = ErrorResponseDTO.builder().status(404).code("E1").build();
        ErrorResponseDTO dto4 = ErrorResponseDTO.builder().status(500).code("E1").build();

        assertEquals(dto2, dto3);
        assertEquals(dto2.hashCode(), dto3.hashCode());
        assertNotEquals(dto2, dto4);
        assertNotNull(dto1.toString());
    }
}