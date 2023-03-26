package com.rsh.fitness_centre.entity.request;

import com.google.common.base.Objects;
import lombok.Data;

@Data
public class AddUserRequest {
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
