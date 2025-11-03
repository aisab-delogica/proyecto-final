package com.ais.proyecto_final.handlers;

import com.ais.proyecto_final.dto.error.ErrorDetailDTO;
import com.ais.proyecto_final.dto.error.ErrorResponseDTO;
import com.ais.proyecto_final.exceptions.DuplicateResourceException;
import com.ais.proyecto_final.exceptions.OrderBusinessException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler handler;

    @Mock
    private HttpServletRequest request;

    private final String REQUEST_PATH = "/api/test";

    @BeforeEach
    void setUp() {
        when(request.getRequestURI()).thenReturn(REQUEST_PATH);
    }

    @Test
    void handleMethodArgumentNotValid_ShouldReturnBadRequestWithDetails() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("objectName", "fieldName", "El campo no puede ser nulo");
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<ErrorResponseDTO> response = handler.handleMethodArgumentNotValidException(ex, request);
        ErrorResponseDTO responseBody = response.getBody();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("VALIDATION_ERROR", responseBody.getCode());
        assertEquals("Los datos enviados no superan la validación", responseBody.getMessage());
        assertNotNull(responseBody.getDetails());
        assertFalse(responseBody.getDetails().isEmpty());
        assertEquals("fieldName", responseBody.getDetails().get(0).getField());
        assertEquals("El campo no puede ser nulo", responseBody.getDetails().get(0).getMessage());
    }

    @Test
    void handleMethodArgumentTypeMismatchException_ShouldReturnBadRequest() {
        MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);
        when(ex.getName()).thenReturn("id");
        when(ex.getValue()).thenReturn("textoInvalido");
        doReturn(Long.class).when(ex).getRequiredType();

        ResponseEntity<ErrorResponseDTO> response = handler.handleMethodArgumentTypeMismatchException(ex, request);
        ErrorResponseDTO responseBody = response.getBody();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("VALIDATION_ERROR", responseBody.getCode());
        assertEquals("Los datos enviados no superan la validación", responseBody.getMessage());

        assertNotNull(responseBody.getDetails());
        assertFalse(responseBody.getDetails().isEmpty());

        ErrorDetailDTO detail = responseBody.getDetails().get(0);
        assertEquals("id", detail.getField());
        assertEquals("textoInvalido no cumple con el formato correcto.", detail.getMessage());
    }

    @Test
    void handleEntityNotFoundException_ShouldReturnNotFound() {
        
        String errorMessage = "Cliente 1 no encontrado.";
        EntityNotFoundException ex = new EntityNotFoundException(errorMessage);

        
        ResponseEntity<ErrorResponseDTO> response = handler.handleEntityNotFoundException(ex, request);

        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ErrorResponseDTO body = response.getBody();
        assertNotNull(body);
        assertEquals(HttpStatus.NOT_FOUND.value(), body.getStatus());
        assertEquals("RESOURCE_NOT_FOUND", body.getCode());
        assertEquals(errorMessage, body.getMessage());
        assertNull(body.getDetails());
    }
    

    
    @Test
    void handleDuplicateResourceException_ShouldReturnConflict() {
        
        String errorMessage = "El email ya está en uso.";
        DuplicateResourceException ex = new DuplicateResourceException(errorMessage);

        
        ResponseEntity<ErrorResponseDTO> response = handler.handleDuplicateResourceException(ex, request);

        
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        ErrorResponseDTO body = response.getBody();
        assertNotNull(body);
        assertEquals(HttpStatus.CONFLICT.value(), body.getStatus());
        assertEquals("DUPLICATE_RESOURCE", body.getCode());
        assertEquals(errorMessage, body.getMessage());
    }

    
    @Test
    void handleOrderBusinessException_ShouldReturnBadRequest() {
        
        String errorMessage = "Stock insuficiente para el producto X.";
        OrderBusinessException ex = new OrderBusinessException(errorMessage);

        
        ResponseEntity<ErrorResponseDTO> response = handler.handleOrderBusinessException(ex, request);

        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponseDTO body = response.getBody();
        assertNotNull(body);
        assertEquals(HttpStatus.BAD_REQUEST.value(), body.getStatus());
        assertEquals("BUSINESS_RULE_VIOLATION", body.getCode());
        assertEquals(errorMessage, body.getMessage());
    }
}