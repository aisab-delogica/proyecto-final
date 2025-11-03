package com.ais.proyecto_final.dto.customer;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class CustomerRequestDTOTests {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private CustomerRequestDTO createValidDTO() {
        CustomerRequestDTO dto = new CustomerRequestDTO();
        dto.setFullName("Juan Pérez García");
        dto.setEmail("juan.perez@example.com");
        dto.setPhone("123456789012345");
        return dto;
    }

    @Test
    void whenValidCustomerRequestDTO_thenNoViolations() {
        CustomerRequestDTO dto = createValidDTO();
        Set<ConstraintViolation<CustomerRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenFullNameIsBlank_thenViolationOccurs() {
        CustomerRequestDTO dto = createValidDTO();
        dto.setFullName(" ");
        Set<ConstraintViolation<CustomerRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertEquals("El nombre es obligatorio", violations.iterator().next().getMessage());
    }

    @Test
    void whenFullNameIsTooLong_thenViolationOccurs() {
        CustomerRequestDTO dto = createValidDTO();
        dto.setFullName("A".repeat(121));
        Set<ConstraintViolation<CustomerRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertEquals("El nombre no puede tener más de 120 caracteres", violations.iterator().next().getMessage());
    }

    @Test
    void whenEmailIsBlank_thenViolationOccurs() {
        CustomerRequestDTO dto = createValidDTO();
        dto.setEmail("");
        Set<ConstraintViolation<CustomerRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("El email es obligatorio")));
    }

    @Test
    void whenEmailFormatIsInvalid_thenViolationOccurs() {
        CustomerRequestDTO dto = createValidDTO();
        dto.setEmail("invalid-email");
        Set<ConstraintViolation<CustomerRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("El formato del email no es valido")));
    }

    @Test
    void whenEmailIsTooLong_thenViolationOccurs() {
        CustomerRequestDTO dto = createValidDTO();
        
        
        
        dto.setEmail("a".repeat(150) + "@example.com"); 
        Set<ConstraintViolation<CustomerRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("El email no puede tener más de 160 caracteres")));
    }

    @Test
    void whenPhoneIsBlank_thenViolationOccurs() {
        CustomerRequestDTO dto = createValidDTO();
        dto.setPhone(" ");
        Set<ConstraintViolation<CustomerRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenPhoneIsTooLong_thenViolationOccurs() {
        CustomerRequestDTO dto = createValidDTO();
        dto.setPhone("1".repeat(16));
        Set<ConstraintViolation<CustomerRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertEquals("El teléfono no puede tener más de 15 caracteres", violations.iterator().next().getMessage());
    }

    @Test
    void lombokDataMethodsWork() {
        CustomerRequestDTO dto1 = new CustomerRequestDTO();
        dto1.setFullName("Test User");
        dto1.setEmail("test@user.com");
        dto1.setPhone("123456789");

        assertEquals("Test User", dto1.getFullName());

        CustomerRequestDTO dto2 = new CustomerRequestDTO();
        dto2.setFullName("Test User");
        dto2.setEmail("test@user.com");
        dto2.setPhone("123456789");

        CustomerRequestDTO dto3 = new CustomerRequestDTO();
        dto3.setFullName("Another User");

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1, dto3);
        assertTrue(dto1.toString().contains("Test User"));
    }
}