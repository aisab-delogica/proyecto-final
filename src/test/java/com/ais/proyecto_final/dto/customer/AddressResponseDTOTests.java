package com.ais.proyecto_final.dto.customer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AddressResponseDTOTests {

    private final Long ID = 1L;
    private final String LINE1 = "Line 1";
    private final String LINE2 = "Line 2";
    private final String CITY = "City";
    private final String POSTAL_CODE = "12345";
    private final String COUNTRY = "Country";

    @Test
    void testNoArgsConstructor() {
        AddressResponseDTO dto = new AddressResponseDTO();
        assertNotNull(dto);
        assertNull(dto.getId());
    }

    @Test
    void testAllArgsConstructor() {
        AddressResponseDTO dto = new AddressResponseDTO(ID, LINE1, LINE2, CITY, POSTAL_CODE, COUNTRY, true);

        assertEquals(ID, dto.getId());
        assertEquals(LINE1, dto.getLine1());
        assertEquals(LINE2, dto.getLine2());
        assertEquals(CITY, dto.getCity());
        assertEquals(POSTAL_CODE, dto.getPostalCode());
        assertEquals(COUNTRY, dto.getCountry());
        assertTrue(dto.isDefaultAddress());
    }

    @Test
    void testBuilder() {
        AddressResponseDTO dto = AddressResponseDTO.builder()
                .id(ID)
                .line1(LINE1)
                .line2(LINE2)
                .city(CITY)
                .postalCode(POSTAL_CODE)
                .country(COUNTRY)
                .defaultAddress(true)
                .build();

        assertEquals(ID, dto.getId());
        assertEquals(LINE1, dto.getLine1());
        assertTrue(dto.isDefaultAddress());
    }

    @Test
    void testGettersAndSetters() {
        AddressResponseDTO dto = new AddressResponseDTO();

        dto.setId(ID);
        dto.setLine1(LINE1);
        dto.setLine2(LINE2);
        dto.setCity(CITY);
        dto.setPostalCode(POSTAL_CODE);
        dto.setCountry(COUNTRY);
        dto.setDefaultAddress(false);

        assertEquals(ID, dto.getId());
        assertEquals(LINE1, dto.getLine1());
        assertEquals(LINE2, dto.getLine2());
        assertEquals(CITY, dto.getCity());
        assertEquals(POSTAL_CODE, dto.getPostalCode());
        assertEquals(COUNTRY, dto.getCountry());
        assertFalse(dto.isDefaultAddress());
    }

    @Test
    void testEqualsAndHashCode() {
        AddressResponseDTO dto1 = new AddressResponseDTO(ID, LINE1, LINE2, CITY, POSTAL_CODE, COUNTRY, true);
        AddressResponseDTO dto2 = new AddressResponseDTO(ID, LINE1, LINE2, CITY, POSTAL_CODE, COUNTRY, true);
        AddressResponseDTO dto3 = new AddressResponseDTO(2L, "Other", null, null, null, null, false);

        assertEquals(dto1, dto1);
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());

        assertNotEquals(dto1, dto3);
        assertNotEquals(dto1.hashCode(), dto3.hashCode());

        assertNotEquals(dto1, null);
        assertNotEquals(dto1, new Object());
    }

    @Test
    void testToString() {
        AddressResponseDTO dto = new AddressResponseDTO(ID, LINE1, LINE2, CITY, POSTAL_CODE, COUNTRY, true);
        String str = dto.toString();
        assertNotNull(str);
        assertTrue(str.contains(LINE1));
        assertTrue(str.contains(POSTAL_CODE));
    }
}