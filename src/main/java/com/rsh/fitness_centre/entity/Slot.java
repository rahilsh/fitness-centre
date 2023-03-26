package com.rsh.fitness_centre.entity;

import com.google.common.base.Objects;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Slot {

  private final int id;

  private final LocalDate date;
  private final Activity activity;
  private final int startTime;
  private final int endTime;
  private final int noOfSeats;
  private final int fitnessCenterId;

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
