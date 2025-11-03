package com.ais.proyecto_final.dto;

import com.ais.proyecto_final.dto.auth.LoginRequestDTO;
import com.ais.proyecto_final.dto.auth.LoginResponseDTO;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LoginDtoTest {

    @Test
    void loginRequestDTO_ShouldCoverAllLombokMethods() {
        
        LoginRequestDTO dto1 = new LoginRequestDTO("testuser", "securepass");

        assertNotNull(dto1);
        assertEquals("testuser", dto1.getUsername());
        assertEquals("securepass", dto1.getPassword());

        
        dto1.setUsername("newuser");
        dto1.setPassword("newpass");
        assertEquals("newuser", dto1.getUsername());
        assertEquals("newpass", dto1.getPassword());

        
        LoginRequestDTO dto2 = new LoginRequestDTO();
        assertNotNull(dto2);

        
        LoginRequestDTO dto3 = new LoginRequestDTO("userA", "passA");
        LoginRequestDTO dto4 = new LoginRequestDTO("userA", "passA");
        LoginRequestDTO dto5 = new LoginRequestDTO("userB", "passA");

        assertEquals(dto3, dto4);
        assertEquals(dto3.hashCode(), dto4.hashCode());
        assertNotEquals(dto3, dto5);
        assertNotNull(dto3.toString());
    }

    @Test
    void loginResponseDTO_ShouldCoverAllLombokMethods() {
        
        LoginResponseDTO dto1 = new LoginResponseDTO("token12345");

        assertNotNull(dto1);
        assertEquals("token12345", dto1.getAccessToken());
        assertEquals("Bearer", dto1.getTokenType());

        
        dto1.setAccessToken("newToken");
        assertEquals("newToken", dto1.getAccessToken());

        
        LoginResponseDTO dto2 = new LoginResponseDTO();
        assertNotNull(dto2);

        
        LoginResponseDTO dto3 = new LoginResponseDTO("tokenX");
        LoginResponseDTO dto4 = new LoginResponseDTO("tokenX");
        LoginResponseDTO dto5 = new LoginResponseDTO("tokenY");

        assertEquals(dto3, dto4);
        assertEquals(dto3.hashCode(), dto4.hashCode());
        assertNotEquals(dto3, dto5);
        assertNotNull(dto3.toString());
    }
}