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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingService {

  private static final Logger logger = LoggerFactory.getLogger(BookingService.class);
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
    logger.info("Creating booking for user {} on slot {}", userId, slotId);
    Optional<Slot> slotOpt = slotRepository.findById(slotId);
    Optional<User> userOpt = userRepository.findById(userId);

    if (slotOpt.isEmpty() || userOpt.isEmpty()) {
      logger.warn("Booking creation failed: invalid slot or user ID (slotId={}, userId={})", slotId, userId);
      throw new BookingNotFoundException("Invalid slot or user ID");
    }

    Slot slot = slotOpt.get();
    User user = userOpt.get();

    Booking booking = new Booking(null, user, slot, null, BookingStatus.BOOKED);

    try {
      mutex.lock();
      Booking savedBooking = bookingRepository.save(booking);
      logger.info("Booking created successfully with ID: {}", savedBooking.getId());
      return savedBooking;
    } finally {
      mutex.unlock();
    }
  }

  @Transactional
  public Booking cancelBooking(Long bookingId) {
    logger.info("Cancelling booking with ID: {}", bookingId);
    Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
    if (bookingOpt.isEmpty()) {
      logger.warn("Cancel booking failed: booking not found (bookingId={})", bookingId);
      throw new BookingNotFoundException("Invalid bookingId: " + bookingId);
    }
    Booking booking = bookingOpt.get();
    booking.setStatus(BookingStatus.CANCELLED);
    Booking savedBooking = bookingRepository.save(booking);
    logger.info("Booking cancelled successfully with ID: {}", bookingId);
    return savedBooking;
  }

  public Set<Booking> getBookingsOfCentre(Long centreId) {
    logger.debug("Retrieving bookings for centre: {}", centreId);
    Set<Booking> bookings = new HashSet<>(bookingRepository.getBookingsByCentre(centreId));
    logger.debug("Found {} bookings for centre {}", bookings.size(), centreId);
    return bookings;
  }

  public Booking getBooking(Long bookingId) {
    logger.debug("Retrieving booking with ID: {}", bookingId);
    Booking booking = bookingRepository.findById(bookingId).orElse(null);
    if (booking == null) {
      logger.warn("Booking not found with ID: {}", bookingId);
    }
    return booking;
  }

  public Set<Booking> getBookings() {
    logger.debug("Retrieving all bookings");
    Set<Booking> bookings = new HashSet<>(bookingRepository.findAll());
    logger.debug("Found {} bookings", bookings.size());
    return bookings;
  }

  public Set<Booking> getBookingsByUser(Long userId) {
    logger.debug("Retrieving bookings for user: {}", userId);
    Set<Booking> bookings = new HashSet<>(bookingRepository.getBookingsByUser(userId));
    logger.debug("Found {} bookings for user {}", bookings.size(), userId);
    return bookings;
  }
}
