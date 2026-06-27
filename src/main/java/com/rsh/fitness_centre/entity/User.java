package com.rsh.fitness_centre.entity;

import com.google.common.base.Objects;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity(name = "app_user")
@NoArgsConstructor
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Schema(description = "Registered user in the system")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Schema(description = "Unique user identifier", example = "1")
  private Long id;
  
  @Column(nullable = false)
  @Schema(description = "User's full name", example = "John Doe")
  private String name;

  @Column(unique = true, nullable = true, updatable = false)
  @Schema(description = "User's email address", example = "john.doe@example.com")
  private String email;

  @Column(nullable = true)
  @Schema(description = "BCrypt hashed password")
  private String passwordHash;

  @ElementCollection(fetch = FetchType.EAGER)
  @Enumerated(EnumType.STRING)
  @Schema(description = "User roles for RBAC")
  private Set<UserRole> roles = new HashSet<>();

  @Column(nullable = false)
  @Schema(description = "Whether the user account is enabled")
  private boolean enabled = true;

  @CreatedDate
  @Column(nullable = false, updatable = false)
  @Schema(description = "Timestamp when the user was created")
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column(nullable = false)
  @Schema(description = "Timestamp when the user was last modified")
  private LocalDateTime updatedAt;

  @Column
  @Schema(description = "Timestamp of the last login")
  private LocalDateTime lastLogin;

  @OneToMany(mappedBy = "user")
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
