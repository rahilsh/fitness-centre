package com.rsh.fitness_centre.entity;

import com.google.common.base.Objects;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class Booking {

  private final int id;
  private final int slotId;
  private final int bookedBy;
  private final LocalDateTime bookedAt;

  @Setter
  private BookingStatus status;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Booking booking = (Booking) o;
    return id == booking.id;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }
}
