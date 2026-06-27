package com.rsh.fitness_centre.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for JwtAuthenticationFilter.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("JwtAuthenticationFilter Tests")
class JwtAuthenticationFilterTest {

  private JwtAuthenticationFilter filter;

  @Mock
  private JwtTokenProvider tokenProvider;

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @Mock
  private FilterChain filterChain;

  @BeforeEach
  void setUp() {
    filter = new JwtAuthenticationFilter(tokenProvider);
    SecurityContextHolder.clearContext();
  }

  @Test
  @DisplayName("Should extract valid JWT token from Authorization header")
  void testExtractValidToken() throws ServletException, IOException {
    String token = "validJwtToken123";
    String bearerToken = "Bearer " + token;

    when(request.getHeader("Authorization")).thenReturn(bearerToken);
    when(tokenProvider.validateToken(token)).thenReturn(true);
    when(tokenProvider.extractUserId(token)).thenReturn(1L);

    filter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    verify(tokenProvider).validateToken(token);
    verify(tokenProvider).extractUserId(token);
  }

  @Test
  @DisplayName("Should set authentication in security context for valid token")
  void testSetAuthenticationInContext() throws ServletException, IOException {
    String token = "validJwtToken123";
    String bearerToken = "Bearer " + token;
    Long userId = 1L;

    when(request.getHeader("Authorization")).thenReturn(bearerToken);
    when(tokenProvider.validateToken(token)).thenReturn(true);
    when(tokenProvider.extractUserId(token)).thenReturn(userId);

    filter.doFilterInternal(request, response, filterChain);

    assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    assertEquals(userId, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
  }

  @Test
  @DisplayName("Should skip filter when Authorization header is missing")
  void testSkipFilterWhenMissingAuthorizationHeader() throws ServletException, IOException {
    when(request.getHeader("Authorization")).thenReturn(null);

    filter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    verify(tokenProvider, never()).validateToken(any());
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  @DisplayName("Should skip filter when Authorization header is empty")
  void testSkipFilterWhenEmptyAuthorizationHeader() throws ServletException, IOException {
    when(request.getHeader("Authorization")).thenReturn("");

    filter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    verify(tokenProvider, never()).validateToken(any());
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  @DisplayName("Should skip filter when Authorization header is whitespace")
  void testSkipFilterWhenWhitespaceAuthorizationHeader() throws ServletException, IOException {
    when(request.getHeader("Authorization")).thenReturn("   ");

    filter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    verify(tokenProvider, never()).validateToken(any());
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  @DisplayName("Should handle invalid token format (no Bearer prefix)")
  void testInvalidTokenFormatNoBearerPrefix() throws ServletException, IOException {
    when(request.getHeader("Authorization")).thenReturn("InvalidToken123");

    filter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    verify(tokenProvider, never()).validateToken(any());
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  @DisplayName("Should handle invalid token format (Bearer without token)")
  void testInvalidTokenFormatBearerWithoutToken() throws ServletException, IOException {
    when(request.getHeader("Authorization")).thenReturn("Bearer ");

    filter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    // "Bearer " returns empty string after substring(7), which is not text
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  @DisplayName("Should not set authentication when token validation fails")
  void testTokenValidationFails() throws ServletException, IOException {
    String token = "invalidJwtToken123";
    String bearerToken = "Bearer " + token;

    when(request.getHeader("Authorization")).thenReturn(bearerToken);
    when(tokenProvider.validateToken(token)).thenReturn(false);

    filter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  @DisplayName("Should not set authentication when userId extraction returns null")
  void testUserIdExtractionReturnsNull() throws ServletException, IOException {
    String token = "validJwtToken123";
    String bearerToken = "Bearer " + token;

    when(request.getHeader("Authorization")).thenReturn(bearerToken);
    when(tokenProvider.validateToken(token)).thenReturn(true);
    when(tokenProvider.extractUserId(token)).thenReturn(null);

    filter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  @DisplayName("Should handle expired token gracefully")
  void testExpiredToken() throws ServletException, IOException {
    String token = "expiredJwtToken123";
    String bearerToken = "Bearer " + token;

    when(request.getHeader("Authorization")).thenReturn(bearerToken);
    when(tokenProvider.validateToken(token)).thenReturn(false);

    filter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  @DisplayName("Should continue filter chain even if exception occurs during token validation")
  void testExceptionDuringTokenValidation() throws ServletException, IOException {
    String token = "testToken";
    String bearerToken = "Bearer " + token;

    when(request.getHeader("Authorization")).thenReturn(bearerToken);
    when(tokenProvider.validateToken(token)).thenThrow(new RuntimeException("Token validation error"));

    // Should not throw exception - should continue filter chain
    filter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  @DisplayName("Should continue filter chain even if exception occurs during userId extraction")
  void testExceptionDuringUserIdExtraction() throws ServletException, IOException {
    String token = "testToken";
    String bearerToken = "Bearer " + token;

    when(request.getHeader("Authorization")).thenReturn(bearerToken);
    when(tokenProvider.validateToken(token)).thenReturn(true);
    when(tokenProvider.extractUserId(token)).thenThrow(new RuntimeException("User extraction error"));

    // Should not throw exception - should continue filter chain
    filter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  @DisplayName("Should extract token correctly with Bearer prefix (case-sensitive)")
  void testBearerPrefixExtraction() throws ServletException, IOException {
    String token = "myJwtToken123";
    String bearerToken = "Bearer " + token;

    when(request.getHeader("Authorization")).thenReturn(bearerToken);
    when(tokenProvider.validateToken(token)).thenReturn(true);
    when(tokenProvider.extractUserId(token)).thenReturn(2L);

    filter.doFilterInternal(request, response, filterChain);

    verify(tokenProvider).validateToken(token);
    verify(tokenProvider).extractUserId(token);
  }

  @Test
  @DisplayName("Should not extract token with lowercase bearer prefix")
  void testLowercaseBearerPrefix() throws ServletException, IOException {
    String bearerToken = "bearer " + "token123"; // lowercase bearer

    when(request.getHeader("Authorization")).thenReturn(bearerToken);

    filter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    verify(tokenProvider, never()).validateToken(any());
  }

  @Test
  @DisplayName("Should handle multiple sequential requests with different tokens")
  void testMultipleSequentialRequests() throws ServletException, IOException {
    // First request
    String token1 = "token1";
    when(request.getHeader("Authorization")).thenReturn("Bearer " + token1);
    when(tokenProvider.validateToken(token1)).thenReturn(true);
    when(tokenProvider.extractUserId(token1)).thenReturn(1L);

    filter.doFilterInternal(request, response, filterChain);
    assertNotNull(SecurityContextHolder.getContext().getAuthentication());

    // Clear context for second request
    SecurityContextHolder.clearContext();

    // Second request with different token
    String token2 = "token2";
    when(request.getHeader("Authorization")).thenReturn("Bearer " + token2);
    when(tokenProvider.validateToken(token2)).thenReturn(true);
    when(tokenProvider.extractUserId(token2)).thenReturn(2L);

    filter.doFilterInternal(request, response, filterChain);
    assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    assertEquals(2L, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
  }

  @Test
  @DisplayName("Should set WebAuthenticationDetails in authentication token")
  void testWebAuthenticationDetailsSet() throws ServletException, IOException {
    String token = "validJwtToken123";
    String bearerToken = "Bearer " + token;

    when(request.getHeader("Authorization")).thenReturn(bearerToken);
    when(tokenProvider.validateToken(token)).thenReturn(true);
    when(tokenProvider.extractUserId(token)).thenReturn(1L);

    filter.doFilterInternal(request, response, filterChain);

    assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    assertNotNull(SecurityContextHolder.getContext().getAuthentication().getDetails());
  }

  @Test
  @DisplayName("Should handle token with special characters")
  void testTokenWithSpecialCharacters() throws ServletException, IOException {
    String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
    String bearerToken = "Bearer " + token;

    when(request.getHeader("Authorization")).thenReturn(bearerToken);
    when(tokenProvider.validateToken(token)).thenReturn(true);
    when(tokenProvider.extractUserId(token)).thenReturn(1L);

    filter.doFilterInternal(request, response, filterChain);

    verify(tokenProvider).validateToken(token);
    verify(tokenProvider).extractUserId(token);
  }

  @Test
  @DisplayName("Should continue filter chain even with valid token and null userId")
  void testContinueChainWithNullUserId() throws ServletException, IOException {
    String token = "validToken";

    when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
    when(tokenProvider.validateToken(token)).thenReturn(true);
    when(tokenProvider.extractUserId(token)).thenReturn(null);

    filter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
  }
}
