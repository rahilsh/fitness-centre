package com.rsh.fitness_centre.integration;

import com.rsh.fitness_centre.entity.User;
import com.rsh.fitness_centre.entity.UserRole;
import com.rsh.fitness_centre.repository.UserRepository;
import com.rsh.fitness_centre.repository.RefreshTokenRepository;
import com.rsh.fitness_centre.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for authentication service.
 */
@SpringBootTest
@ActiveProfiles("test")
class AuthServiceIntegrationTest {

  @Autowired
  private UserService userService;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RefreshTokenRepository refreshTokenRepository;

  @BeforeEach
  void setUp() {
    refreshTokenRepository.deleteAll();
    userRepository.deleteAll();
  }

  @Test
  void testRegisterUser() {
    User user = userService.registerUser("john@example.com", "SecurePass123", "John Doe");

    assertNotNull(user);
    assertNotNull(user.getId());
    assertEquals("john@example.com", user.getEmail());
    assertEquals("John Doe", user.getName());
    assertTrue(user.isEnabled());
    assertTrue(user.getRoles().contains(UserRole.USER));
    assertNotNull(user.getPasswordHash());
    assertNotEquals("SecurePass123", user.getPasswordHash());
  }

  @Test
  void testRegisterUserDuplicateEmail() {
    userService.registerUser("john@example.com", "SecurePass123", "John Doe");

    assertThrows(IllegalArgumentException.class,
        () -> userService.registerUser("john@example.com", "AnotherPass456", "Another John"));
  }

  @Test
  void testAuthenticateUserSuccess() {
    userService.registerUser("john@example.com", "SecurePass123", "John Doe");

    User authenticatedUser = userService.authenticateUser("john@example.com", "SecurePass123");

    assertNotNull(authenticatedUser);
    assertEquals("john@example.com", authenticatedUser.getEmail());
    assertNotNull(authenticatedUser.getLastLogin());
  }

  @Test
  void testAuthenticateUserInvalidPassword() {
    userService.registerUser("john@example.com", "SecurePass123", "John Doe");

    assertThrows(IllegalArgumentException.class,
        () -> userService.authenticateUser("john@example.com", "WrongPassword"));
  }

  @Test
  void testAuthenticateUserNotFound() {
    assertThrows(IllegalArgumentException.class,
        () -> userService.authenticateUser("nonexistent@example.com", "AnyPassword"));
  }

  @Test
  void testAuthenticateUserDisabled() {
    User user = userService.registerUser("john@example.com", "SecurePass123", "John Doe");
    user.setEnabled(false);
    userRepository.save(user);

    assertThrows(IllegalArgumentException.class,
        () -> userService.authenticateUser("john@example.com", "SecurePass123"));
  }

  @Test
  void testGetUserByEmail() {
    userService.registerUser("john@example.com", "SecurePass123", "John Doe");

    User user = userService.getUserByEmail("john@example.com");

    assertNotNull(user);
    assertEquals("john@example.com", user.getEmail());
    assertEquals("John Doe", user.getName());
  }

  @Test
  void testGetUserWithRoles() {
    User registered = userService.registerUser("john@example.com", "SecurePass123", "John Doe");

    User user = userService.getUserWithRoles(registered.getId());

    assertNotNull(user);
    assertNotNull(user.getRoles());
    assertTrue(user.getRoles().contains(UserRole.USER));
  }

  @Test
  void testGetUserByEmailNotFound() {
    User user = userService.getUserByEmail("nonexistent@example.com");

    assertNull(user);
  }

  @Test
  void testPasswordHashing() {
    User user = userService.registerUser("john@example.com", "SecurePass123", "John Doe");

    // Password should be hashed, not stored as plaintext
    assertNotEquals("SecurePass123", user.getPasswordHash());
    assertNotNull(user.getPasswordHash());
    assertTrue(user.getPasswordHash().length() > 20);
  }

  @Test
  void testLastLoginUpdate() {
    userService.registerUser("john@example.com", "SecurePass123", "John Doe");
    User user1 = userRepository.findByEmail("john@example.com").get();
    assertNull(user1.getLastLogin());

    User authenticatedUser = userService.authenticateUser("john@example.com", "SecurePass123");
    assertNotNull(authenticatedUser.getLastLogin());
  }

  @Test
  void testUserDefaultRole() {
    User user = userService.registerUser("john@example.com", "SecurePass123", "John Doe");

    assertTrue(user.getRoles().contains(UserRole.USER));
    assertEquals(1, user.getRoles().size());
  }

  @Test
  void testUserAccountEnabled() {
    User user = userService.registerUser("john@example.com", "SecurePass123", "John Doe");

    assertTrue(user.isEnabled());
  }
}
