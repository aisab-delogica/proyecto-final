package com.ais.proyecto_final.handlers;

import com.ais.proyecto_final.dto.error.ErrorDetailDTO;
import com.ais.proyecto_final.dto.error.ErrorResponseDTO;
import com.ais.proyecto_final.exceptions.DuplicateResourceException;
import com.ais.proyecto_final.exceptions.OrderBusinessException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.security.core.AuthenticationException;
@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

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
        String requiredType = (ex.getRequiredType() != null) ? ex.getRequiredType().getSimpleName() : "tipo incorrecto";
        String message = String.format("El valor '%s' no es un formato válido. Se esperaba %s.",
                ex.getValue(),
                requiredType);

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
  era mejor usar este que el IllegalStateException porque es mas especifico
   */

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponseDTO> handleDuplicateResourceException(
            DuplicateResourceException ex, HttpServletRequest request) {

        HttpStatus status = HttpStatus.CONFLICT;

        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .timestamp(Instant.now())
                .path(request.getRequestURI())
                .status(status.value())
                .error(status.getReasonPhrase())
                .code("DUPLICATE_RESOURCE")
                .message(ex.getMessage())
                .build();

        return new ResponseEntity<>(errorResponse, status);
    }


    /*
        400 Bad Request BUSINESS_RULE_VIOLATION (EJ. STOCK INSUFICIENTE)
    */
    @ExceptionHandler(OrderBusinessException.class)
    public ResponseEntity<ErrorResponseDTO> handleOrderBusinessException(
            OrderBusinessException ex, HttpServletRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST;

        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .timestamp(Instant.now())
                .path(request.getRequestURI())
                .status(status.value())
                .error(status.getReasonPhrase())
                .code("BUSINESS_RULE_VIOLATION")
                .message(ex.getMessage())
                .build();

        return new ResponseEntity<>(errorResponse, status);
    }

    // handler generíco para excepciones no controladas (internal error)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleInternalServerError(
            Exception ex, HttpServletRequest request) {
        log.error("Unhandled Internal Server Error at path {}: {}", request.getRequestURI(), ex.getMessage(), ex);

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .timestamp(Instant.now())
                .path(request.getRequestURI())
                .status(status.value())
                .error(status.getReasonPhrase())
                .code("INTERNAL_ERROR")
                .message("Ha habido un error interno.")
                .build();

        return new ResponseEntity<>(errorResponse, status);
    }

    /*
        401 Unauthorized AUTHENTICATION_FAILURE (Ej. login incorrecto)
        */
    @ExceptionHandler({AuthenticationException.class, BadCredentialsException.class})
    public ResponseEntity<ErrorResponseDTO> handleAuthenticationException(
            AuthenticationException ex, HttpServletRequest request) {

        HttpStatus status = HttpStatus.UNAUTHORIZED;

        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .timestamp(Instant.now())
                .path(request.getRequestURI())
                .status(status.value())
                .error(status.getReasonPhrase())
                .code("AUTHENTICATION_FAILURE")
                .message("Credenciales inválidas o autenticación fallida.")
                .build();

        return new ResponseEntity<>(errorResponse, status);
    }

    /*
        403 Forbidden AUTHORIZATION_FAILURE (Ej. Rol no permitido)
    */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDTO> handleAccessDeniedException(
            AccessDeniedException ex, HttpServletRequest request) {

        HttpStatus status = HttpStatus.FORBIDDEN;

        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .timestamp(Instant.now())
                .path(request.getRequestURI())
                .status(status.value())
                .error(status.getReasonPhrase())
                .code("FORBIDDEN")
                .message("No tiene permisos suficientes para acceder a este recurso.")
                .build();

        return new ResponseEntity<>(errorResponse, status);
    }
}