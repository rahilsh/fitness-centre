package com.rsh.fitness_centre.service;

import com.rsh.fitness_centre.entity.RefreshToken;
import com.rsh.fitness_centre.entity.User;
import com.rsh.fitness_centre.repository.RefreshTokenRepository;
import com.rsh.fitness_centre.repository.UserRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service to manage Refresh Token lifecycle.
 */
@Service
public class RefreshTokenService {

  private static final Logger logger = LoggerFactory.getLogger(RefreshTokenService.class);

  @Value("${jwt.refreshExpirationDays:7}")
  private long refreshExpirationDays;

  private final RefreshTokenRepository refreshTokenRepository;
  private final UserRepository userRepository;

  @Autowired
  public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
    this.refreshTokenRepository = refreshTokenRepository;
    this.userRepository = userRepository;
  }

  /**
   * Create or update a refresh token for a given user ID.
   *
   * @param userId the user ID
   * @return the created RefreshToken
   */
  @Transactional
  public RefreshToken createRefreshToken(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

    // Clear existing tokens for this user first
    refreshTokenRepository.deleteByUser(user);

    RefreshToken refreshToken = RefreshToken.builder()
        .user(user)
        .token(UUID.randomUUID().toString())
        .expiryDate(Instant.now().plus(refreshExpirationDays, ChronoUnit.DAYS))
        .revoked(false)
        .build();

    RefreshToken savedToken = refreshTokenRepository.save(refreshToken);
    logger.info("Refresh token created successfully for user ID: {}", userId);
    return savedToken;
  }

  /**
   * Find a refresh token by its string value.
   *
   * @param token the token string
   * @return Optional RefreshToken
   */
  public Optional<RefreshToken> findByToken(String token) {
    return refreshTokenRepository.findByToken(token);
  }

  /**
   * Verify and validate the refresh token.
   * If expired or revoked, deletes it and throws exception.
   *
   * @param token the RefreshToken entity
   * @return the verified RefreshToken
   */
  @Transactional
  public RefreshToken verifyExpiration(RefreshToken token) {
    if (token.isExpired()) {
      refreshTokenRepository.delete(token);
      logger.warn("Refresh token was expired and has been deleted: {}", token.getToken());
      throw new IllegalArgumentException("Refresh token was expired. Please make a new signin request");
    }

    if (token.isRevoked()) {
      refreshTokenRepository.delete(token);
      logger.warn("Refresh token was revoked and has been deleted: {}", token.getToken());
      throw new IllegalArgumentException("Refresh token was revoked. Please make a new signin request");
    }

    return token;
  }

  /**
   * Revoke refresh tokens for a user.
   *
   * @param userId the user ID
   */
  @Transactional
  public void revokeByUserId(Long userId) {
    User user = userRepository.findById(userId).orElse(null);
    if (user != null) {
      refreshTokenRepository.deleteByUser(user);
      logger.info("Revoked all refresh tokens for user ID: {}", userId);
    }
  }

  /**
   * Clean up expired refresh tokens (scheduled/manual).
   */
  @Transactional
  public void deleteExpiredTokens() {
    refreshTokenRepository.deleteByExpiryDateBefore(Instant.now());
    logger.info("Deleted expired refresh tokens");
  }
}
