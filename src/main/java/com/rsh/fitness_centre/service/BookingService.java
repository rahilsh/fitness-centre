package com.rsh.fitness_centre.service;

import com.rsh.fitness_centre.entity.Booking;
import com.rsh.fitness_centre.entity.BookingStatus;
import com.rsh.fitness_centre.entity.Slot;
import com.rsh.fitness_centre.entity.User;
import com.rsh.fitness_centre.exception.BookingNotFoundException;
import com.rsh.fitness_centre.repository.BookingRepository;
import com.rsh.fitness_centre.repository.SlotRepository;
import com.rsh.fitness_centre.repository.UserRepository;
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
  private final UserRepository userRepository;
  private final ReentrantLock mutex = new ReentrantLock();

  @Autowired
  public BookingService(
      BookingRepository bookingRepository,
      SlotRepository slotRepository,
      UserRepository userRepository) {
    this.bookingRepository = bookingRepository;
    this.slotRepository = slotRepository;
    this.userRepository = userRepository;
  }

  @Transactional
  public Booking addBooking(Long slotId, Long userId) {
    Optional<Slot> slotOpt = slotRepository.findById(slotId);
    Optional<User> userOpt = userRepository.findById(userId);

    if (slotOpt.isEmpty() || userOpt.isEmpty()) {
      throw new BookingNotFoundException("Invalid slot or user ID");
    }

    Slot slot = slotOpt.get();
    User user = userOpt.get();

    Booking booking = new Booking(null, user, slot, null, BookingStatus.BOOKED);

    try {
      mutex.lock();
      return bookingRepository.save(booking);
    } finally {
      mutex.unlock();
    }
  }

  @Transactional
  public Booking cancelBooking(Long bookingId) {
    Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
    if (bookingOpt.isEmpty()) {
      throw new BookingNotFoundException("Invalid bookingId: " + bookingId);
    }
    Booking booking = bookingOpt.get();
    booking.setStatus(BookingStatus.CANCELLED);
    return bookingRepository.save(booking);
  }

  public Set<Booking> getBookingsOfCentre(Long centreId) {
    return new HashSet<>(bookingRepository.getBookingsByCentre(centreId));
  }

  public Booking getBooking(Long bookingId) {
    return bookingRepository.findById(bookingId).orElse(null);
  }

  public Set<Booking> getBookings() {
    return new HashSet<>(bookingRepository.findAll());
  }

  public Set<Booking> getBookingsByUser(Long userId) {
    return new HashSet<>(bookingRepository.getBookingsByUser(userId));
  }
}
