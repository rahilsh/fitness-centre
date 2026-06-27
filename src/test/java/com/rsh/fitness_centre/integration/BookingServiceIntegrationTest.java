package com.rsh.fitness_centre.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.rsh.fitness_centre.entity.Activity;
import com.rsh.fitness_centre.entity.Booking;
import com.rsh.fitness_centre.entity.BookingStatus;
import com.rsh.fitness_centre.entity.FitnessCentre;
import com.rsh.fitness_centre.entity.Slot;
import com.rsh.fitness_centre.entity.User;
import com.rsh.fitness_centre.exception.BookingNotFoundException;
import com.rsh.fitness_centre.repository.BookingRepository;
import com.rsh.fitness_centre.repository.FitnessCentreRepository;
import com.rsh.fitness_centre.repository.SlotRepository;
import com.rsh.fitness_centre.repository.UserRepository;
import com.rsh.fitness_centre.repository.RefreshTokenRepository;
import com.rsh.fitness_centre.service.BookingService;
import com.rsh.fitness_centre.service.FitnessCentreService;
import com.rsh.fitness_centre.service.UserService;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("BookingService Integration Tests")
@Transactional
class BookingServiceIntegrationTest {

  @Autowired
  private BookingService bookingService;

  @Autowired
  private UserService userService;

  @Autowired
  private FitnessCentreService fitnessCentreService;

  @Autowired
  private BookingRepository bookingRepository;

  @Autowired
  private SlotRepository slotRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RefreshTokenRepository refreshTokenRepository;

  @Autowired
  private FitnessCentreRepository fitnessCentreRepository;

  private User testUser;
  private Slot testSlot;
  private FitnessCentre testCentre;

  @BeforeEach
  void setUp() {
    bookingRepository.deleteAll();
    refreshTokenRepository.deleteAll();
    slotRepository.deleteAll();
    userRepository.deleteAll();
    fitnessCentreRepository.deleteAll();

    // Create test centre
    testCentre = fitnessCentreService.addCentre("Test Centre", null, null);

    // Create test user
    testUser = userService.addUser("Test User");

    // Create test slot
    testSlot = new Slot(
        null,
        LocalDate.now(),
        Activity.YOGA,
        9,
        10,
        20,
        testCentre
    );
    testSlot = slotRepository.save(testSlot);
  }

  @Test
  @DisplayName("Should create booking and persist to database")
  void testAddBookingPersistsToDatabase() {
    // Act
    Booking booking = bookingService.addBooking(testSlot.getId(), testUser.getId());

    // Assert
    assertNotNull(booking);
    assertNotNull(booking.getId());
    assertEquals(testSlot.getId(), booking.getSlot().getId());
    assertEquals(testUser.getId(), booking.getUser().getId());
    assertEquals(BookingStatus.BOOKED, booking.getStatus());

    // Verify persistence
    Booking persistedBooking = bookingRepository.findById(booking.getId()).orElse(null);
    assertNotNull(persistedBooking);
    assertEquals(BookingStatus.BOOKED, persistedBooking.getStatus());
  }

  @Test
  @DisplayName("Should cancel booking and update status in database")
  void testCancelBookingUpdatesStatus() {
    // Arrange
    Booking booking = bookingService.addBooking(testSlot.getId(), testUser.getId());

    // Act
    Booking cancelledBooking = bookingService.cancelBooking(booking.getId());

    // Assert
    assertEquals(BookingStatus.CANCELLED, cancelledBooking.getStatus());

    // Verify persistence
    Booking persistedBooking = bookingRepository.findById(booking.getId()).orElse(null);
    assertNotNull(persistedBooking);
    assertEquals(BookingStatus.CANCELLED, persistedBooking.getStatus());
  }

  @Test
  @DisplayName("Should throw exception when cancelling non-existent booking")
  void testCancelNonExistentBookingThrowsException() {
    // Act & Assert
    assertThrows(BookingNotFoundException.class, () -> {
      bookingService.cancelBooking(9999L);
    });
  }

