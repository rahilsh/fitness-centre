package com.rsh.fitness_centre.entity;

import com.google.common.base.Objects;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Schema(description = "Registered user in the system")
public class User {

  @Schema(description = "Unique user identifier", example = "1")
  private Long id;
  
  @Schema(description = "User's full name", example = "John Doe")
  private String name;

  @Schema(description = "User's email address", example = "john.doe@example.com")
  private String email;

  @Schema(description = "BCrypt hashed password")
  private String passwordHash;

  @Schema(description = "User roles for RBAC")
  private Set<UserRole> roles = new HashSet<>();

  @Schema(description = "Whether the user account is enabled")
  private boolean enabled = true;

  @Schema(description = "Timestamp when the user was created")
  private LocalDateTime createdAt;

  @Schema(description = "Timestamp when the user was last modified")
  private LocalDateTime updatedAt;

  @Schema(description = "Timestamp of the last login")
  private LocalDateTime lastLogin;

  @com.fasterxml.jackson.annotation.JsonIgnore
  private Set<Booking> bookings = new HashSet<>();
  
  public User(Long id, String name) {
    this.id = id;
    this.name = name;
  }

  public User(Long id, String name, String email) {
    this.id = id;
    this.name = name;
    this.email = email;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    User user = (User) o;
    return Objects.equal(name, user.name);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(name);
  }
}
