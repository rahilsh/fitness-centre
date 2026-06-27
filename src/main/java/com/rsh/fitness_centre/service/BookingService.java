package com.rsh.fitness_centre.service;

import com.rsh.fitness_centre.entity.Booking;
import com.rsh.fitness_centre.entity.BookingStatus;
import com.rsh.fitness_centre.entity.Slot;
import com.rsh.fitness_centre.exception.BookingNotFoundException;
import com.rsh.fitness_centre.repository.BookingRepository;
import com.rsh.fitness_centre.repository.SlotRepository;
import com.rsh.fitness_centre.util.SequenceGenerator;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingService {

  private final BookingRepository bookingRepository;
  private final SlotRepository slotRepository;
  private final SequenceGenerator sequenceGenerator;
  private final ReentrantLock mutex = new ReentrantLock();

  @Autowired
  public BookingService(
      BookingRepository bookingRepository,
      SlotRepository slotRepository,
      SequenceGenerator sequenceGenerator) {
    this.bookingRepository = bookingRepository;
    this.slotRepository = slotRepository;
    this.sequenceGenerator = sequenceGenerator;
  }

  @Transactional
  public Booking addBooking(int slotId, int userId) {
    Booking booking =
        new Booking(
            sequenceGenerator.getNext("Booking"),
            slotId,
            userId,
            LocalDateTime.now(),
            BookingStatus.BOOKED);
    Optional<Slot> slot = slotRepository.findById(slotId);

    try {
      mutex.lock();
      bookingRepository.save(booking);
      return booking;
    } finally {
      mutex.unlock();
    }
  }

  @Transactional
  public Booking cancelBooking(int bookingId) {
    Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
    if (bookingOpt.isEmpty()) {
      throw new BookingNotFoundException("Invalid bookingId: " + bookingId);
    }
    Booking booking = bookingOpt.get();
    booking.setStatus(BookingStatus.CANCELLED);
    bookingRepository.save(booking);
    return booking;
  }

  public Set<Booking> getBookingsOfCentre(int centreId) {
    return new HashSet<>(bookingRepository.getBookingsByCentre(centreId));
  }

  public Booking getBooking(int bookingId) {
    return bookingRepository.findById(bookingId).orElse(null);
  }

  public Set<Booking> getBookings() {
    return new HashSet<>(bookingRepository.findAll());
  }
}
