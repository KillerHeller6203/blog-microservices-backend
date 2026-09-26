package com.rickzzy.blog.user.security;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

class JwtServiceTest {

    private static final String SECRET = "test-only-secret-key-that-is-at-least-32-bytes-long";

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", SECRET);
    }

    private UserDetails user(String email) {
        return User.withUsername(email).password("x").roles("USER").build();
    }

    @Test
    void generatedToken_containsUsernameAsSubject() {
        String token = jwtService.generateToken("rick@example.com");

        assertThat(jwtService.extractUsername(token)).isEqualTo("rick@example.com");
    }

    @Test
    void generatedToken_expiresInOneHour() {
        long now = System.currentTimeMillis();
        String token = jwtService.generateToken("rick@example.com");

        Date expiration = jwtService.extractExpiration(token);

        assertThat(expiration.getTime()).isCloseTo(now + 60 * 60 * 1000, within(5_000L));
    }

    @Test
    void token_isValidForItsOwner() {
        String token = jwtService.generateToken("rick@example.com");

        assertThat(jwtService.isTokenValid(token, user("rick@example.com"))).isTrue();
    }

    @Test
    void token_isNotValidForAnotherUser() {
        String token = jwtService.generateToken("rick@example.com");

        assertThat(jwtService.isTokenValid(token, user("someone-else@example.com"))).isFalse();
    }

    @Test
    void tamperedToken_isRejected() {
        String[] parts = jwtService.generateToken("rick@example.com").split("\\.");
        // Swap the payload for one claiming to be a different user, keeping the original signature
        String forgedPayload = Base64.getUrlEncoder().withoutPadding()
                .encodeToString("{\"sub\":\"attacker@example.com\"}".getBytes(StandardCharsets.UTF_8));
        String tampered = parts[0] + "." + forgedPayload + "." + parts[2];

        assertThatThrownBy(() -> jwtService.extractUsername(tampered))
                .isInstanceOf(JwtException.class);
    }

    @Test
    void tokenSignedWithDifferentSecret_isRejected() {
        JwtService otherService = new JwtService();
        ReflectionTestUtils.setField(otherService, "secretKey",
                "a-completely-different-secret-key-also-32-bytes-plus");
        String foreignToken = otherService.generateToken("rick@example.com");

        assertThatThrownBy(() -> jwtService.extractUsername(foreignToken))
                .isInstanceOf(JwtException.class);
    }
}
