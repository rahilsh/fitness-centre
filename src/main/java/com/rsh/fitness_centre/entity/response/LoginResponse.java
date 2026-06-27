package com.rsh.fitness_centre.entity.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for login endpoint.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Login response with JWT token")
public class LoginResponse {

  @Schema(description = "JWT token with Bearer prefix", example = "Bearer eyJhbGciOiJIUzI1NiJ9...")
  private String token;

  @Schema(description = "User ID", example = "1")
  private Long userId;

  @Schema(description = "User email address", example = "john.doe@example.com")
  private String email;

  @Schema(description = "User roles")
  private Set<String> roles;

  @Schema(description = "Token expiration time in seconds", example = "86400")
  private long expiresIn;
}
