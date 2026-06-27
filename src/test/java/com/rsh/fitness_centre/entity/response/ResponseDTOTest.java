package com.rsh.fitness_centre.entity.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for response DTOs - LoginResponse, UserResponse, ErrorResponse.
 */
@DisplayName("Response DTO Tests")
class ResponseDTOTest {

  // ==================== LoginResponse Tests ====================

  @Test
  @DisplayName("LoginResponse - Should create with builder")
  void testLoginResponseBuilder() {
    LoginResponse response = LoginResponse.builder()
        .token("Bearer eyJhbGciOiJIUzI1NiJ9...")
        .userId(1L)
        .email("john@example.com")
        .roles(Set.of("USER"))
        .expiresIn(86400L)
        .build();

    assertEquals("Bearer eyJhbGciOiJIUzI1NiJ9...", response.getToken());
    assertEquals(1L, response.getUserId());
    assertEquals("john@example.com", response.getEmail());
    assertEquals(Set.of("USER"), response.getRoles());
    assertEquals(86400L, response.getExpiresIn());
  }

  @Test
  @DisplayName("LoginResponse - Should create with all-arg constructor")
  void testLoginResponseAllArgConstructor() {
    LoginResponse response = new LoginResponse(
        "Bearer token123",
        2L,
        "jane@example.com",
        Set.of("USER", "ADMIN"),
        86400L
    );

    assertEquals("Bearer token123", response.getToken());
    assertEquals(2L, response.getUserId());
    assertEquals("jane@example.com", response.getEmail());
    assertEquals(2, response.getRoles().size());
    assertEquals(86400L, response.getExpiresIn());
  }

  @Test
  @DisplayName("LoginResponse - Should handle no-arg constructor")
  void testLoginResponseNoArgConstructor() {
    LoginResponse response = new LoginResponse();
    assertNotNull(response);
    assertNull(response.getToken());
    assertNull(response.getUserId());
  }

  @Test
  @DisplayName("LoginResponse - Should handle getters and setters for token")
  void testLoginResponseTokenGetterSetter() {
    LoginResponse response = new LoginResponse();
    String token = "Bearer newToken123";
    response.setToken(token);

    assertEquals(token, response.getToken());
  }

  @Test
  @DisplayName("LoginResponse - Should handle getters and setters for userId")
  void testLoginResponseUserIdGetterSetter() {
    LoginResponse response = new LoginResponse();
    response.setUserId(5L);

    assertEquals(5L, response.getUserId());
  }

  @Test
  @DisplayName("LoginResponse - Should handle getters and setters for email")
  void testLoginResponseEmailGetterSetter() {
    LoginResponse response = new LoginResponse();
    response.setEmail("test@example.com");

    assertEquals("test@example.com", response.getEmail());
  }

  @Test
  @DisplayName("LoginResponse - Should handle getters and setters for roles")
  void testLoginResponseRolesGetterSetter() {
    LoginResponse response = new LoginResponse();
    Set<String> roles = Set.of("USER", "ADMIN");
    response.setRoles(roles);

    assertEquals(roles, response.getRoles());
  }

  @Test
  @DisplayName("LoginResponse - Should handle getters and setters for expiresIn")
  void testLoginResponseExpiresInGetterSetter() {
    LoginResponse response = new LoginResponse();
    response.setExpiresIn(172800L);

    assertEquals(172800L, response.getExpiresIn());
  }

  @Test
  @DisplayName("LoginResponse - Should handle multiple updates")
  void testLoginResponseMultipleUpdates() {
    LoginResponse response = LoginResponse.builder()
        .token("Bearer initial")
        .userId(1L)
        .email("initial@example.com")
        .roles(Set.of("USER"))
        .expiresIn(86400L)
        .build();

    response.setToken("Bearer updated");
    response.setUserId(2L);
    response.setEmail("updated@example.com");
    response.setRoles(Set.of("ADMIN"));
    response.setExpiresIn(172800L);

    assertEquals("Bearer updated", response.getToken());
    assertEquals(2L, response.getUserId());
    assertEquals("updated@example.com", response.getEmail());
    assertEquals(Set.of("ADMIN"), response.getRoles());
    assertEquals(172800L, response.getExpiresIn());
  }

  // ==================== UserResponse Tests ====================

