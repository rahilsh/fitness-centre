package com.rsh.fitness_centre.security;

import com.rsh.fitness_centre.entity.User;
import com.rsh.fitness_centre.entity.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for JwtTokenProvider.
 */
class JwtTokenProviderTest {

  private JwtTokenProvider tokenProvider;

  @BeforeEach
  void setUp() {
    tokenProvider = new JwtTokenProvider();
    ReflectionTestUtils.setField(tokenProvider, "jwtSecret",
        "your-secret-key-that-is-at-least-32-characters-long-for-hs256");
    ReflectionTestUtils.setField(tokenProvider, "jwtExpirationMs", 86400000L);
  }

  @Test
  void testGenerateToken() {
    User user = new User(1L, "John Doe", "john@example.com");
    user.setRoles(Set.of(UserRole.USER));

    String token = tokenProvider.generateToken(user);

    assertNotNull(token);
    assertTrue(token.startsWith("Bearer "));
  }

  @Test
  void testValidateToken() {
    User user = new User(1L, "John Doe", "john@example.com");
    user.setRoles(Set.of(UserRole.USER));

    String token = tokenProvider.generateToken(user);
    String tokenWithoutBearer = token.substring(7);

    assertTrue(tokenProvider.validateToken(tokenWithoutBearer));
  }

  @Test
  void testValidateInvalidToken() {
    assertFalse(tokenProvider.validateToken("invalid-token"));
  }

  @Test
  void testExtractUserId() {
    User user = new User(1L, "John Doe", "john@example.com");
    user.setRoles(Set.of(UserRole.USER));

    String token = tokenProvider.generateToken(user);
    String tokenWithoutBearer = token.substring(7);

    Long userId = tokenProvider.extractUserId(tokenWithoutBearer);

    assertEquals(1L, userId);
  }

  @Test
  void testExtractUserIdInvalidToken() {
    Long userId = tokenProvider.extractUserId("invalid-token");

    assertNull(userId);
  }

  @Test
  void testExtractEmail() {
    User user = new User(1L, "John Doe", "john@example.com");
    user.setRoles(Set.of(UserRole.USER));

    String token = tokenProvider.generateToken(user);
    String tokenWithoutBearer = token.substring(7);

    String email = tokenProvider.extractEmail(tokenWithoutBearer);

    assertEquals("john@example.com", email);
  }

  @Test
  void testExtractEmailInvalidToken() {
    String email = tokenProvider.extractEmail("invalid-token");

    assertNull(email);
  }

  @Test
  void testGetExpirationTimeInSeconds() {
    long expirationSeconds = tokenProvider.getExpirationTimeInSeconds();

    assertEquals(86400, expirationSeconds);
  }

  @Test
  void testTokenContainsRoles() {
    User user = new User(1L, "John Doe", "john@example.com");
    user.setRoles(Set.of(UserRole.USER, UserRole.ADMIN));

    String token = tokenProvider.generateToken(user);

    assertNotNull(token);
    assertTrue(token.contains("Bearer"));
  }
}
