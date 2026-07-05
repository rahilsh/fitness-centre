package com.rsh.fitness_centre.entity;

import com.google.common.base.Objects;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Booking of an activity slot")
public class Booking {

  @Schema(description = "Unique booking identifier", example = "1")
  private Long id;

  @Schema(description = "Timestamp when the booking was made", example = "2024-06-27T10:30:00")
  private LocalDateTime bookedAt;

  @Schema(description = "Timestamp when the booking was last modified")
  private LocalDateTime updatedAt;

  @Schema(description = "Current status of the booking", example = "CONFIRMED")
  private BookingStatus status;

  @com.fasterxml.jackson.annotation.JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
  private User user;

  @com.fasterxml.jackson.annotation.JsonIgnoreProperties({"bookings", "fitnessCentre", "hibernateLazyInitializer", "handler"})
  private Slot slot;
  
  public Booking(Long id, User user, Slot slot, LocalDateTime bookedAt, BookingStatus status) {
    this.id = id;
    this.user = user;
    this.slot = slot;
    this.bookedAt = bookedAt;
    this.status = status;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Booking booking = (Booking) o;
    return Objects.equal(id, booking.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }
}
