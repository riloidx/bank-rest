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
    private final long testExpiration = 86400000;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", testSecret);
        ReflectionTestUtils.setField(jwtUtil, "expiration", testExpiration);
    }

    @Test
    void generateToken_ShouldCreateValidToken() {
        String username = "test@example.com";

        String token = jwtUtil.generateToken(username);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3);
    }

    @Test
    void extractUsername_ShouldReturnCorrectUsername() {
        String username = "test@example.com";
        String token = jwtUtil.generateToken(username);

        String extractedUsername = jwtUtil.extractUsername(token);
        assertEquals(username, extractedUsername);
    }

    @Test
    void extractExpiration_ShouldReturnFutureDate() {
        String username = "test@example.com";
        String token = jwtUtil.generateToken(username);

        Date expiration = jwtUtil.extractExpiration(token);
        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    void isTokenExpired_WhenTokenValid_ShouldReturnFalse() {
        String username = "test@example.com";
        String token = jwtUtil.generateToken(username);

        boolean isExpired = jwtUtil.isTokenExpired(token);
        assertFalse(isExpired);
    }

    @Test
    void validateToken_WhenTokenValidAndUsernameMatches_ShouldReturnTrue() {
        String username = "test@example.com";
        String token = jwtUtil.generateToken(username);

        boolean isValid = jwtUtil.validateToken(token, username);
        assertTrue(isValid);
    }

    @Test
    void validateToken_WhenUsernameDoesNotMatch_ShouldReturnFalse() {
        String username = "test@example.com";
        String differentUsername = "different@example.com";
        String token = jwtUtil.generateToken(username);

        boolean isValid = jwtUtil.validateToken(token, differentUsername);
        assertFalse(isValid);
    }

    @Test
    void validateToken_WhenTokenExpired_ShouldReturnFalse() {
        String username = "test@example.com";
        SecretKey key = Keys.hmacShaKeyFor(testSecret.getBytes());
        String expiredToken = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis() - 2000))
                .setExpiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(key)
                .compact();

        boolean isValid = jwtUtil.validateToken(expiredToken, username);
        assertFalse(isValid);
    }

    @Test
    void validateToken_WhenTokenMalformed_ShouldReturnFalse() {
        String username = "test@example.com";
        String malformedToken = "invalid.token.format";

        boolean isValid = jwtUtil.validateToken(malformedToken, username);
        assertFalse(isValid);
    }
}