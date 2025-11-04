package com.ais.proyecto_final.dto.customer;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

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
        dto.setPhone("123456789");
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
        assertEquals(1, violations.size());
        assertEquals("El nombre es obligatorio", violations.iterator().next().getMessage());
    }

    @Test
    void whenFullNameIsTooLong_thenViolationOccurs() {
        CustomerRequestDTO dto = createValidDTO();
        dto.setFullName("A".repeat(121));
        Set<ConstraintViolation<CustomerRequestDTO>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("El nombre no puede tener más de 120 caracteres", violations.iterator().next().getMessage());
    }

    @Test
    void whenEmailIsBlank_thenViolationOccurs() {
        CustomerRequestDTO dto = createValidDTO();
        dto.setEmail("");
        Set<ConstraintViolation<CustomerRequestDTO>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("El email es obligatorio", violations.iterator().next().getMessage());
    }

    @Test
    void whenEmailFormatIsInvalid_thenViolationOccurs() {
        CustomerRequestDTO dto = createValidDTO();
        dto.setEmail("invalid-email");
        Set<ConstraintViolation<CustomerRequestDTO>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("El formato del email no es valido", violations.iterator().next().getMessage());
    }

    @Test
    void whenEmailIsTooLong_thenViolationOccurs() {
        CustomerRequestDTO dto = createValidDTO();
        dto.setEmail("a".repeat(150) + "@example.com");
        Set<ConstraintViolation<CustomerRequestDTO>> violations = validator.validate(dto);

        assertEquals(2, violations.size());

        Set<String> messages = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());

        assertTrue(messages.contains("El email no puede tener más de 160 caracteres"));
        assertTrue(messages.contains("El formato del email no es valido"));
    }

    @Test
    void whenPhoneIsBlank_thenViolationOccurs() {
        CustomerRequestDTO dto = createValidDTO();
        dto.setPhone(" ");
        Set<ConstraintViolation<CustomerRequestDTO>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("El teléfono es obligatorio", violations.iterator().next().getMessage());
    }

    @Test
    void whenPhoneIsTooLong_thenViolationOccurs() {
        CustomerRequestDTO dto = createValidDTO();
        dto.setPhone("1".repeat(16));
        Set<ConstraintViolation<CustomerRequestDTO>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("El teléfono no puede tener más de 15 caracteres", violations.iterator().next().getMessage());
    }

    @Test
    void whenMultipleViolations_thenAllAreFound() {
        CustomerRequestDTO dto = new CustomerRequestDTO();
        dto.setFullName(" ");
        dto.setEmail("bad-email");
        dto.setPhone("1".repeat(20));

        Set<ConstraintViolation<CustomerRequestDTO>> violations = validator.validate(dto);
        assertEquals(3, violations.size());
    }



    @Test
    void testFullLombokCoverage() {
        CustomerRequestDTO dto1 = new CustomerRequestDTO();
        assertNotNull(dto1);

        dto1.setFullName("Test User");
        dto1.setEmail("test@user.com");
        dto1.setPhone("123456789");

        assertEquals("Test User", dto1.getFullName());
        assertEquals("test@user.com", dto1.getEmail());
        assertEquals("123456789", dto1.getPhone());

        assertNotNull(dto1.toString());
        assertTrue(dto1.toString().contains("Test User"));

        CustomerRequestDTO dto2 = new CustomerRequestDTO();
        dto2.setFullName("Test User");
        dto2.setEmail("test@user.com");
        dto2.setPhone("123456789");

        assertTrue(dto1.equals(dto1));
        assertTrue(dto1.equals(dto2) && dto2.equals(dto1));
        assertEquals(dto1.hashCode(), dto2.hashCode());

        assertFalse(dto1.equals(null));
        assertFalse(dto1.equals(new Object()));

        CustomerRequestDTO dto3_name = createValidDTO(); dto3_name.setFullName("Diff");
        assertFalse(dto1.equals(dto3_name));

        CustomerRequestDTO dto3_email = createValidDTO(); dto3_email.setEmail("Diff");
        assertFalse(dto1.equals(dto3_email));

        CustomerRequestDTO dto3_phone = createValidDTO(); dto3_phone.setPhone("Diff");
        assertFalse(dto1.equals(dto3_phone));

        assertNotEquals(dto1.hashCode(), dto3_phone.hashCode());
    }

    @Test
    void testEqualsAndHashCodeWithNulls() {
        CustomerRequestDTO dto1 = new CustomerRequestDTO();
        CustomerRequestDTO dto2 = new CustomerRequestDTO();

        assertTrue(dto1.equals(dto2));
        assertEquals(dto1.hashCode(), dto2.hashCode());

        dto1.setFullName("Test");
        assertFalse(dto1.equals(dto2));
        assertNotEquals(dto1.hashCode(), dto2.hashCode());

        dto2.setFullName("Test");
        assertTrue(dto1.equals(dto2));
        assertEquals(dto1.hashCode(), dto2.hashCode());

        dto1.setEmail("Test2");
        assertFalse(dto1.equals(dto2));
        assertNotEquals(dto1.hashCode(), dto2.hashCode());
    }
}