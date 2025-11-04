package com.ais.proyecto_final.dto.customer;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CustomerResponseDTOTests {

    private final Long ID = 1L;
    private final String FULL_NAME = "Test User";
    private final String EMAIL = "test@mail.com";
    private final String PHONE = "123456";
    private final LocalDateTime NOW = LocalDateTime.now();
    private final List<AddressResponseDTO> ADDRESSES = List.of(new AddressResponseDTO());

    @Test
    void testNoArgsConstructorAndSettersAndGetters() {
        // Test No-Args Constructor
        CustomerResponseDTO dto = new CustomerResponseDTO();
        assertNotNull(dto);

        // Test Setters
        dto.setId(ID);
        dto.setFullName(FULL_NAME);
        dto.setEmail(EMAIL);
        dto.setPhone(PHONE);
        dto.setCreatedAt(NOW);
        dto.setUpdatedAt(NOW);
        dto.setAddresses(ADDRESSES);

        // Test Getters
        assertEquals(ID, dto.getId());
        assertEquals(FULL_NAME, dto.getFullName());
        assertEquals(EMAIL, dto.getEmail());
        assertEquals(PHONE, dto.getPhone());
        assertEquals(NOW, dto.getCreatedAt());
        assertEquals(NOW, dto.getUpdatedAt());
        assertEquals(ADDRESSES, dto.getAddresses());
    }

    @Test
    void testEqualsAndHashCode() {
        CustomerResponseDTO dto1 = createDTO();
        CustomerResponseDTO dto2 = createDTO();

        CustomerResponseDTO dto3 = new CustomerResponseDTO();
        dto3.setId(2L);

        assertEquals(dto1, dto1); // Self
        assertEquals(dto1, dto2); // Equal
        assertEquals(dto1.hashCode(), dto2.hashCode()); // HashCode

        assertNotEquals(dto1, dto3); // Different
        assertNotEquals(dto1.hashCode(), dto3.hashCode()); // HashCode different

        assertNotEquals(dto1, null); // Null
        assertNotEquals(dto1, new Object()); // Different class
    }

    @Test
    void testToString() {
        CustomerResponseDTO dto = createDTO();
        String str = dto.toString();
        assertNotNull(str);
        assertTrue(str.contains(FULL_NAME));
        assertTrue(str.contains(EMAIL));
    }

    // Helper para crear un DTO poblado
    private CustomerResponseDTO createDTO() {
        CustomerResponseDTO dto = new CustomerResponseDTO();
        dto.setId(ID);
        dto.setFullName(FULL_NAME);
        dto.setEmail(EMAIL);
        dto.setPhone(PHONE);
        dto.setCreatedAt(NOW);
        dto.setUpdatedAt(NOW);
        dto.setAddresses(ADDRESSES);
        return dto;
    }
}