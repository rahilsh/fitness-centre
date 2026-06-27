package com.rsh.fitness_centre.repository;

import com.rsh.fitness_centre.entity.RefreshToken;
import com.rsh.fitness_centre.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

  Optional<RefreshToken> findByToken(String token);

  void deleteByUser(User user);

  @Modifying
  void deleteByExpiryDateBefore(java.time.Instant now);
}
