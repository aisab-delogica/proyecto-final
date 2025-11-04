package com.ais.proyecto_final.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

    @InjectMocks
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private Authentication authentication;

    private final String testSecret = "SGVsbG8sIHRoaXMgaXMgYSBzZWNyZXQga2V5IGZvciBKV1QgYXV0aGVudGljYXRpb24u";
    private final int testExpirationMs = 60000;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", testSecret);
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationMs", testExpirationMs);
    }

    @Test
    void generateToken_ShouldCreateValidToken() {
        String username = "admin";
        when(authentication.getName()).thenReturn(username);

        String token = jwtTokenProvider.generateToken(authentication);
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertEquals(username, jwtTokenProvider.getUsernameFromToken(token));
    }

    @Test
    void validateToken_ShouldReturnTrue_ForValidToken() {
        when(authentication.getName()).thenReturn("admin");
        String token = jwtTokenProvider.generateToken(authentication);
        boolean isValid = jwtTokenProvider.validateToken(token);
        assertTrue(isValid);
    }

    @Test
    void validateToken_ShouldReturnFalse_ForInvalidSignature() {
        
        String badToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTcxNTM0MjAwOCwiZXhwIjoxNzE1MzQyMDY4fQ.invalidSignature";

        
        boolean isValid = jwtTokenProvider.validateToken(badToken);

        
        assertFalse(isValid);
    }

    @Test
    void validateToken_ShouldReturnFalse_ForMalformedToken() {
        
        String malformedToken = "un-token-malformado";

        
        boolean isValid = jwtTokenProvider.validateToken(malformedToken);

        
        assertFalse(isValid);
    }

    @Test
    void validateToken_ShouldReturnFalse_ForExpiredToken() throws InterruptedException {
        
        // token con expiración de 1ms
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationMs", 1);
        when(authentication.getName()).thenReturn("admin");
        String expiredToken = jwtTokenProvider.generateToken(authentication);

        Thread.sleep(50);

        
        boolean isValid = jwtTokenProvider.validateToken(expiredToken);

        
        assertFalse(isValid);
    }

    @Test
    void getUsernameFromToken_ShouldReturnUsername() {
        
        when(authentication.getName()).thenReturn("testuser");
        String token = jwtTokenProvider.generateToken(authentication);

        
        String username = jwtTokenProvider.getUsernameFromToken(token);

        assertEquals("testuser", username);
    }
}