package com.rsh.fitness_centre.security;

import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service to manage token blacklist using abstraction layer for caching.
 * Supports both in-memory and Redis backends based on environment.
 */
@Service
public class TokenBlacklistService {

  private static final Logger logger = LoggerFactory.getLogger(TokenBlacklistService.class);
  private static final String BLACKLIST_KEY_PREFIX = "token:blacklist:";

  private final TokenBlacklistStore tokenBlacklistStore;

  @Autowired
  public TokenBlacklistService(TokenBlacklistStore tokenBlacklistStore) {
    this.tokenBlacklistStore = tokenBlacklistStore;
  }

  /**
   * Add a token to the blacklist.
   *
   * @param tokenId the unique token identifier (jti claim)
   * @param expirationSeconds the remaining lifetime of the token in seconds
   */
  public void blacklistToken(String tokenId, long expirationSeconds) {
    if (tokenId == null || tokenId.isEmpty()) {
      logger.warn("Attempted to blacklist token with empty or null tokenId");
      return;
    }

    String key = BLACKLIST_KEY_PREFIX + tokenId;
    tokenBlacklistStore.set(key, "true", expirationSeconds);
    logger.info("Token blacklisted with ID: {}, TTL: {} seconds", tokenId, expirationSeconds);
  }

  /**
   * Check if a token is blacklisted.
   *
   * @param tokenId the unique token identifier (jti claim)
   * @return true if token is blacklisted, false otherwise
   */
  public boolean isBlacklisted(String tokenId) {
    if (tokenId == null || tokenId.isEmpty()) {
      return false;
    }

    String key = BLACKLIST_KEY_PREFIX + tokenId;
    boolean blacklisted = tokenBlacklistStore.get(key) != null;
    
    if (blacklisted) {
      logger.debug("Token found in blacklist: {}", tokenId);
    }
    
    return blacklisted;
  }

  /**
   * Remove a token from the blacklist (for testing purposes).
   *
   * @param tokenId the unique token identifier
   */
  public void removeFromBlacklist(String tokenId) {
    if (tokenId == null || tokenId.isEmpty()) {
      return;
    }

    String key = BLACKLIST_KEY_PREFIX + tokenId;
    tokenBlacklistStore.delete(key);
    logger.debug("Token removed from blacklist: {}", tokenId);
  }

  /**
   * Clear all blacklisted tokens (for testing purposes).
   */
  public void clearBlacklist() {
    tokenBlacklistStore.deleteAllByPrefix(BLACKLIST_KEY_PREFIX);
    logger.info("All blacklisted tokens cleared");
  }
}
