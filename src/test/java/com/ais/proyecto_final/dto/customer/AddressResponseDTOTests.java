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

    private AddressResponseDTO createFullDto() {
        return new AddressResponseDTO(ID, LINE1, LINE2, CITY, POSTAL_CODE, COUNTRY, true);
    }

    @Test
    void testNoArgsConstructor() {
        AddressResponseDTO dto = new AddressResponseDTO();
        assertNotNull(dto);
        assertNull(dto.getId());
    }

    @Test
    void testAllArgsConstructor() {
        AddressResponseDTO dto = createFullDto();

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
    void testSettersAndGetters() {
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
    void testEqualsAndHashCodeExhaustive() {
        AddressResponseDTO dto1 = createFullDto();
        AddressResponseDTO dto2 = createFullDto();

        assertTrue(dto1.equals(dto1));
        assertTrue(dto1.equals(dto2) && dto2.equals(dto1));
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertFalse(dto1.equals(null));
        assertFalse(dto1.equals(new Object()));

        AddressResponseDTO dto_id = createFullDto(); dto_id.setId(2L);
        assertFalse(dto1.equals(dto_id));
        assertNotEquals(dto1.hashCode(), dto_id.hashCode());

        AddressResponseDTO dto_l1 = createFullDto(); dto_l1.setLine1("D");
        assertFalse(dto1.equals(dto_l1));
        assertNotEquals(dto1.hashCode(), dto_l1.hashCode());

        AddressResponseDTO dto_l2 = createFullDto(); dto_l2.setLine2("D");
        assertFalse(dto1.equals(dto_l2));
        assertNotEquals(dto1.hashCode(), dto_l2.hashCode());

        AddressResponseDTO dto_city = createFullDto(); dto_city.setCity("D");
        assertFalse(dto1.equals(dto_city));
        assertNotEquals(dto1.hashCode(), dto_city.hashCode());

        AddressResponseDTO dto_post = createFullDto(); dto_post.setPostalCode("D");
        assertFalse(dto1.equals(dto_post));
        assertNotEquals(dto1.hashCode(), dto_post.hashCode());

        AddressResponseDTO dto_country = createFullDto(); dto_country.setCountry("D");
        assertFalse(dto1.equals(dto_country));
        assertNotEquals(dto1.hashCode(), dto_country.hashCode());

        AddressResponseDTO dto_default = createFullDto(); dto_default.setDefaultAddress(false);
        assertFalse(dto1.equals(dto_default));
        assertNotEquals(dto1.hashCode(), dto_default.hashCode());
    }

    @Test
    void testEqualsAndHashCodeWithNulls() {
        AddressResponseDTO dto1 = new AddressResponseDTO();
        AddressResponseDTO dto2 = new AddressResponseDTO();

        assertTrue(dto1.equals(dto2));
        assertEquals(dto1.hashCode(), dto2.hashCode());

        dto1.setLine1("Test");
        assertFalse(dto1.equals(dto2));
        assertNotEquals(dto1.hashCode(), dto2.hashCode());

        dto2.setLine1("Test");
        assertTrue(dto1.equals(dto2));
        assertEquals(dto1.hashCode(), dto2.hashCode());

        dto1.setLine2("Test2");
        assertFalse(dto1.equals(dto2));
        assertNotEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testToString() {
        AddressResponseDTO dto = createFullDto();
        String str = dto.toString();
        assertNotNull(str);
        assertTrue(str.contains(LINE1));
        assertTrue(str.contains(POSTAL_CODE));
    }

    @Test
    void testBuilderToString() {
        String builderStr = AddressResponseDTO.builder().id(1L).line1("Test").toString();
        assertNotNull(builderStr);
        assertTrue(builderStr.contains("Test"));
    }
}