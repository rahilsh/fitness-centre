package com.rsh.fitness_centre.entity;

import com.google.common.base.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class User {

  private final int id;
  private final String name;

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
