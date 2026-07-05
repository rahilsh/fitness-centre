package com.rsh.fitness_centre.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity representing a secure Refresh Token stored in the database.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Schema(description = "Refresh token for generating new access tokens")
public class RefreshToken {

  private Long id;

  private User user;

  private String token;

  private Instant expiryDate;

  private Instant createdAt;

  @lombok.Builder.Default
  private boolean revoked = false;

  /**
   * Check if token is expired.
   *
   * @return true if expired, false otherwise
   */
  public boolean isExpired() {
    return expiryDate.isBefore(Instant.now());
  }
}
