package com.rsh.fitness_centre.entity;

import com.google.common.base.Objects;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class FitnessCentre {

  @Id
  private int id;

  private String name;

  @Transient
  private Set<List<Integer>> timings = new HashSet<>();
  
  @Transient
  private Set<Activity> supportedActivities = new HashSet<>();

  public FitnessCentre(int id, String name) {
    this.id = id;
    this.name = name;
    this.timings = new HashSet<>();
    this.supportedActivities = new HashSet<>();
  }

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
