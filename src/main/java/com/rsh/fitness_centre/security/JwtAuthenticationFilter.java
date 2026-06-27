package com.rsh.fitness_centre.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * JWT Authentication filter that processes JWT tokens from request headers.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

  private final JwtTokenProvider tokenProvider;

  public JwtAuthenticationFilter(JwtTokenProvider tokenProvider) {
    this.tokenProvider = tokenProvider;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain
  ) throws ServletException, IOException {
    try {
      String jwt = extractTokenFromRequest(request);

      if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
        Long userId = tokenProvider.extractUserId(jwt);

        if (userId != null) {
          // Extract roles from token and map to GrantedAuthorities
          java.util.Set<String> roles = tokenProvider.extractRoles(jwt);
          java.util.List<org.springframework.security.core.authority.SimpleGrantedAuthority> authorities = roles.stream()
              .map(role -> new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + role))
              .collect(Collectors.toList());

          // Create authentication token with mapped authorities
          UsernamePasswordAuthenticationToken authentication =
              new UsernamePasswordAuthenticationToken(userId, null, authorities);
          authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

          SecurityContextHolder.getContext().setAuthentication(authentication);
          logger.debug("JWT authentication set for user ID: {} with roles: {}", userId, roles);
        }
      }
    } catch (Exception ex) {
      logger.error("Failed to set user authentication in security context", ex);
    }

    filterChain.doFilter(request, response);
  }

  /**
   * Extract JWT token from Authorization header.
   *
   * @param request the HTTP request
   * @return the JWT token (without Bearer prefix) or null
   */
  private String extractTokenFromRequest(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return null;
  }
}