  @Test
  @DisplayName("Should retrieve booking by ID from database")
  void testGetBookingFetchesFromDatabase() {
    // Arrange
    Booking booking = bookingService.addBooking(testSlot.getId(), testUser.getId());

    // Act
    Booking retrievedBooking = bookingService.getBooking(booking.getId());

    // Assert
    assertNotNull(retrievedBooking);
    assertEquals(booking.getId(), retrievedBooking.getId());
    assertEquals(testUser.getId(), retrievedBooking.getUser().getId());
  }

  @Test
  @DisplayName("Should retrieve all bookings from database")
  void testGetAllBookingsFetchesFromDatabase() {
    // Arrange
    Booking booking1 = bookingService.addBooking(testSlot.getId(), testUser.getId());
    
    User user2 = userService.addUser("Another User");
    Booking booking2 = bookingService.addBooking(testSlot.getId(), user2.getId());

    // Act
    var allBookings = bookingService.getBookings();

    // Assert
    assertEquals(2, allBookings.size());
    assertTrue(allBookings.stream().anyMatch(b -> b.getId().equals(booking1.getId())));
    assertTrue(allBookings.stream().anyMatch(b -> b.getId().equals(booking2.getId())));
  }

  @Test
  @DisplayName("Should retrieve bookings by centre ID from database")
  void testGetBookingsByCentreFetchesFromDatabase() {
    // Arrange - Create multiple slots and bookings for same centre
    Slot slot2 = new Slot(
        null,
        LocalDate.now().plusDays(1),
        Activity.CARDIO,
        10,
        11,
        20,
        testCentre
    );
    slot2 = slotRepository.save(slot2);

    User user2 = userService.addUser("User 2");
    Booking booking1 = bookingService.addBooking(testSlot.getId(), testUser.getId());
    Booking booking2 = bookingService.addBooking(slot2.getId(), user2.getId());

    // Act
    var centreBookings = bookingService.getBookingsOfCentre(testCentre.getId());

    // Assert
    assertEquals(2, centreBookings.size());
  }

  @Test
  @DisplayName("Should maintain booking status across transactions")
  void testBookingStatusPersistenceAcrossTransactions() {
    // Arrange
    Booking booking = bookingService.addBooking(testSlot.getId(), testUser.getId());
    Long bookingId = booking.getId();

    // Act - Cancel and verify
    Booking cancelledBooking = bookingService.cancelBooking(bookingId);
    assertEquals(BookingStatus.CANCELLED, cancelledBooking.getStatus());

    // Verify in new transaction
    Booking retrievedBooking = bookingService.getBooking(bookingId);
    assertEquals(BookingStatus.CANCELLED, retrievedBooking.getStatus());
  }

  @Test
  @DisplayName("Should handle multiple concurrent bookings")
  void testMultipleConcurrentBookings() {
    // Arrange
    User user1 = testUser;
    User user2 = userService.addUser("User 2");
    User user3 = userService.addUser("User 3");

    // Act
    Booking booking1 = bookingService.addBooking(testSlot.getId(), user1.getId());
    Booking booking2 = bookingService.addBooking(testSlot.getId(), user2.getId());
    Booking booking3 = bookingService.addBooking(testSlot.getId(), user3.getId());

    // Assert
    assertEquals(3, bookingRepository.count());
    assertTrue(bookingRepository.findById(booking1.getId()).isPresent());
    assertTrue(bookingRepository.findById(booking2.getId()).isPresent());
    assertTrue(bookingRepository.findById(booking3.getId()).isPresent());
  }

  @Test
  @DisplayName("Should retrieve empty set when no bookings exist for centre")
  void testGetBookingsByCentreReturnsEmptySet() {
    // Act
    var centreBookings = bookingService.getBookingsOfCentre(9999L);

    // Assert
    assertNotNull(centreBookings);
    assertTrue(centreBookings.isEmpty());
  }
}
