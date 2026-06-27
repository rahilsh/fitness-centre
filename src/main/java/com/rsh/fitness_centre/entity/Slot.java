package com.rsh.fitness_centre.entity;

import com.google.common.base.Objects;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
@Schema(description = "Activity slot in a fitness centre")
public class Slot {

  @Id
  @Schema(description = "Unique slot identifier", example = "1")
  private int id;

  @Column(name = "slot_date")
  @Schema(description = "Date of the slot", example = "2024-06-27")
  private LocalDate date;
  
  @Enumerated(EnumType.STRING)
  @Schema(description = "Type of activity", example = "YOGA")
  private Activity activity;
  
  @Column(name = "start_time")
  @Schema(description = "Start time in 24-hour format", example = "9")
  private int startTime;
  
  @Column(name = "end_time")
  @Schema(description = "End time in 24-hour format", example = "10")
  private int endTime;
  
  @Column(name = "no_of_seats")
  @Schema(description = "Number of available seats", example = "20")
  private int noOfSeats;
  
  @Column(name = "fitness_centre_id")
  @Schema(description = "ID of the fitness centre", example = "1")
  private int fitnessCenterId;
  
  public Slot(int id, LocalDate date, Activity activity, int startTime, int endTime, int noOfSeats, int fitnessCenterId) {
    this.id = id;
    this.date = date;
    this.activity = activity;
    this.startTime = startTime;
    this.endTime = endTime;
    this.noOfSeats = noOfSeats;
    this.fitnessCenterId = fitnessCenterId;
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
        && fitnessCenterId == slot.fitnessCenterId && Objects.equal(date,
        slot.date);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(date, startTime, endTime, fitnessCenterId);
  }
}
