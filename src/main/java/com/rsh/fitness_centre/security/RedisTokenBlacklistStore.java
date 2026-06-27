package com.rsh.fitness_centre.security;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Redis-based token blacklist store.
 * Used for production and environments where Redis is available.
 */
@Component
@ConditionalOnProperty(
    name = "token.blacklist.store",
    havingValue = "redis"
)
public class RedisTokenBlacklistStore implements TokenBlacklistStore {

  private static final Logger logger = LoggerFactory.getLogger(RedisTokenBlacklistStore.class);

  private final RedisTemplate<String, String> redisTemplate;

  public RedisTokenBlacklistStore(RedisTemplate<String, String> redisTemplate) {
    this.redisTemplate = redisTemplate;
    logger.info("RedisTokenBlacklistStore initialized for Redis-based token blacklist storage");
  }

  @Override
  public void set(String key, String value, long ttlSeconds) {
    try {
      redisTemplate.opsForValue().set(key, value, ttlSeconds, TimeUnit.SECONDS);
      logger.debug("Token blacklisted in Redis: {}, TTL: {} seconds", key, ttlSeconds);
    } catch (Exception e) {
      logger.error("Error setting token in Redis: {}", key, e);
      throw new RuntimeException("Failed to blacklist token in Redis", e);
    }
  }

  @Override
  public String get(String key) {
    try {
      return redisTemplate.opsForValue().get(key);
    } catch (Exception e) {
      logger.error("Error retrieving token from Redis: {}", key, e);
      return null;
    }
  }

  @Override
  public void delete(String key) {
    try {
      redisTemplate.delete(key);
      logger.debug("Token removed from Redis: {}", key);
    } catch (Exception e) {
      logger.error("Error deleting token from Redis: {}", key, e);
    }
  }

  @Override
  public void deleteAllByPrefix(String prefix) {
    try {
      Set<String> keys = redisTemplate.keys(prefix + "*");
      if (keys != null && !keys.isEmpty()) {
        redisTemplate.delete(keys);
        logger.info("Cleared {} tokens matching prefix: {}", keys.size(), prefix);
      }
    } catch (Exception e) {
      logger.error("Error clearing tokens by prefix in Redis: {}", prefix, e);
    }
  }
}
