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
    private final List<AddressResponseDTO> ADDRESSES = List.of(
            AddressResponseDTO.builder().id(1L).city("Madrid").build()
    );

    private CustomerResponseDTO createFullDTO() {
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

    @Test
    void testNoArgsConstructorAndSettersAndGetters() {
        CustomerResponseDTO dto = new CustomerResponseDTO();
        assertNotNull(dto);

        dto.setId(ID);
        dto.setFullName(FULL_NAME);
        dto.setEmail(EMAIL);
        dto.setPhone(PHONE);
        dto.setCreatedAt(NOW);
        dto.setUpdatedAt(NOW);
        dto.setAddresses(ADDRESSES);

        assertEquals(ID, dto.getId());
        assertEquals(FULL_NAME, dto.getFullName());
        assertEquals(EMAIL, dto.getEmail());
        assertEquals(PHONE, dto.getPhone());
        assertEquals(NOW, dto.getCreatedAt());
        assertEquals(NOW, dto.getUpdatedAt());
        assertEquals(ADDRESSES, dto.getAddresses());
    }

    @Test
    void testEqualsAndHashCodeExhaustive() {
        CustomerResponseDTO dto1 = createFullDTO();
        CustomerResponseDTO dto2 = createFullDTO();

        assertTrue(dto1.equals(dto1));
        assertTrue(dto1.equals(dto2) && dto2.equals(dto1));
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertFalse(dto1.equals(null));
        assertFalse(dto1.equals(new Object()));

        CustomerResponseDTO dto_id = createFullDTO(); dto_id.setId(2L);
        assertFalse(dto1.equals(dto_id));
        assertNotEquals(dto1.hashCode(), dto_id.hashCode());

        CustomerResponseDTO dto_name = createFullDTO(); dto_name.setFullName("D");
        assertFalse(dto1.equals(dto_name));
        assertNotEquals(dto1.hashCode(), dto_name.hashCode());

        CustomerResponseDTO dto_email = createFullDTO(); dto_email.setEmail("D");
        assertFalse(dto1.equals(dto_email));
        assertNotEquals(dto1.hashCode(), dto_email.hashCode());

        CustomerResponseDTO dto_phone = createFullDTO(); dto_phone.setPhone("D");
        assertFalse(dto1.equals(dto_phone));
        assertNotEquals(dto1.hashCode(), dto_phone.hashCode());

        CustomerResponseDTO dto_created = createFullDTO(); dto_created.setCreatedAt(LocalDateTime.MIN);
        assertFalse(dto1.equals(dto_created));
        assertNotEquals(dto1.hashCode(), dto_created.hashCode());

        CustomerResponseDTO dto_updated = createFullDTO(); dto_updated.setUpdatedAt(LocalDateTime.MIN);
        assertFalse(dto1.equals(dto_updated));
        assertNotEquals(dto1.hashCode(), dto_updated.hashCode());

        CustomerResponseDTO dto_addr = createFullDTO(); dto_addr.setAddresses(List.of());
        assertFalse(dto1.equals(dto_addr));
        assertNotEquals(dto1.hashCode(), dto_addr.hashCode());
    }

    @Test
    void testToString() {
        CustomerResponseDTO dto = createFullDTO();
        String str = dto.toString();
        assertNotNull(str);
        assertTrue(str.contains(FULL_NAME));
        assertTrue(str.contains(EMAIL));
    }
}