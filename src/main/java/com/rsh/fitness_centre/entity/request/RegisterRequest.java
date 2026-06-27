package com.rsh.fitness_centre.entity.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for user registration.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User registration request")
public class RegisterRequest {

  @NotBlank(message = "Email is required")
  @Email(message = "Email should be valid")
  @Schema(description = "User email address", example = "john.doe@example.com")
  private String email;

  @NotBlank(message = "Name is required")
  @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
  @Schema(description = "User full name", example = "John Doe")
  private String name;

  @NotBlank(message = "Password is required")
  @Size(min = 8, message = "Password must be at least 8 characters")
  @Schema(description = "User password", example = "SecurePassword123!")
  private String password;

  @NotBlank(message = "Password confirmation is required")
  @Schema(description = "Password confirmation", example = "SecurePassword123!")
  private String passwordConfirmation;
}
