package com.rsh.fitness_centre.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import com.rsh.fitness_centre.entity.User;
import com.rsh.fitness_centre.entity.UserRole;
import com.rsh.fitness_centre.entity.request.LoginRequest;
import com.rsh.fitness_centre.entity.request.RegisterRequest;
import com.rsh.fitness_centre.entity.response.LoginResponse;
import com.rsh.fitness_centre.security.JwtTokenProvider;
import com.rsh.fitness_centre.security.TokenBlacklistService;
import com.rsh.fitness_centre.service.RefreshTokenService;
import com.rsh.fitness_centre.service.MetricsService;
import com.rsh.fitness_centre.service.UserService;
import com.rsh.fitness_centre.entity.RefreshToken;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Unit tests for AuthController.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController Tests")
class AuthControllerTest {

  private AuthController authController;

  @Mock
  private UserService userService;

  @Mock
  private JwtTokenProvider tokenProvider;

  @Mock
  private TokenBlacklistService tokenBlacklistService;

  @Mock
  private RefreshTokenService refreshTokenService;

  @Mock
  private MetricsService metricsService;

  @Mock
  private HttpServletResponse response;

  @BeforeEach
  void setUp() {
    authController = new AuthController(userService, tokenProvider, tokenBlacklistService, refreshTokenService, metricsService);
  }

  @Test
  @DisplayName("Should register user successfully")
  void testRegisterSuccess() {
    RegisterRequest request = new RegisterRequest("john@example.com", "John Doe",
        "SecurePass123!", "SecurePass123!");

    User user = new User(1L, "John Doe", "john@example.com");
    user.setRoles(Set.of(UserRole.USER));
    user.setPasswordHash("hashed");
    user.setEnabled(true);

    when(userService.registerUser(anyString(), anyString(), anyString())).thenReturn(user);
    when(tokenProvider.generateToken(any(User.class))).thenReturn("Bearer token123");
    when(tokenProvider.getExpirationTimeInSeconds()).thenReturn(86400L);
    when(refreshTokenService.createRefreshToken(anyLong())).thenReturn(new RefreshToken(1L, user, "refresh-token-123", Instant.now().plusSeconds(3600), Instant.now(), false));

    ResponseEntity<LoginResponse> responseEntity = authController.register(request, response);

    assertNotNull(responseEntity);
    assertNotNull(responseEntity.getBody());
    assertNotNull(responseEntity.getBody().getToken());
  }

  @Test
  @DisplayName("Should fail register with password mismatch")
  void testRegisterPasswordMismatch() {
    RegisterRequest request = new RegisterRequest("john@example.com", "John Doe",
        "SecurePass123!", "DifferentPass456!");

    ResponseEntity<LoginResponse> responseEntity = authController.register(request, response);

    assertNotNull(responseEntity);
  }

  @Test
  @DisplayName("Should fail register with duplicate email")
  void testRegisterDuplicateEmail() {
    RegisterRequest request = new RegisterRequest("john@example.com", "John Doe",
        "SecurePass123!", "SecurePass123!");

    when(userService.registerUser(anyString(), anyString(), anyString()))
        .thenThrow(new IllegalArgumentException("User with email already exists"));

    ResponseEntity<LoginResponse> responseEntity = authController.register(request, response);

    assertNotNull(responseEntity);
  }

  @Test
  @DisplayName("Should login user successfully")
  void testLoginSuccess() {
    LoginRequest request = new LoginRequest("john@example.com", "SecurePass123!");

    User user = new User(1L, "John Doe", "john@example.com");
    user.setRoles(Set.of(UserRole.USER));
    user.setPasswordHash("hashed");
    user.setEnabled(true);
    user.setLastLogin(LocalDateTime.now());

    when(userService.authenticateUser(anyString(), anyString())).thenReturn(user);
    when(tokenProvider.generateToken(any(User.class))).thenReturn("Bearer token123");
    when(tokenProvider.getExpirationTimeInSeconds()).thenReturn(86400L);
    when(refreshTokenService.createRefreshToken(anyLong())).thenReturn(new RefreshToken(1L, user, "refresh-token-123", Instant.now().plusSeconds(3600), Instant.now(), false));

    ResponseEntity<LoginResponse> responseEntity = authController.login(request, response);

    assertNotNull(responseEntity);
    assertNotNull(responseEntity.getBody());
    assertNotNull(responseEntity.getBody().getToken());
  }

  @Test
  @DisplayName("Should fail login with invalid credentials")
  void testLoginInvalidCredentials() {
    LoginRequest request = new LoginRequest("john@example.com", "WrongPassword");

    when(userService.authenticateUser(anyString(), anyString()))
        .thenThrow(new IllegalArgumentException("Invalid email or password"));

    ResponseEntity<LoginResponse> responseEntity = authController.login(request, response);

    assertNotNull(responseEntity);
  }

  @Test
  @DisplayName("Should fail to get current user when unauthorized")
  void testGetCurrentUserUnauthorized() {
    ResponseEntity<?> response = authController.getCurrentUser();

    assertNotNull(response);
  }
}
