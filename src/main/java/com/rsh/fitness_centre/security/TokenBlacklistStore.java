package com.rsh.fitness_centre.security;

/**
 * Abstraction interface for token blacklist storage.
 * Implementations can use in-memory cache (Caffeine) or Redis.
 */
public interface TokenBlacklistStore {

  /**
   * Set a key-value pair with TTL.
   *
   * @param key the key
   * @param value the value
   * @param ttlSeconds the time to live in seconds
   */
  void set(String key, String value, long ttlSeconds);

  /**
   * Get value for a key.
   *
   * @param key the key
   * @return the value or null if not found
   */
  String get(String key);

  /**
   * Delete a key.
   *
   * @param key the key to delete
   */
  void delete(String key);

  /**
   * Delete all keys matching a prefix.
   *
   * @param prefix the key prefix
   */
  void deleteAllByPrefix(String prefix);
}
