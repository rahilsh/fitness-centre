package com.rsh.fitness_centre.entity;

import com.google.common.base.Objects;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "app_user")
@NoArgsConstructor
@Getter
@Schema(description = "Registered user in the system")
public class User {

  @Id
  @Schema(description = "Unique user identifier", example = "1")
  private int id;
  
  @Schema(description = "User's full name", example = "John Doe")
  private String name;
  
  public User(int id, String name) {
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
