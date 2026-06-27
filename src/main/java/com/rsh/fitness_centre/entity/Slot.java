package com.rsh.fitness_centre.entity;

import com.google.common.base.Objects;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@NoArgsConstructor
@Getter
@EntityListeners(AuditingEntityListener.class)
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {"fitness_centre_id", "slot_date", "start_time"})
})
@Schema(description = "Activity slot in a fitness centre")
public class Slot {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Schema(description = "Unique slot identifier", example = "1")
  private Long id;

  @Column(name = "slot_date", nullable = false)
  @Schema(description = "Date of the slot", example = "2024-06-27")
  private LocalDate date;
  
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @Schema(description = "Type of activity", example = "YOGA")
  private Activity activity;
  
  @Column(name = "start_time", nullable = false)
  @Schema(description = "Start time in 24-hour format", example = "9")
  private int startTime;
  
  @Column(name = "end_time", nullable = false)
  @Schema(description = "End time in 24-hour format", example = "10")
  private int endTime;
  
  @Column(name = "no_of_seats", nullable = false)
  @Schema(description = "Number of available seats", example = "20")
  private int noOfSeats;

  @CreatedDate
  @Column(nullable = false, updatable = false)
  @Schema(description = "Timestamp when the slot was created")
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column(nullable = false)
  @Schema(description = "Timestamp when the slot was last modified")
  private LocalDateTime updatedAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "fitness_centre_id", nullable = false)
  private FitnessCentre fitnessCentre;

  @OneToMany(mappedBy = "slot")
  private Set<Booking> bookings = new HashSet<>();
  
  public Slot(Long id, LocalDate date, Activity activity, int startTime, int endTime, int noOfSeats, FitnessCentre fitnessCentre) {
    this.id = id;
    this.date = date;
    this.activity = activity;
    this.startTime = startTime;
    this.endTime = endTime;
    this.noOfSeats = noOfSeats;
    this.fitnessCentre = fitnessCentre;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Slot slot = (Slot) o;
    return startTime == slot.startTime && endTime == slot.endTime
        && Objects.equal(date, slot.date)
        && Objects.equal(fitnessCentre, slot.fitnessCentre);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(date, startTime, endTime, fitnessCentre);
  }
}
