package com.rsh.fitness_centre.service;

import com.rsh.fitness_centre.entity.Booking;
import com.rsh.fitness_centre.entity.BookingStatus;
import com.rsh.fitness_centre.entity.Slot;
import com.rsh.fitness_centre.exception.BookingNotFoundException;
import com.rsh.fitness_centre.store.BookingStore;
import com.rsh.fitness_centre.store.FitnessCenterSlotStore;
import com.rsh.fitness_centre.util.SequenceGenerator;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookingService {

  private final BookingStore bookingStore;
  private final FitnessCenterSlotStore fitnessCenterSlotStore;
  private final SequenceGenerator sequenceGenerator;
  private final ReentrantLock mutex = new ReentrantLock();

  @Autowired
  public BookingService(
      BookingStore bookingStore,
      FitnessCenterSlotStore fitnessCenterSlotStore,
      SequenceGenerator sequenceGenerator) {
    this.bookingStore = bookingStore;
    this.fitnessCenterSlotStore = fitnessCenterSlotStore;
    this.sequenceGenerator = sequenceGenerator;
  }

  public Booking addBooking(int slotId, int userId) {
    Booking booking =
        new Booking(
            sequenceGenerator.getNext("Booking"),
            slotId,
            userId,
            LocalDateTime.now(),
            BookingStatus.BOOKED);
    Slot slot = fitnessCenterSlotStore.getSlotById(slotId);

    try {
      mutex.lock();
      bookingStore.addBooking(booking, slot.getFitnessCenterId());
      return booking;
    } finally {
      mutex.unlock();
    }
  }

  public Booking cancelBooking(int bookingId) {
    Booking booking = bookingStore.getBooking(bookingId);
    if (booking == null) {
      throw new BookingNotFoundException("Invalid bookingId: " + bookingId);
    }
    booking.setStatus(BookingStatus.CANCELLED);
    return booking;
  }

  public Set<Booking> getBookingsOfCentre(int centreId) {
    return bookingStore.getBookingsByCentre(centreId);
  }

  public Booking getBooking(int bookingId) {
    return bookingStore.getBooking(bookingId);
  }

  public Set<Booking> getBookings() {
    return bookingStore.getBookings();
  }
}