  @Test
  @DisplayName("UserResponse - Should create with builder")
  void testUserResponseBuilder() {
    LocalDateTime now = LocalDateTime.now();
    UserResponse response = UserResponse.builder()
        .userId(1L)
        .email("john@example.com")
        .name("John Doe")
        .roles(Set.of("USER"))
        .lastLogin(now)
        .createdAt(now)
        .enabled(true)
        .build();

    assertEquals(1L, response.getUserId());
    assertEquals("john@example.com", response.getEmail());
    assertEquals("John Doe", response.getName());
    assertEquals(Set.of("USER"), response.getRoles());
    assertEquals(now, response.getLastLogin());
    assertEquals(now, response.getCreatedAt());
    assertTrue(response.isEnabled());
  }

  @Test
  @DisplayName("UserResponse - Should create with all-arg constructor")
  void testUserResponseAllArgConstructor() {
    LocalDateTime now = LocalDateTime.now();
    UserResponse response = new UserResponse(
        2L,
        "jane@example.com",
        "Jane Doe",
        Set.of("USER", "ADMIN"),
        now,
        now,
        true
    );

    assertEquals(2L, response.getUserId());
    assertEquals("jane@example.com", response.getEmail());
    assertEquals("Jane Doe", response.getName());
    assertEquals(2, response.getRoles().size());
    assertTrue(response.isEnabled());
  }

  @Test
  @DisplayName("UserResponse - Should handle no-arg constructor")
  void testUserResponseNoArgConstructor() {
    UserResponse response = new UserResponse();
    assertNotNull(response);
    assertNull(response.getUserId());
  }

  @Test
  @DisplayName("UserResponse - Should handle getters and setters for userId")
  void testUserResponseUserIdGetterSetter() {
    UserResponse response = new UserResponse();
    response.setUserId(10L);

    assertEquals(10L, response.getUserId());
  }

  @Test
  @DisplayName("UserResponse - Should handle getters and setters for email")
  void testUserResponseEmailGetterSetter() {
    UserResponse response = new UserResponse();
    response.setEmail("newemail@example.com");

    assertEquals("newemail@example.com", response.getEmail());
  }

  @Test
  @DisplayName("UserResponse - Should handle getters and setters for name")
  void testUserResponseNameGetterSetter() {
    UserResponse response = new UserResponse();
    response.setName("New Name");

    assertEquals("New Name", response.getName());
  }

  @Test
  @DisplayName("UserResponse - Should handle getters and setters for roles")
  void testUserResponseRolesGetterSetter() {
    UserResponse response = new UserResponse();
    Set<String> roles = Set.of("USER");
    response.setRoles(roles);

    assertEquals(roles, response.getRoles());
  }

  @Test
  @DisplayName("UserResponse - Should handle getters and setters for lastLogin")
  void testUserResponseLastLoginGetterSetter() {
    UserResponse response = new UserResponse();
    LocalDateTime lastLogin = LocalDateTime.now();
    response.setLastLogin(lastLogin);

    assertEquals(lastLogin, response.getLastLogin());
  }

  @Test
  @DisplayName("UserResponse - Should handle getters and setters for createdAt")
  void testUserResponseCreatedAtGetterSetter() {
    UserResponse response = new UserResponse();
    LocalDateTime createdAt = LocalDateTime.now();
    response.setCreatedAt(createdAt);

    assertEquals(createdAt, response.getCreatedAt());
  }

  @Test
  @DisplayName("UserResponse - Should handle getters and setters for enabled")
  void testUserResponseEnabledGetterSetter() {
    UserResponse response = new UserResponse();
    response.setEnabled(true);

    assertTrue(response.isEnabled());

    response.setEnabled(false);

    assertFalse(response.isEnabled());
  }

  @Test
  @DisplayName("UserResponse - Should handle all properties with mixed disabled state")
  void testUserResponseDisabledUser() {
    UserResponse response = new UserResponse();
    response.setUserId(3L);
    response.setEmail("disabled@example.com");
    response.setName("Disabled User");
    response.setRoles(Set.of("USER"));
    response.setEnabled(false);

    assertEquals(3L, response.getUserId());
    assertFalse(response.isEnabled());
  }

  // ==================== ErrorResponse Tests ====================

  @Test
  @DisplayName("ErrorResponse - Should create with all-arg constructor")
  void testErrorResponseAllArgConstructor() {
    LocalDateTime timestamp = LocalDateTime.now();
    ErrorResponse response = new ErrorResponse(
        timestamp,
        400,
        "Bad Request",
        "Invalid input",
        "/api/users",
        "Email is required"
    );

    assertEquals(timestamp, response.getTimestamp());
    assertEquals(400, response.getStatus());
    assertEquals("Bad Request", response.getError());
    assertEquals("Invalid input", response.getMessage());
    assertEquals("/api/users", response.getPath());
    assertEquals("Email is required", response.getDetails());
  }

