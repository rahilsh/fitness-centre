package com.rsh.fitness_centre.entity;

import com.google.common.base.Objects;
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
public class Slot {

  @Id
  private int id;

  @Column(name = "slot_date")
  private LocalDate date;
  
  @Enumerated(EnumType.STRING)
  private Activity activity;
  
  @Column(name = "start_time")
  private int startTime;
  
  @Column(name = "end_time")
  private int endTime;
  
  @Column(name = "no_of_seats")
  private int noOfSeats;
  
  @Column(name = "fitness_centre_id")
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
