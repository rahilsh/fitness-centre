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
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Schema(description = "Booking of an activity slot")
public class Booking {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Schema(description = "Unique booking identifier", example = "1")
  private Long id;

  @CreatedDate
  @Column(nullable = false, updatable = false)
  @Schema(description = "Timestamp when the booking was made", example = "2024-06-27T10:30:00")
  private LocalDateTime bookedAt;

  @LastModifiedDate
  @Column(nullable = false)
  @Schema(description = "Timestamp when the booking was last modified")
  private LocalDateTime updatedAt;

  @Setter
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @Schema(description = "Current status of the booking", example = "CONFIRMED")
  private BookingStatus status;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  @com.fasterxml.jackson.annotation.JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "slot_id", nullable = false)
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
