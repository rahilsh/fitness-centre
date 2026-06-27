package com.rsh.fitness_centre.entity;

import com.google.common.base.Objects;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
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
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity(name = "app_user")
@NoArgsConstructor
@Getter
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

  @CreatedDate
  @Column(nullable = false, updatable = false)
  @Schema(description = "Timestamp when the user was created")
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column(nullable = false)
  @Schema(description = "Timestamp when the user was last modified")
  private LocalDateTime updatedAt;

  @OneToMany(mappedBy = "user")
  private Set<Booking> bookings = new HashSet<>();
  
  public User(Long id, String name) {
    this.id = id;
    this.name = name;
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
