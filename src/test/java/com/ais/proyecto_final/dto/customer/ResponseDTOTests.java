package com.ais.proyecto_final.dto.customer;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ResponseDTOTests {

    private AddressResponseDTO createAddress(Long id, String line1) {
        return AddressResponseDTO.builder()
                .id(id)
                .line1(line1)
                .line2("Apto")
                .city("City")
                .postalCode("12345")
                .country("Country")
                .defaultAddress(true)
                .build();
    }

    @Test
    void addressResponseDTOLombokBuilderAndGettersWorks() {
        AddressResponseDTO dto = createAddress(1L, "Line 1");
        assertEquals(1L, dto.getId());
        assertTrue(dto.isDefaultAddress());
    }

    @Test
    void addressResponseDTOLombokAllArgsConstructorWorks() {
        AddressResponseDTO dto = new AddressResponseDTO(2L, "L1", "L2", "C", "PC", "CY", false);
        assertEquals(2L, dto.getId());
        assertFalse(dto.isDefaultAddress());
    }

    @Test
    void addressResponseDTOLombokNoArgsConstructorWorks() {
        AddressResponseDTO dto = new AddressResponseDTO();
        assertNotNull(dto);
        assertEquals(null, dto.getLine1());
    }

    @Test
    void addressResponseDTOLombokEqualsAndHashCodeWorks() {
        AddressResponseDTO dto1 = createAddress(1L, "Line 1");
        AddressResponseDTO dto2 = createAddress(1L, "Line 1");
        AddressResponseDTO dto3 = createAddress(2L, "Line 2");

        
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1, dto3);

        dto2.setLine2("Apto New");
        assertNotEquals(dto1, dto2);
    }

    @Test
    void addressResponseDTOLombokToStringWorks() {
        AddressResponseDTO dto = createAddress(1L, "Test String");
        assertTrue(dto.toString().contains("id=1"));
    }

    @Test
    void customerResponseDTOLombokGettersAndSettersWorks() {
        LocalDateTime now = LocalDateTime.now();
        List<AddressResponseDTO> addresses = Arrays.asList(createAddress(1L, "L1"));

        CustomerResponseDTO dto = new CustomerResponseDTO();
        dto.setId(2L);
        dto.setFullName("Test User");
        dto.setAddresses(addresses);

        assertEquals(2L, dto.getId());
        assertEquals("Test User", dto.getFullName());
        assertEquals(1, dto.getAddresses().size());
    }

    @Test
    void customerResponseDTOLombokNoArgsConstructorWorks() {
        CustomerResponseDTO dto = new CustomerResponseDTO();
        assertNotNull(dto);
    }

    @Test
    void customerResponseDTOLombokEqualsAndHashCodeWorks() {
        LocalDateTime time = LocalDateTime.now();
        CustomerResponseDTO dto1 = new CustomerResponseDTO();
        dto1.setId(1L);
        dto1.setCreatedAt(time);

        CustomerResponseDTO dto2 = new CustomerResponseDTO();
        dto2.setId(1L);
        dto2.setCreatedAt(time);

        
        
        dto1.setEmail("test@email.com");
        dto2.setEmail("test@email.com");

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());

        
        dto2.setPhone("123");
        assertNotEquals(dto1, dto2);
    }

    @Test
    void customerResponseDTOLombokToStringWorks() {
        CustomerResponseDTO dto = new CustomerResponseDTO();
        dto.setFullName("Test String");
        assertTrue(dto.toString().contains("Test String"));
    }
}