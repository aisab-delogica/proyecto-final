package com.ais.proyecto_final.dto;

import com.ais.proyecto_final.dto.error.ErrorDetailDTO;
import com.ais.proyecto_final.dto.error.ErrorResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ErrorDtoTest {

    private final Instant FIXED_TIME = Instant.parse("2023-10-27T10:00:00Z");
    private ErrorDetailDTO detail1;
    private ErrorDetailDTO detail2;
    private ErrorResponseDTO response1;
    private ErrorResponseDTO response2;

    @BeforeEach
    void setUp() {
        detail1 = ErrorDetailDTO.builder().field("email").message("must not be null").build();
        detail2 = ErrorDetailDTO.builder().field("email").message("must not be null").build();

        response1 = ErrorResponseDTO.builder()
                .timestamp(FIXED_TIME)
                .path("/api/users")
                .status(400)
                .error("Validation Error")
                .code("ERR-400-01")
                .message("Request validation failed")
                .details(List.of(detail1))
                .build();

        response2 = ErrorResponseDTO.builder()
                .timestamp(FIXED_TIME)
                .path("/api/users")
                .status(400)
                .error("Validation Error")
                .code("ERR-400-01")
                .message("Request validation failed")
                .details(List.of(detail2))
                .build();
    }

    @Test
    void errorDetailDTO_ShouldCoverLombokMethods() {
        assertEquals(detail1, detail2, "Los DTOs idénticos deben ser iguales");
        assertEquals(detail1.hashCode(), detail2.hashCode(), "Los DTOs idénticos deben tener el mismo hashCode");

        assertNotEquals(detail1, null);
        assertNotEquals(detail1, new Object());
        assertEquals(detail1, detail1);

        ErrorDetailDTO diffField = ErrorDetailDTO.builder().field("password").message("must not be null").build();
        ErrorDetailDTO diffMessage = ErrorDetailDTO.builder().field("email").message("diferente").build();
        assertNotEquals(detail1, diffField);
        assertNotEquals(detail1, diffMessage);

        detail1.setField("password");
        assertEquals("password", detail1.getField());

        assertNotNull(detail1.toString());
    }

    @Test
    void errorDetailDTOBuilder_ShouldCoverToString() {
        assertNotNull(ErrorDetailDTO.builder().field("test").toString());
    }

    @Test
    void errorResponseDTO_ShouldCoverLombokMethods() {
        assertEquals(response1, response2, "Los DTOs idénticos deben ser iguales");
        assertEquals(response1.hashCode(), response2.hashCode(), "Los DTOs idénticos deben tener el mismo hashCode");

        assertNotEquals(response1, null);
        assertNotEquals(response1, new Object());
        assertEquals(response1, response1);

        ErrorResponseDTO diffTime = ErrorResponseDTO.builder().timestamp(Instant.now()).build();
        ErrorResponseDTO diffPath = ErrorResponseDTO.builder().path("/diff").build();
        ErrorResponseDTO diffStatus = ErrorResponseDTO.builder().status(500).build();
        ErrorResponseDTO diffError = ErrorResponseDTO.builder().error("diff").build();
        ErrorResponseDTO diffCode = ErrorResponseDTO.builder().code("diff").build();
        ErrorResponseDTO diffMsg = ErrorResponseDTO.builder().message("diff").build();
        ErrorResponseDTO diffDetails = ErrorResponseDTO.builder().details(List.of()).build();

        assertNotEquals(response1, diffTime);
        assertNotEquals(response1, diffPath);
        assertNotEquals(response1, diffStatus);
        assertNotEquals(response1, diffError);
        assertNotEquals(response1, diffCode);
        assertNotEquals(response1, diffMsg);
        assertNotEquals(response1, diffDetails);

        response1.setStatus(500);
        assertEquals(500, response1.getStatus());

        assertNotNull(response1.toString());
    }

    @Test
    void errorResponseDTOBuilder_ShouldCoverToString() {
        assertNotNull(ErrorResponseDTO.builder().code("test").toString());
    }

    @Test
    void errorDetailDTO_EqualsAndHashCode_WithNullFields() {
        ErrorDetailDTO dto1 = ErrorDetailDTO.builder().field(null).message("msg").build();
        ErrorDetailDTO dto2 = ErrorDetailDTO.builder().field("field").message(null).build();
        ErrorDetailDTO dto3 = ErrorDetailDTO.builder().field(null).message("msg").build();

        assertNotEquals(dto1, dto2);
        assertNotEquals(dto2, dto1);

        assertEquals(dto1, dto3);
        assertEquals(dto1.hashCode(), dto3.hashCode());
        assertNotEquals(dto1.hashCode(), dto2.hashCode());

        assertNotEquals(dto1, ErrorDetailDTO.builder().field(null).message(null).build());
    }

    @Test
    void errorResponseDTO_EqualsAndHashCode_WithNullFields() {
        ErrorResponseDTO dto1 = ErrorResponseDTO.builder()
                .timestamp(null).path("/path").status(400)
                .error(null).code("CODE").message(null)
                .details(null).build();

        ErrorResponseDTO dto2 = ErrorResponseDTO.builder()
                .timestamp(FIXED_TIME).path(null).status(400)
                .error("ERR").code(null).message("MSG")
                .details(List.of()).build();

        ErrorResponseDTO dto3 = ErrorResponseDTO.builder()
                .timestamp(null).path("/path").status(400)
                .error(null).code("CODE").message(null)
                .details(null).build();

        assertNotEquals(dto1, dto2);
        assertNotEquals(dto2, dto1);
        assertEquals(dto1, dto3);
        assertEquals(dto1.hashCode(), dto3.hashCode());
        assertNotEquals(dto1.hashCode(), dto2.hashCode());
    }
}