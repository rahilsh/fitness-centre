package com.rsh.fitness_centre.entity;

import com.google.common.base.Objects;
import java.util.HashSet;
import java.util.List;import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class FitnessCentre {


  private final int id;

  private final String name;

  private final Set<List<Integer>> timings = new HashSet<>();
  private final Set<Activity> supportedActivities = new HashSet<>();

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FitnessCentre that = (FitnessCentre) o;
    return Objects.equal(name, that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(name);
  }
}
