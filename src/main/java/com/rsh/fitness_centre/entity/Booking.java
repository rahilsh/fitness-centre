package com.rsh.fitness_centre.entity;

import com.google.common.base.Objects;
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
public class Booking {

  @Id
  private int id;
  
  private int slotId;
  
  private int bookedBy;
  
  private LocalDateTime bookedAt;

  @Setter
  @Enumerated(EnumType.STRING)
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
