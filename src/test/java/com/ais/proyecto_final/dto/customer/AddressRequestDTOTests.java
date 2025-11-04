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
    void testFullLombokCoverage() {
        AddressRequestDTO dto1 = new AddressRequestDTO();
        assertNotNull(dto1);

        dto1.setLine1("Line 1");
        dto1.setLine2("Line 2");
        dto1.setCity("City");
        dto1.setPostalCode("12345");
        dto1.setCountry("Country");
        dto1.setDefaultAddress(true);

        assertEquals("Line 1", dto1.getLine1());
        assertEquals("Line 2", dto1.getLine2());
        assertEquals("City", dto1.getCity());
        assertEquals("12345", dto1.getPostalCode());
        assertEquals("Country", dto1.getCountry());
        assertTrue(dto1.isDefaultAddress());

        assertNotNull(dto1.toString());
        assertTrue(dto1.toString().contains("Line 1"));

        AddressRequestDTO dto2 = createValidDTO();
        dto2.setLine1("Line 1");
        dto2.setLine2("Line 2");
        dto2.setCity("City");
        dto2.setPostalCode("12345");
        dto2.setCountry("Country");
        dto2.setDefaultAddress(true);

        assertTrue(dto1.equals(dto1));
        assertTrue(dto1.equals(dto2) && dto2.equals(dto1)); // equals
        assertEquals(dto1.hashCode(), dto2.hashCode()); // hashCode

        assertFalse(dto1.equals(null)); // null
        assertFalse(dto1.equals(new Object())); // otra clase

        AddressRequestDTO dto3_line1 = createValidDTO(); dto3_line1.setLine1("Diff");
        assertFalse(dto1.equals(dto3_line1));

        AddressRequestDTO dto3_line2 = createValidDTO(); dto3_line2.setLine2("Diff");
        assertFalse(dto1.equals(dto3_line2));

        AddressRequestDTO dto3_city = createValidDTO(); dto3_city.setCity("Diff");
        assertFalse(dto1.equals(dto3_city));

        AddressRequestDTO dto3_postal = createValidDTO(); dto3_postal.setPostalCode("Diff");
        assertFalse(dto1.equals(dto3_postal));

        AddressRequestDTO dto3_country = createValidDTO(); dto3_country.setCountry("Diff");
        assertFalse(dto1.equals(dto3_country));

        AddressRequestDTO dto3_default = createValidDTO(); dto3_default.setDefaultAddress(false);
        assertFalse(dto1.equals(dto3_default));

        assertNotEquals(dto1.hashCode(), dto3_line1.hashCode());
    }
}