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
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AddressRequestDTOTests {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private AddressRequestDTO createValidDTO() {
        AddressRequestDTO dto = new AddressRequestDTO();
        dto.setLine1("C/ Falsa 123");
        dto.setLine2("Apto. 4B");
        dto.setCity("Madrid");
        dto.setPostalCode("28001");
        dto.setCountry("España");
        dto.setDefaultAddress(true);
        return dto;
    }

    @Test
    void whenValidAddressRequestDTO_thenNoViolations() {
        AddressRequestDTO dto = createValidDTO();
        Set<ConstraintViolation<AddressRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenLine1IsBlank_thenViolationOccurs() {
        AddressRequestDTO dto = createValidDTO();
        dto.setLine1("");
        Set<ConstraintViolation<AddressRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertEquals("La linea 1 de la dirección no puede estar vacía", violations.iterator().next().getMessage());
    }

    @Test
    void whenCityIsBlank_thenViolationOccurs() {
        AddressRequestDTO dto = createValidDTO();
        dto.setCity(" ");
        Set<ConstraintViolation<AddressRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertEquals("La ciudad es obligatoria", violations.iterator().next().getMessage());
    }

    @Test
    void whenPostalCodeIsBlank_thenViolationOccurs() {
        AddressRequestDTO dto = createValidDTO();
        dto.setPostalCode("");
        Set<ConstraintViolation<AddressRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertEquals("El codigo postal es obligatorio", violations.iterator().next().getMessage());
    }

    @Test
    void whenCountryIsBlank_thenViolationOccurs() {
        AddressRequestDTO dto = createValidDTO();
        dto.setCountry(null);
        Set<ConstraintViolation<AddressRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertEquals("El país es obligatorio", violations.iterator().next().getMessage());
    }

    @Test
    void whenLine2IsNullAndIsDefaultIsFalse_thenNoViolations() {
        AddressRequestDTO dto = createValidDTO();
        dto.setLine2(null);
        dto.setDefaultAddress(false);
        Set<ConstraintViolation<AddressRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void lombokDataMethodsWorks() {
        AddressRequestDTO dto1 = createValidDTO();
        AddressRequestDTO dto2 = createValidDTO();

        
        assertEquals(dto1, dto2); 
        assertEquals(dto1.hashCode(), dto2.hashCode());

        
        dto1.setCountry("Chile"); 
        assertEquals("Chile", dto1.getCountry());
        assertTrue(dto1.isDefaultAddress());

        
        assertNotEquals(dto1, dto2); 

        
        assertTrue(dto1.toString().contains("28001"));
    }

    @Test
    void lombokNoArgsConstructorWorks() {
        AddressRequestDTO dto = new AddressRequestDTO();
        assertNotNull(dto);
    }
}