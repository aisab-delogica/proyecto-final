package com.ais.proyecto_final.dto;

import com.ais.proyecto_final.dto.auth.LoginRequestDTO;
import com.ais.proyecto_final.dto.auth.LoginResponseDTO;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LoginDtoTest {

    @Test
    void loginRequestDTO_ShouldCoverAllLombokMethods() {

        LoginRequestDTO dto1 = new LoginRequestDTO("userA", "passA");


        LoginRequestDTO dto2 = new LoginRequestDTO();
        assertNotNull(dto2);
        dto2.setUsername("userA");
        dto2.setPassword("passA");


        assertEquals("userA", dto1.getUsername());
        assertEquals("passA", dto1.getPassword());


        LoginRequestDTO dto3 = new LoginRequestDTO("userA", "passA");
        LoginRequestDTO dto_diff_user = new LoginRequestDTO("userB", "passA");
        LoginRequestDTO dto_diff_pass = new LoginRequestDTO("userA", "passB");


        assertTrue(dto1.equals(dto1));
        assertTrue(dto1.equals(dto3));
        assertEquals(dto1.hashCode(), dto3.hashCode());


        assertFalse(dto1.equals(null));
        assertFalse(dto1.equals(new Object()));
        assertFalse(dto1.equals(dto_diff_user));
        assertFalse(dto_diff_pass.equals(dto1));


        assertNotEquals(dto1.hashCode(), dto_diff_user.hashCode());
        assertNotEquals(dto1.hashCode(), dto_diff_pass.hashCode());


        assertNotNull(dto1.toString());
        assertTrue(dto1.toString().contains("userA"));
    }

    @Test
    void loginResponseDTO_ShouldCoverAllLombokMethods() {

        LoginResponseDTO dto1 = new LoginResponseDTO("tokenA");


        LoginResponseDTO dto2 = new LoginResponseDTO();
        assertNotNull(dto2);
        dto2.setAccessToken("tokenA");


        assertEquals("tokenA", dto1.getAccessToken());
        assertEquals("Bearer", dto1.getTokenType());


        LoginResponseDTO dto3 = new LoginResponseDTO("tokenA");
        LoginResponseDTO dto_diff_token = new LoginResponseDTO("tokenB");
        LoginResponseDTO dto_null_token = new LoginResponseDTO(null);
        LoginResponseDTO dto_null_token_2 = new LoginResponseDTO(null);


        assertTrue(dto1.equals(dto1));
        assertTrue(dto1.equals(dto3));
        assertEquals(dto1.hashCode(), dto3.hashCode());


        assertFalse(dto1.equals(null));
        assertFalse(dto1.equals(new Object()));
        assertFalse(dto1.equals(dto_diff_token));
        assertFalse(dto_diff_token.equals(dto1));
        assertFalse(dto1.equals(dto_null_token));
        assertFalse(dto_null_token.equals(dto1));
        assertTrue(dto_null_token.equals(dto_null_token_2));
        assertEquals(dto_null_token.hashCode(), dto_null_token_2.hashCode());


        assertNotEquals(dto1.hashCode(), dto_diff_token.hashCode());


        assertNotNull(dto1.toString());
        assertTrue(dto1.toString().contains("tokenA"));
    }
}