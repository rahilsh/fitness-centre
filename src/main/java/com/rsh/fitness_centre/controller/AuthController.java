package com.rsh.fitness_centre.controller;

import com.rsh.fitness_centre.entity.User;
import com.rsh.fitness_centre.entity.request.LoginRequest;
import com.rsh.fitness_centre.entity.request.RegisterRequest;
import com.rsh.fitness_centre.entity.response.LoginResponse;
import com.rsh.fitness_centre.entity.response.UserResponse;
import com.rsh.fitness_centre.security.TokenBlacklistService;
import com.rsh.fitness_centre.security.JwtTokenProvider;
import com.rsh.fitness_centre.service.RefreshTokenService;
import com.rsh.fitness_centre.service.MetricsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.rsh.fitness_centre.entity.RefreshToken;
import com.rsh.fitness_centre.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Authentication controller for user registration, login, and profile endpoints.
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "User authentication and authorization endpoints")
public class AuthController {

  private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

  private final UserService userService;
  private final JwtTokenProvider tokenProvider;
  private final TokenBlacklistService tokenBlacklistService;
  private final RefreshTokenService refreshTokenService;
  private final MetricsService metricsService;

  @Autowired
  public AuthController(UserService userService, JwtTokenProvider tokenProvider, 
                        TokenBlacklistService tokenBlacklistService, RefreshTokenService refreshTokenService,
                        MetricsService metricsService) {
    this.userService = userService;
    this.tokenProvider = tokenProvider;
    this.tokenBlacklistService = tokenBlacklistService;
    this.refreshTokenService = refreshTokenService;
    this.metricsService = metricsService;
  }

