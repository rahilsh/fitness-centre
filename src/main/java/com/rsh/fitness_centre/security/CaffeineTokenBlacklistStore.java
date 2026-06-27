package com.rsh.fitness_centre.security;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * In-memory token blacklist store using Caffeine cache.
 * Used for local development and testing.
 */
@Component
@ConditionalOnProperty(
    name = "token.blacklist.store",
    havingValue = "memory",
    matchIfMissing = true
)
public class CaffeineTokenBlacklistStore implements TokenBlacklistStore {

  private static final Logger logger = LoggerFactory.getLogger(CaffeineTokenBlacklistStore.class);

  private final Cache<String, String> cache;

  public CaffeineTokenBlacklistStore() {
    // Create cache with automatic expiration
    this.cache = Caffeine.newBuilder()
        .expireAfterWrite(Duration.ofDays(7)) // Max TTL of 7 days
        .recordStats()
        .build();
    logger.info("CaffeineTokenBlacklistStore initialized for in-memory token blacklist storage");
  }

  @Override
  public void set(String key, String value, long ttlSeconds) {
    // Caffeine automatically expires based on expireAfterWrite, but we store with explicit TTL
    cache.put(key, value);
    logger.debug("Token cached in Caffeine: {}, TTL: {} seconds", key, ttlSeconds);
  }

  @Override
  public String get(String key) {
    return cache.getIfPresent(key);
  }

  @Override
  public void delete(String key) {
    cache.invalidate(key);
    logger.debug("Token removed from Caffeine cache: {}", key);
  }

  @Override
  public void deleteAllByPrefix(String prefix) {
    cache.asMap().keySet().stream()
        .filter(key -> key.startsWith(prefix))
        .forEach(cache::invalidate);
    logger.info("Cleared {} tokens matching prefix: {}", 
        cache.asMap().keySet().stream().filter(key -> key.startsWith(prefix)).count(), prefix);
  }
}
