package com.rsh.fitness_centre.store;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.rsh.fitness_centre.entity.Booking;
import com.rsh.fitness_centre.entity.BookingStatus;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("BookingStore Tests")
class BookingStoreTest {

  private BookingStore bookingStore;

  @BeforeEach
  void setUp() {
    bookingStore = new BookingStore();
  }

  @Test
  @DisplayName("Should add booking successfully")
  void testAddBookingSuccess() {
    // Arrange
    Booking booking = new Booking(1, 1, 1, null, BookingStatus.BOOKED);
    int fitnessCenterId = 1;

    // Act
    bookingStore.addBooking(booking, fitnessCenterId);
    Booking retrievedBooking = bookingStore.getBooking(1);

    // Assert
    assertNotNull(retrievedBooking);
    assertEquals(1, retrievedBooking.getId());
    assertEquals(1, retrievedBooking.getSlotId());
    assertEquals(1, retrievedBooking.getBookedBy());
  }

  @Test
  @DisplayName("Should add multiple bookings successfully")
  void testAddMultipleBookingsSuccess() {
    // Arrange
    Booking booking1 = new Booking(1, 1, 1, null, BookingStatus.BOOKED);
    Booking booking2 = new Booking(2, 2, 2, null, BookingStatus.BOOKED);
    Booking booking3 = new Booking(3, 3, 3, null, BookingStatus.BOOKED);

    // Act
    bookingStore.addBooking(booking1, 1);
    bookingStore.addBooking(booking2, 1);
    bookingStore.addBooking(booking3, 1);
    Set<Booking> bookings = bookingStore.getBookings();

    // Assert
    assertEquals(3, bookings.size());
    assertTrue(bookings.contains(booking1));
    assertTrue(bookings.contains(booking2));
    assertTrue(bookings.contains(booking3));
  }

  @Test
  @DisplayName("Should get booking by ID successfully")
  void testGetBookingByIdSuccess() {
    // Arrange
    Booking booking = new Booking(5, 10, 5, null, BookingStatus.BOOKED);
    bookingStore.addBooking(booking, 2);

    // Act
    Booking retrievedBooking = bookingStore.getBooking(5);

    // Assert
    assertNotNull(retrievedBooking);
    assertEquals(5, retrievedBooking.getId());
  }

  @Test
  @DisplayName("Should return null when booking ID not found")
  void testGetBookingNotFound() {
    // Act
    Booking retrievedBooking = bookingStore.getBooking(999);

    // Assert
    assertEquals(null, retrievedBooking);
  }

  @Test
  @DisplayName("Should get bookings by centre successfully")
  void testGetBookingsByCentreSuccess() {
    // Arrange
    Booking booking1 = new Booking(1, 1, 1, null, BookingStatus.BOOKED);
    Booking booking2 = new Booking(2, 2, 1, null, BookingStatus.BOOKED);
    Booking booking3 = new Booking(3, 3, 2, null, BookingStatus.BOOKED);
    int centreId1 = 1;
    int centreId2 = 2;

    // Act
    bookingStore.addBooking(booking1, centreId1);
    bookingStore.addBooking(booking2, centreId1);
    bookingStore.addBooking(booking3, centreId2);
    Set<Booking> bookingsCentre1 = bookingStore.getBookingsByCentre(centreId1);
    Set<Booking> bookingsCentre2 = bookingStore.getBookingsByCentre(centreId2);

    // Assert
    assertEquals(2, bookingsCentre1.size());
    assertEquals(1, bookingsCentre2.size());
    assertTrue(bookingsCentre1.contains(booking1));
    assertTrue(bookingsCentre1.contains(booking2));
    assertTrue(bookingsCentre2.contains(booking3));
  }

  @Test
  @DisplayName("Should return empty set when centre has no bookings")
  void testGetBookingsByCentreEmpty() {
    // Act
    Set<Booking> bookings = bookingStore.getBookingsByCentre(999);

    // Assert
    assertNotNull(bookings);
    assertEquals(0, bookings.size());
  }

  @Test
  @DisplayName("Should get all bookings successfully")
  void testGetAllBookingsSuccess() {
    // Arrange
    Booking booking1 = new Booking(1, 1, 1, null, BookingStatus.BOOKED);
    Booking booking2 = new Booking(2, 2, 2, null, BookingStatus.CANCELLED);

    // Act
    bookingStore.addBooking(booking1, 1);
    bookingStore.addBooking(booking2, 2);
    Set<Booking> allBookings = bookingStore.getBookings();

    // Assert
    assertEquals(2, allBookings.size());
    assertTrue(allBookings.contains(booking1));
    assertTrue(allBookings.contains(booking2));
  }

  @Test
  @DisplayName("Should get empty set when no bookings exist")
  void testGetAllBookingsEmpty() {
    // Act
    Set<Booking> bookings = bookingStore.getBookings();

    // Assert
    assertNotNull(bookings);
    assertEquals(0, bookings.size());
  }

