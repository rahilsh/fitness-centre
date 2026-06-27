package com.rsh.fitness_centre.entity;

import com.google.common.base.Objects;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@NoArgsConstructor
@Getter
@EntityListeners(AuditingEntityListener.class)
@Schema(description = "Fitness centre with its details and offerings")
public class FitnessCentre {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Schema(description = "Unique fitness centre identifier", example = "1")
  private Long id;

  @Column(nullable = false, unique = true)
  @Schema(description = "Name of the fitness centre", example = "FitZone Gym")
  private String name;

  @CreatedDate
  @Column(nullable = false, updatable = false)
  @Schema(description = "Timestamp when the centre was created")
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column(nullable = false)
  @Schema(description = "Timestamp when the centre was last modified")
  private LocalDateTime updatedAt;

  @OneToMany(mappedBy = "fitnessCentre", cascade = CascadeType.ALL)
  private Set<Slot> slots = new HashSet<>();

  @Transient
  @Schema(description = "Operating timings as [open, close] hour pairs", example = "[[6, 22], [8, 20]]")
  private Set<List<Integer>> timings = new HashSet<>();
  
  @Transient
  @Schema(description = "Activities supported by the centre", example = "[\"YOGA\", \"CARDIO\"]")
  private Set<Activity> supportedActivities = new HashSet<>();

  public FitnessCentre(Long id, String name) {
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