  /**
   * Register a new user.
   *
   * @param registerRequest the registration request
   * @return the login response with JWT token
   */
  @PostMapping("/register")
  @Operation(summary = "Register a new user")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "User registered successfully"),
      @ApiResponse(responseCode = "400", description = "Invalid request or user already exists",
          content = @Content(schema = @Schema(implementation = String.class)))
  })
  public ResponseEntity<LoginResponse> register(@Valid @RequestBody RegisterRequest registerRequest, HttpServletResponse servletResponse) {
    logger.info("User registration request for email: {}", registerRequest.getEmail());

    if (!registerRequest.getPassword().equals(registerRequest.getPasswordConfirmation())) {
      logger.warn("Registration failed: password mismatch for email: {}", registerRequest.getEmail());
      return ResponseEntity.badRequest().build();
    }

    try {
      User user = userService.registerUser(
          registerRequest.getEmail(),
          registerRequest.getPassword(),
          registerRequest.getName()
      );

      String token = tokenProvider.generateToken(user);
      long expirationSeconds = tokenProvider.getExpirationTimeInSeconds();

      // Create refresh token
      RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());
      addRefreshTokenCookie(servletResponse, refreshToken.getToken());

      LoginResponse response = LoginResponse.builder()
          .token(token)
          .userId(user.getId())
          .email(user.getEmail())
          .roles(user.getRoles().stream()
              .map(Enum::name)
              .collect(java.util.stream.Collectors.toSet()))
          .expiresIn(expirationSeconds)
          .build();

      logger.info("User registered successfully: {}", user.getEmail());
      metricsService.incrementRegistration();
      return ResponseEntity.status(HttpStatus.CREATED).body(response);
    } catch (IllegalArgumentException ex) {
      logger.warn("Registration error: {}", ex.getMessage());
      return ResponseEntity.badRequest().build();
    }
  }

  /**
   * Login user with email and password.
   *
   * @param loginRequest the login request
   * @return the login response with JWT token
   */
  @PostMapping("/login")
  @Operation(summary = "Login user")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Login successful"),
      @ApiResponse(responseCode = "401", description = "Invalid credentials",
          content = @Content(schema = @Schema(implementation = String.class)))
  })
  public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse servletResponse) {
    logger.info("Login request for email: {}", loginRequest.getEmail());

    try {
      User user = userService.authenticateUser(loginRequest.getEmail(), loginRequest.getPassword());

      String token = tokenProvider.generateToken(user);
      long expirationSeconds = tokenProvider.getExpirationTimeInSeconds();

      // Create refresh token
      RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());
      addRefreshTokenCookie(servletResponse, refreshToken.getToken());

      LoginResponse response = LoginResponse.builder()
          .token(token)
          .userId(user.getId())
          .email(user.getEmail())
          .roles(user.getRoles().stream()
              .map(Enum::name)
              .collect(java.util.stream.Collectors.toSet()))
          .expiresIn(expirationSeconds)
          .build();

      logger.info("User logged in successfully: {}", user.getEmail());
      metricsService.incrementSuccessfulLogin();
      return ResponseEntity.ok(response);
    } catch (IllegalArgumentException ex) {
      logger.warn("Login error: {}", ex.getMessage());
      metricsService.incrementFailedLogin();
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
  }

  /**
   * Get current authenticated user information.
   *
   * @return the current user information
   */
  @GetMapping("/me")
  @Operation(summary = "Get current user information")
  @SecurityRequirement(name = "bearer-jwt")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User information retrieved"),
      @ApiResponse(responseCode = "401", description = "Unauthorized",
          content = @Content(schema = @Schema(implementation = String.class)))
  })
  public ResponseEntity<UserResponse> getCurrentUser() {
    logger.debug("Getting current user information");

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof Long)) {
      logger.warn("Attempt to get current user without authentication");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    Long userId = (Long) authentication.getPrincipal();
    User user = userService.getUserWithRoles(userId);

    if (user == null) {
      logger.warn("Current user not found with ID: {}", userId);
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    UserResponse response = UserResponse.builder()
        .userId(user.getId())
        .email(user.getEmail())
        .name(user.getName())
        .roles(user.getRoles().stream()
            .map(Enum::name)
            .collect(java.util.stream.Collectors.toSet()))
        .lastLogin(user.getLastLogin())
        .createdAt(user.getCreatedAt())
        .enabled(user.isEnabled())
        .build();

    logger.info("Current user information retrieved: {}", user.getEmail());
    return ResponseEntity.ok(response);
  }

  /**
   * Safe logout endpoint that blacklists the current JWT access token.
   *
   * @param request the HTTP servlet request
   * @param response the HTTP servlet response
   * @return success response
   */
  @PostMapping("/logout")
  @Operation(summary = "Logout user and revoke access token")
  @SecurityRequirement(name = "bearer-jwt")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Logout successful"),
      @ApiResponse(responseCode = "401", description = "Unauthorized")
  })
  public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
    String bearerToken = request.getHeader("Authorization");
    if (org.springframework.util.StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
      String jwt = bearerToken.substring(7);
      if (tokenProvider.validateToken(jwt)) {
        String tokenId = tokenProvider.extractTokenId(jwt);
        long expirationSeconds = tokenProvider.getExpirationTimeInSeconds();
        tokenBlacklistService.blacklistToken(tokenId, expirationSeconds);
        
        Long userId = tokenProvider.extractUserId(jwt);
        if (userId != null) {
          refreshTokenService.revokeByUserId(userId);
        }
        
        logger.info("User logged out successfully and token blacklisted: {}", tokenId);
      }
    }
    clearRefreshTokenCookie(response);
    SecurityContextHolder.clearContext();
    return ResponseEntity.ok().build();
  }

  /**
   * Endpoint to refresh access token using cookie-based refresh token.
   */
  @PostMapping("/refresh")
  @Operation(summary = "Refresh JWT access token")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
      @ApiResponse(responseCode = "400", description = "Invalid or expired refresh token",
          content = @Content(schema = @Schema(implementation = String.class)))
  })
  public ResponseEntity<LoginResponse> refresh(HttpServletRequest request, HttpServletResponse response) {
    String requestRefreshToken = extractRefreshTokenFromCookie(request);

    if (requestRefreshToken == null || requestRefreshToken.isEmpty()) {
      return ResponseEntity.badRequest().build();
    }

    try {
      RefreshToken token = refreshTokenService.findByToken(requestRefreshToken)
          .map(refreshTokenService::verifyExpiration)
          .orElseThrow(() -> new IllegalArgumentException("Refresh token not found in database"));

      User user = token.getUser();
      String newAccessToken = tokenProvider.generateToken(user);
      long expirationSeconds = tokenProvider.getExpirationTimeInSeconds();

      LoginResponse loginResponse = LoginResponse.builder()
          .token(newAccessToken)
          .userId(user.getId())
          .email(user.getEmail())
          .roles(user.getRoles().stream()
              .map(Enum::name)
              .collect(java.util.stream.Collectors.toSet()))
          .expiresIn(expirationSeconds)
          .build();

      return ResponseEntity.ok(loginResponse);
    } catch (IllegalArgumentException ex) {
      logger.warn("Token refresh failed: {}", ex.getMessage());
      clearRefreshTokenCookie(response);
      return ResponseEntity.badRequest().build();
    }
  }

  private void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
    org.springframework.http.ResponseCookie responseCookie = org.springframework.http.ResponseCookie.from("refreshToken", refreshToken)
        .httpOnly(true)
        .secure(true)
        .path("/auth")
        .maxAge(7 * 24 * 60 * 60)
        .sameSite("Strict")
        .build();
    response.addHeader(org.springframework.http.HttpHeaders.SET_COOKIE, responseCookie.toString());
  }

  private void clearRefreshTokenCookie(HttpServletResponse response) {
    org.springframework.http.ResponseCookie responseCookie = org.springframework.http.ResponseCookie.from("refreshToken", "")
        .httpOnly(true)
        .secure(true)
        .path("/auth")
        .maxAge(0)
        .sameSite("Strict")
        .build();
    response.addHeader(org.springframework.http.HttpHeaders.SET_COOKIE, responseCookie.toString());
  }

  private String extractRefreshTokenFromCookie(HttpServletRequest request) {
    if (request.getCookies() != null) {
      for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
        if ("refreshToken".equals(cookie.getName())) {
          return cookie.getValue();
        }
      }
    }
    return null;
  }
}
