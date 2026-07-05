package com.rsh.fitness_centre.entity;

import com.google.common.base.Objects;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
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
@Schema(description = "Activity slot in a fitness centre")
public class Slot {

  @Schema(description = "Unique slot identifier", example = "1")
  private Long id;

  @Schema(description = "Date of the slot", example = "2024-06-27")
  private LocalDate date;
  
  @Schema(description = "Type of activity", example = "YOGA")
  private Activity activity;
  
  @Schema(description = "Start time in 24-hour format", example = "9")
  private int startTime;
  
  @Schema(description = "End time in 24-hour format", example = "10")
  private int endTime;
  
  @Schema(description = "Number of available seats", example = "20")
  private int noOfSeats;

  @Schema(description = "Timestamp when the slot was created")
  private LocalDateTime createdAt;

  @Schema(description = "Timestamp when the slot was last modified")
  private LocalDateTime updatedAt;

  @com.fasterxml.jackson.annotation.JsonIgnoreProperties({"slots", "hibernateLazyInitializer", "handler"})
  private FitnessCentre fitnessCentre;

  @com.fasterxml.jackson.annotation.JsonIgnore
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
