package com.rsh.fitness_centre.security;

import com.rsh.fitness_centre.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Utility class for JWT token generation and validation.
 */
@Component
public class JwtTokenProvider {

  private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

  @Value("${jwt.secret:your-secret-key-that-is-at-least-32-characters-long-for-hs256}")
  private String jwtSecret;

  @Value("${jwt.expiration:86400}")
  private long jwtExpirationMs;

  /**
   * Generate JWT token for the given user.
   *
   * @param user the user to generate token for
   * @return JWT token with Bearer prefix
   */
  public String generateToken(User user) {
    SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    Date now = new Date();
    Date expirationDate = new Date(now.getTime() + jwtExpirationMs);
    String tokenId = UUID.randomUUID().toString();

    String token = Jwts.builder()
        .id(tokenId) // Add JWT ID (jti) for token revocation
        .subject(user.getId().toString())
        .claim("email", user.getEmail())
        .claim("roles", user.getRoles().stream()
            .map(Enum::name)
            .collect(Collectors.toSet()))
        .issuedAt(now)
        .expiration(expirationDate)
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();

    return "Bearer " + token;
  }

  /**
   * Validate the given JWT token.
   *
   * @param token the JWT token (without Bearer prefix)
   * @return true if token is valid, false otherwise
   */
  public boolean validateToken(String token) {
    try {
      SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
      Jwts.parser()
          .verifyWith(key)
          .build()
          .parseSignedClaims(token);
      return true;
    } catch (Exception ex) {
      logger.error("Invalid JWT token: {}", ex.getMessage());
      return false;
    }
  }

  /**
   * Extract user ID from JWT token.
   *
   * @param token the JWT token (without Bearer prefix)
   * @return the user ID or null if token is invalid
   */
  public Long extractUserId(String token) {
    try {
      SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
      Claims claims = Jwts.parser()
          .verifyWith(key)
          .build()
          .parseSignedClaims(token)
          .getPayload();

      String userId = claims.getSubject();
      return Long.parseLong(userId);
    } catch (Exception ex) {
      logger.error("Failed to extract user ID from token: {}", ex.getMessage());
      return null;
    }
  }

  /**
   * Extract email from JWT token.
   *
   * @param token the JWT token (without Bearer prefix)
   * @return the email or null if token is invalid
   */
  public String extractEmail(String token) {
    try {
      SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
      Claims claims = Jwts.parser()
          .verifyWith(key)
          .build()
          .parseSignedClaims(token)
          .getPayload();

      return claims.get("email", String.class);
    } catch (Exception ex) {
      logger.error("Failed to extract email from token: {}", ex.getMessage());
      return null;
    }
  }

  /**
   * Extract roles from JWT token.
   *
   * @param token the JWT token (without Bearer prefix)
   * @return set of role names
   */
  @SuppressWarnings("unchecked")
  public java.util.Set<String> extractRoles(String token) {
    try {
      SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
      Claims claims = Jwts.parser()
          .verifyWith(key)
          .build()
          .parseSignedClaims(token)
          .getPayload();

      java.util.List<String> roles = claims.get("roles", java.util.List.class);
      return roles != null ? new java.util.HashSet<>(roles) : java.util.Collections.emptySet();
    } catch (Exception ex) {
      logger.error("Failed to extract roles from token: {}", ex.getMessage());
      return java.util.Collections.emptySet();
    }
  }

  /**
   * Extract token ID (jti claim) from JWT token.
   *
   * @param token the JWT token (without Bearer prefix)
   * @return the token ID or null if token is invalid
   */
  public String extractTokenId(String token) {
    try {
      SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
      Claims claims = Jwts.parser()
          .verifyWith(key)
          .build()
          .parseSignedClaims(token)
          .getPayload();

      return claims.getId();
    } catch (Exception ex) {
      logger.error("Failed to extract token ID from token: {}", ex.getMessage());
      return null;
    }
  }

  /**
   * Get JWT expiration time in seconds.
   *
   * @return expiration time in seconds
   */
  public long getExpirationTimeInSeconds() {
    return jwtExpirationMs / 1000;
  }
}
