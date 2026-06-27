package com.rsh.fitness_centre.entity.request;

import com.google.common.base.Objects;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "User registration request")
public class AddUserRequest {
  @NotBlank(message = "User name is required")
  @Size(min = 2, max = 100, message = "Name must be between 2-100 characters")
  @Schema(description = "User's full name", example = "John Doe")
  private String name;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AddUserRequest that = (AddUserRequest) o;
    return Objects.equal(name, that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(name);
  }
}
