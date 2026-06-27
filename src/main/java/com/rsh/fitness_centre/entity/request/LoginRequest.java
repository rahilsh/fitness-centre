package com.rsh.fitness_centre.entity.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for user login.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User login request")
public class LoginRequest {

  @NotBlank(message = "Email is required")
  @Email(message = "Email should be valid")
  @Schema(description = "User email address", example = "john.doe@example.com")
  private String email;

  @NotBlank(message = "Password is required")
  @Schema(description = "User password", example = "SecurePassword123!")
  private String password;
}