  @Test
  @DisplayName("ErrorResponse - Should handle no-arg constructor")
  void testErrorResponseNoArgConstructor() {
    ErrorResponse response = new ErrorResponse();
    assertNotNull(response);
    assertEquals(0, response.getStatus());
  }

  @Test
  @DisplayName("ErrorResponse - Should handle getters and setters for timestamp")
  void testErrorResponseTimestampGetterSetter() {
    ErrorResponse response = new ErrorResponse();
    LocalDateTime now = LocalDateTime.now();
    response.setTimestamp(now);

    assertEquals(now, response.getTimestamp());
  }

  @Test
  @DisplayName("ErrorResponse - Should handle getters and setters for status")
  void testErrorResponseStatusGetterSetter() {
    ErrorResponse response = new ErrorResponse();
    response.setStatus(404);

    assertEquals(404, response.getStatus());
  }

  @Test
  @DisplayName("ErrorResponse - Should handle getters and setters for error")
  void testErrorResponseErrorGetterSetter() {
    ErrorResponse response = new ErrorResponse();
    response.setError("Not Found");

    assertEquals("Not Found", response.getError());
  }

  @Test
  @DisplayName("ErrorResponse - Should handle getters and setters for message")
  void testErrorResponseMessageGetterSetter() {
    ErrorResponse response = new ErrorResponse();
    response.setMessage("Resource not found");

    assertEquals("Resource not found", response.getMessage());
  }

  @Test
  @DisplayName("ErrorResponse - Should handle getters and setters for path")
  void testErrorResponsePathGetterSetter() {
    ErrorResponse response = new ErrorResponse();
    response.setPath("/api/users/999");

    assertEquals("/api/users/999", response.getPath());
  }

  @Test
  @DisplayName("ErrorResponse - Should handle getters and setters for details")
  void testErrorResponseDetailsGetterSetter() {
    ErrorResponse response = new ErrorResponse();
    response.setDetails("No user found with id 999");

    assertEquals("No user found with id 999", response.getDetails());
  }

  @Test
  @DisplayName("ErrorResponse - Should handle null details (JsonInclude.NON_NULL)")
  void testErrorResponseNullDetails() {
    ErrorResponse response = new ErrorResponse();
    response.setTimestamp(LocalDateTime.now());
    response.setStatus(500);
    response.setError("Internal Server Error");
    response.setMessage("An error occurred");
    response.setPath("/api/users");
    response.setDetails(null);

    assertNull(response.getDetails());
  }

  @Test
  @DisplayName("ErrorResponse - Should handle multiple updates")
  void testErrorResponseMultipleUpdates() {
    ErrorResponse response = new ErrorResponse();
    LocalDateTime now = LocalDateTime.now();

    response.setTimestamp(now);
    response.setStatus(400);
    response.setError("Bad Request");
    response.setMessage("Validation failed");
    response.setPath("/api/users");
    response.setDetails("Name is required");

    assertEquals(now, response.getTimestamp());
    assertEquals(400, response.getStatus());
    assertEquals("Bad Request", response.getError());

    // Update
    response.setStatus(422);
    response.setError("Unprocessable Entity");
    response.setMessage("Field validation failed");
    response.setDetails("Email format is invalid");

    assertEquals(422, response.getStatus());
    assertEquals("Unprocessable Entity", response.getError());
    assertEquals("Field validation failed", response.getMessage());
    assertEquals("Email format is invalid", response.getDetails());
  }

  @Test
  @DisplayName("ErrorResponse - Should handle 500 Internal Server Error")
  void testErrorResponse500Error() {
    ErrorResponse response = new ErrorResponse();
    response.setStatus(500);
    response.setError("Internal Server Error");
    response.setMessage("An unexpected error occurred");
    response.setPath("/api/fitness-centres");

    assertEquals(500, response.getStatus());
    assertEquals("Internal Server Error", response.getError());
  }

  @Test
  @DisplayName("ErrorResponse - Should handle 401 Unauthorized")
  void testErrorResponse401Error() {
    ErrorResponse response = new ErrorResponse();
    response.setStatus(401);
    response.setError("Unauthorized");
    response.setMessage("Invalid credentials");

    assertEquals(401, response.getStatus());
    assertEquals("Unauthorized", response.getError());
  }

  @Test
  @DisplayName("ErrorResponse - Should handle 403 Forbidden")
  void testErrorResponse403Error() {
    ErrorResponse response = new ErrorResponse();
    response.setStatus(403);
    response.setError("Forbidden");
    response.setMessage("Access denied");

    assertEquals(403, response.getStatus());
    assertEquals("Forbidden", response.getError());
  }
}
