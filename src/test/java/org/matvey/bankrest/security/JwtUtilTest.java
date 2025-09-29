package org.matvey.bankrest.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    private JwtUtil jwtUtil;
    private final String testSecret = "mySecretKeymySecretKeymySecretKeymySecretKey";
    private final long testExpiration = 86400000; // 24 hours

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", testSecret);
        ReflectionTestUtils.setField(jwtUtil, "expiration", testExpiration);
    }

    @Test
    void generateToken_ShouldCreateValidToken() {
        // Given
        String username = "test@example.com";

        // When
        String token = jwtUtil.generateToken(username);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3); // JWT has 3 parts
    }

    @Test
    void extractUsername_ShouldReturnCorrectUsername() {
        // Given
        String username = "test@example.com";
        String token = jwtUtil.generateToken(username);

        // When
        String extractedUsername = jwtUtil.extractUsername(token);

        // Then
        assertEquals(username, extractedUsername);
    }

    @Test
    void extractExpiration_ShouldReturnFutureDate() {
        // Given
        String username = "test@example.com";
        String token = jwtUtil.generateToken(username);

        // When
        Date expiration = jwtUtil.extractExpiration(token);

        // Then
        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    void isTokenExpired_WhenTokenValid_ShouldReturnFalse() {
        // Given
        String username = "test@example.com";
        String token = jwtUtil.generateToken(username);

        // When
        boolean isExpired = jwtUtil.isTokenExpired(token);

        // Then
        assertFalse(isExpired);
    }

    @Test
    void validateToken_WhenTokenValidAndUsernameMatches_ShouldReturnTrue() {
        // Given
        String username = "test@example.com";
        String token = jwtUtil.generateToken(username);

        // When
        boolean isValid = jwtUtil.validateToken(token, username);

        // Then
        assertTrue(isValid);
    }

    @Test
    void validateToken_WhenUsernameDoesNotMatch_ShouldReturnFalse() {
        // Given
        String username = "test@example.com";
        String differentUsername = "different@example.com";
        String token = jwtUtil.generateToken(username);

        // When
        boolean isValid = jwtUtil.validateToken(token, differentUsername);

        // Then
        assertFalse(isValid);
    }

    @Test
    void validateToken_WhenTokenExpired_ShouldReturnFalse() {
        // Given
        String username = "test@example.com";
        
        // Create expired token manually
        SecretKey key = Keys.hmacShaKeyFor(testSecret.getBytes());
        String expiredToken = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis() - 2000))
                .setExpiration(new Date(System.currentTimeMillis() - 1000)) // Expired 1 second ago
                .signWith(key)
                .compact();

        // When
        boolean isValid = jwtUtil.validateToken(expiredToken, username);

        // Then
        assertFalse(isValid);
    }

    @Test
    void validateToken_WhenTokenMalformed_ShouldReturnFalse() {
        // Given
        String username = "test@example.com";
        String malformedToken = "invalid.token.format";

        // When
        boolean isValid = jwtUtil.validateToken(malformedToken, username);

        // Then
        assertFalse(isValid);
    }
}