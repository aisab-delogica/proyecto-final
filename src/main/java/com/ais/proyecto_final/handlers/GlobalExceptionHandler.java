package com.ais.proyecto_final.handlers;

import com.ais.proyecto_final.dto.error.ErrorDetailDTO;
import com.ais.proyecto_final.dto.error.ErrorResponseDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {
    /* Contrato de errores segun documentacion
{
 "timestamp": "2025-09-15T10:00:00Z",
 "path": "/api/orders",
 "status": 400,
 "error": "Bad Request",
 "code": "VALIDATION_ERROR",
 "message": "Los datos enviados no superan la validación",
 "details": [
   {"field": "items[0].quantity", "message": "debe ser mayor que 0"}
 ],
 "traceId": "opcional-para-trazabilidad"
} */
    /*
    404 Not Found RESOURCE_NOT_FOUND (EJ. ID NO EXISTE)
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleEntityNotFoundException(
            EntityNotFoundException ex, HttpServletRequest request) {

        HttpStatus status = HttpStatus.NOT_FOUND;

        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .timestamp(Instant.now())
                .path(request.getRequestURI())
                .status(status.value())
                .error(status.getReasonPhrase())
                .code("RESOURCE_NOT_FOUND")
                .message(ex.getMessage())
                .build();

        return new ResponseEntity<>(errorResponse, status);
    }
    /*
        400 Bad Request VALIDATION_ERROR (@VALID NO PASA, EJ @NOTBLANK ES BLANK)
        */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST;

        //mapea los detalles del error
        List<ErrorDetailDTO> details = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            ErrorDetailDTO detail = ErrorDetailDTO.builder()
                    .field(error.getField())
                    .message(error.getDefaultMessage())
                    .build();
            details.add(detail);
        }

        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .timestamp(Instant.now())
                .path(request.getRequestURI())
                .status(status.value())
                .error(status.getReasonPhrase())
                .code("VALIDATION_ERROR")
                .message("Los datos enviados no superan la validación")
                .details(details)
                .build();

        return new ResponseEntity<>(errorResponse, status);
    }

    /*
   400 Bad Request VALIDATION_ERROR (TIPO DE DATO NO CORRECTO EN REQUEST, EJ STRING EN VEZ DE DATE)
    */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDTO> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST;
        String fieldName = ex.getName();

        ErrorDetailDTO detail = ErrorDetailDTO.builder()
                .field(fieldName)
                .message(ex.getValue()+" no cumple con el formato correcto.")
                .build();

        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .timestamp(Instant.now())
                .path(request.getRequestURI())
                .status(status.value())
                .error(status.getReasonPhrase())
                .code("VALIDATION_ERROR")
                .message("Los datos enviados no superan la validación")
                .details(List.of(detail))
                .build();

        return new ResponseEntity<>(errorResponse, status);
    }

    /*
  409 Conflict DUPLICATE_RESOURCE (EJ sku YA EXISTE)
   */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleConflictException(
            IllegalArgumentException ex, HttpServletRequest request) {

        HttpStatus status = HttpStatus.CONFLICT;

        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .timestamp(Instant.now())
                .path(request.getRequestURI())
                .status(status.value())
                .error(status.getReasonPhrase())
                .code("CONFLICT")
                .message(ex.getMessage())
                .build();

        return new ResponseEntity<>(errorResponse, status);
    }
}
