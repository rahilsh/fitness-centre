package com.rsh.fitness_centre.entity.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for user information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "User information response")
public class UserResponse {

  @Schema(description = "User ID", example = "1")
  private Long userId;

  @Schema(description = "User email address", example = "john.doe@example.com")
  private String email;

  @Schema(description = "User full name", example = "John Doe")
  private String name;

  @Schema(description = "User roles")
  private Set<String> roles;

  @Schema(description = "Last login timestamp")
  private LocalDateTime lastLogin;

  @Schema(description = "Account creation timestamp")
  private LocalDateTime createdAt;

  @Schema(description = "Whether the user account is enabled")
  private boolean enabled;
}
