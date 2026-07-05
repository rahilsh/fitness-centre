package com.rsh.fitness_centre.repository;

import com.rsh.fitness_centre.entity.RefreshToken;
import com.rsh.fitness_centre.entity.User;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for RefreshToken entity.
 * Implementation uses JDBC instead of JPA.
 */
public interface RefreshTokenRepository {

  RefreshToken save(RefreshToken token);

  Optional<RefreshToken> findById(Long id);

  Optional<RefreshToken> findByToken(String token);

  void deleteByUser(User user);

  void deleteByExpiryDateBefore(Instant now);

  List<RefreshToken> findAll();

  void delete(RefreshToken token);

  void deleteById(Long id);

  void deleteAll();

  long count();

  boolean existsById(Long id);
}
