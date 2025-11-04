package com.ais.proyecto_final.dto.customer;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

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

    // --- Tests de Validación (los tuyos, que están perfectos) ---

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

    // --- Test de Cobertura de Lombok @Data ---

    @Test
    void testLombokDataMethods() {
        // 1. Test No-Args Constructor
        AddressRequestDTO dto1 = new AddressRequestDTO();
        assertNotNull(dto1);

        // 2. Test Setters
        dto1.setLine1("Line 1");
        dto1.setLine2("Line 2");
        dto1.setCity("City");
        dto1.setPostalCode("12345");
        dto1.setCountry("Country");
        dto1.setDefaultAddress(true);

        // 3. Test Getters
        assertEquals("Line 1", dto1.getLine1());
        assertEquals("Line 2", dto1.getLine2());
        assertEquals("City", dto1.getCity());
        assertEquals("12345", dto1.getPostalCode());
        assertEquals("Country", dto1.getCountry());
        assertTrue(dto1.isDefaultAddress());

        // 4. Test equals() y hashCode()
        AddressRequestDTO dto2 = createValidDTO();
        dto2.setLine1("Line 1");
        dto2.setLine2("Line 2");
        dto2.setCity("City");
        dto2.setPostalCode("12345");
        dto2.setCountry("Country");
        dto2.setDefaultAddress(true);

        AddressRequestDTO dto3 = new AddressRequestDTO();
        dto3.setLine1("Different");

        assertEquals(dto1, dto1); // Self
        assertEquals(dto1, dto2); // Equal
        assertEquals(dto1.hashCode(), dto2.hashCode()); // HashCode
        assertNotEquals(dto1, dto3); // Different
        assertNotEquals(dto1.hashCode(), dto3.hashCode()); // HashCode different
        assertNotEquals(dto1, null); // Null
        assertNotEquals(dto1, new Object()); // Different class

        // 5. Test toString()
        assertNotNull(dto1.toString());
        assertTrue(dto1.toString().contains("Line 1"));
    }
}