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

    // Usamos el mismo secreto que en tu application.properties
    private final String testSecret = "SGVsbG8sIHRoaXMgaXMgYSBzZWNyZXQga2V5IGZvciBKV1QgYXV0aGVudGljYXRpb24u";
    private final int testExpirationMs = 60000; // 1 minuto

    @BeforeEach
    void setUp() {
        // Inyectamos los valores de @Value manualmente
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", testSecret);
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationMs", testExpirationMs);
    }

    @Test
    void generateToken_ShouldCreateValidToken() {
        // Given
        String username = "admin";
        when(authentication.getName()).thenReturn(username);

        // When
        String token = jwtTokenProvider.generateToken(authentication);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertEquals(username, jwtTokenProvider.getUsernameFromToken(token));
    }

    @Test
    void validateToken_ShouldReturnTrue_ForValidToken() {
        // Given
        when(authentication.getName()).thenReturn("admin");
        String token = jwtTokenProvider.generateToken(authentication);

        // When
        boolean isValid = jwtTokenProvider.validateToken(token);

        // Then
        assertTrue(isValid);
    }

    @Test
    void validateToken_ShouldReturnFalse_ForInvalidSignature() {
        // Given
        String badToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTcxNTM0MjAwOCwiZXhwIjoxNzE1MzQyMDY4fQ.invalidSignature";

        // When
        boolean isValid = jwtTokenProvider.validateToken(badToken);

        // Then
        assertFalse(isValid);
    }

    @Test
    void validateToken_ShouldReturnFalse_ForMalformedToken() {
        // Given
        String malformedToken = "un-token-malformado";

        // When
        boolean isValid = jwtTokenProvider.validateToken(malformedToken);

        // Then
        assertFalse(isValid);
    }

    @Test
    void validateToken_ShouldReturnFalse_ForExpiredToken() throws InterruptedException {
        // Given
        // Generamos un token con expiración de 1ms
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationMs", 1);
        when(authentication.getName()).thenReturn("admin");
        String expiredToken = jwtTokenProvider.generateToken(authentication);

        // Esperamos a que expire
        Thread.sleep(50);

        // When
        boolean isValid = jwtTokenProvider.validateToken(expiredToken);

        // Then
        assertFalse(isValid);
    }

    @Test
    void getUsernameFromToken_ShouldReturnUsername() {
        // Given
        when(authentication.getName()).thenReturn("testuser");
        String token = jwtTokenProvider.generateToken(authentication);

        // When
        String username = jwtTokenProvider.getUsernameFromToken(token);

        // Then
        assertEquals("testuser", username);
    }
}