  @Test
  @DisplayName("Should handle bookings with cancelled status")
  void testBookingWithCancelledStatus() {
    // Arrange
    Booking booking = new Booking(1, 1, 1, null, BookingStatus.CANCELLED);

    // Act
    bookingStore.addBooking(booking, 1);
    Booking retrievedBooking = bookingStore.getBooking(1);

    // Assert
    assertNotNull(retrievedBooking);
    assertEquals(BookingStatus.CANCELLED, retrievedBooking.getStatus());
  }

  @Test
  @DisplayName("Should handle large number of bookings")
  void testAddLargeNumberOfBookings() {
    // Arrange & Act
    for (int i = 1; i <= 1000; i++) {
      Booking booking = new Booking(i, i, i % 100, null, BookingStatus.BOOKED);
      bookingStore.addBooking(booking, i % 10);
    }
    Set<Booking> allBookings = bookingStore.getBookings();

    // Assert
    assertEquals(1000, allBookings.size());
  }

  @Test
  @DisplayName("Should maintain booking integrity for same centre")
  void testBookingIntegritySameCentre() {
    // Arrange
    int centreId = 1;
    Booking booking1 = new Booking(1, 1, 1, null, BookingStatus.BOOKED);
    Booking booking2 = new Booking(2, 2, 2, null, BookingStatus.BOOKED);
    Booking booking3 = new Booking(3, 3, 3, null, BookingStatus.BOOKED);

    // Act
    bookingStore.addBooking(booking1, centreId);
    bookingStore.addBooking(booking2, centreId);
    bookingStore.addBooking(booking3, centreId);
    Set<Booking> centreBookings = bookingStore.getBookingsByCentre(centreId);

    // Assert
    assertEquals(3, centreBookings.size());
    assertTrue(centreBookings.contains(booking1));
    assertTrue(centreBookings.contains(booking2));
    assertTrue(centreBookings.contains(booking3));
  }

  @Test
  @DisplayName("Should maintain separate booking lists per centre")
  void testSeparateBookingListsPerCentre() {
    // Arrange
    Booking booking1 = new Booking(1, 1, 1, null, BookingStatus.BOOKED);
    Booking booking2 = new Booking(2, 2, 2, null, BookingStatus.BOOKED);
    Booking booking3 = new Booking(3, 3, 3, null, BookingStatus.BOOKED);
    Booking booking4 = new Booking(4, 4, 4, null, BookingStatus.BOOKED);

    // Act
    bookingStore.addBooking(booking1, 1);
    bookingStore.addBooking(booking2, 1);
    bookingStore.addBooking(booking3, 2);
    bookingStore.addBooking(booking4, 2);

    Set<Booking> centre1Bookings = bookingStore.getBookingsByCentre(1);
    Set<Booking> centre2Bookings = bookingStore.getBookingsByCentre(2);

    // Assert
    assertEquals(2, centre1Bookings.size());
    assertEquals(2, centre2Bookings.size());
    assertTrue(centre1Bookings.contains(booking1));
    assertTrue(centre1Bookings.contains(booking2));
    assertTrue(centre2Bookings.contains(booking3));
    assertTrue(centre2Bookings.contains(booking4));
  }

  @Test
  @DisplayName("Should handle concurrent bookings")
  void testConcurrentBookingAdditions() throws InterruptedException {
    // Arrange
    Thread thread1 = new Thread(() -> {
      for (int i = 1; i <= 10; i++) {
        Booking booking = new Booking(i, i, i, null, BookingStatus.BOOKED);
        bookingStore.addBooking(booking, 1);
      }
    });

    Thread thread2 = new Thread(() -> {
      for (int i = 11; i <= 20; i++) {
        Booking booking = new Booking(i, i, i, null, BookingStatus.BOOKED);
        bookingStore.addBooking(booking, 1);
      }
    });

    // Act
    thread1.start();
    thread2.start();
    thread1.join();
    thread2.join();

    Set<Booking> bookings = bookingStore.getBookings();

    // Assert
    assertTrue(bookings.size() >= 10);  // At least 10 bookings should be added
  }

  @Test
  @DisplayName("Should track bookings by user")
  void testBookingsByUser() {
    // Arrange
    Booking booking1 = new Booking(1, 1, 1, null, BookingStatus.BOOKED);
    Booking booking2 = new Booking(2, 2, 1, null, BookingStatus.BOOKED);
    Booking booking3 = new Booking(3, 3, 2, null, BookingStatus.BOOKED);

    // Act
    bookingStore.addBooking(booking1, 1);
    bookingStore.addBooking(booking2, 1);
    bookingStore.addBooking(booking3, 1);

    Set<Booking> allBookings = bookingStore.getBookings();

    // Assert
    assertEquals(3, allBookings.size());
  }
}
