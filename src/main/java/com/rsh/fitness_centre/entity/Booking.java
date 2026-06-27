package com.rsh.fitness_centre.entity;

import com.google.common.base.Objects;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
@Schema(description = "Booking of an activity slot")
public class Booking {

  @Id
  @Schema(description = "Unique booking identifier", example = "1")
  private int id;
  
  @Schema(description = "ID of the booked slot", example = "1")
  private int slotId;
  
  @Schema(description = "ID of the user who made the booking", example = "1")
  private int bookedBy;
  
  @Schema(description = "Timestamp when the booking was made", example = "2024-06-27T10:30:00")
  private LocalDateTime bookedAt;

  @Setter
  @Enumerated(EnumType.STRING)
  @Schema(description = "Current status of the booking", example = "CONFIRMED")
  private BookingStatus status;
  
  public Booking(int id, int slotId, int bookedBy, LocalDateTime bookedAt, BookingStatus status) {
    this.id = id;
    this.slotId = slotId;
    this.bookedBy = bookedBy;
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
    return id == booking.id;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }
}
