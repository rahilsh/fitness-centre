package com.rsh.fitness_centre.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.rsh.fitness_centre.entity.Activity;
import com.rsh.fitness_centre.entity.Booking;
import com.rsh.fitness_centre.entity.BookingStatus;
import com.rsh.fitness_centre.entity.Slot;
import com.rsh.fitness_centre.exception.BookingNotFoundException;
import com.rsh.fitness_centre.store.BookingStore;
import com.rsh.fitness_centre.store.FitnessCenterSlotStore;
import com.rsh.fitness_centre.util.SequenceGenerator;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookingService Tests")
class BookingServiceTest {

  private BookingService bookingService;

  @Mock
  private BookingStore bookingStore;

  @Mock
  private FitnessCenterSlotStore fitnessCenterSlotStore;

  @Mock
  private SequenceGenerator sequenceGenerator;

  @BeforeEach
  void setUp() {
    bookingService = new BookingService(bookingStore, fitnessCenterSlotStore, sequenceGenerator);
  }

  @Test
  @DisplayName("Should add booking successfully")
  void testAddBookingSuccess() {
    // Arrange
    int slotId = 1;
    int userId = 1;
    int bookingId = 1;
    int fitnessCenterId = 1;
    Slot slot = new Slot(slotId, LocalDate.now(), Activity.YOGA, 9, 10, 10, fitnessCenterId);
    
    when(sequenceGenerator.getNext("Booking")).thenReturn(bookingId);
    when(fitnessCenterSlotStore.getSlotById(slotId)).thenReturn(slot);

    // Act
    Booking result = bookingService.addBooking(slotId, userId);

    // Assert
    assertNotNull(result);
    assertEquals(bookingId, result.getId());
    assertEquals(slotId, result.getSlotId());
    assertEquals(userId, result.getBookedBy());
    assertEquals(BookingStatus.BOOKED, result.getStatus());
    verify(sequenceGenerator, times(1)).getNext("Booking");
    verify(fitnessCenterSlotStore, times(1)).getSlotById(slotId);
    verify(bookingStore, times(1)).addBooking(result, fitnessCenterId);
  }

  @Test
  @DisplayName("Should cancel booking successfully")
  void testCancelBookingSuccess() {
    // Arrange
    int bookingId = 1;
    int slotId = 1;
    int userId = 1;
    Booking booking = new Booking(bookingId, slotId, userId, null, BookingStatus.BOOKED);
    when(bookingStore.getBooking(bookingId)).thenReturn(booking);

    // Act
    Booking result = bookingService.cancelBooking(bookingId);

    // Assert
    assertNotNull(result);
    assertEquals(BookingStatus.CANCELLED, result.getStatus());
    verify(bookingStore, times(1)).getBooking(bookingId);
  }

  @Test
  @DisplayName("Should throw BookingNotFoundException when cancelling non-existent booking")
  void testCancelBookingNotFound() {
    // Arrange
    int bookingId = 999;
    when(bookingStore.getBooking(bookingId)).thenReturn(null);

    // Act & Assert
    assertThrows(
        BookingNotFoundException.class,
        () -> bookingService.cancelBooking(bookingId),
        "Should throw BookingNotFoundException");
    verify(bookingStore, times(1)).getBooking(bookingId);
  }

  @Test
  @DisplayName("Should get bookings of centre successfully")
  void testGetBookingsOfCentreSuccess() {
    // Arrange
    int centreId = 1;
    Set<Booking> bookings = new HashSet<>();
    bookings.add(new Booking(1, 1, 1, null, BookingStatus.BOOKED));
    bookings.add(new Booking(2, 2, 2, null, BookingStatus.BOOKED));
    when(bookingStore.getBookingsByCentre(centreId)).thenReturn(bookings);

    // Act
    Set<Booking> result = bookingService.getBookingsOfCentre(centreId);

    // Assert
    assertNotNull(result);
    assertEquals(2, result.size());
    verify(bookingStore, times(1)).getBookingsByCentre(centreId);
  }

  @Test
  @DisplayName("Should get empty set when centre has no bookings")
  void testGetBookingsOfCentreEmpty() {
    // Arrange
    int centreId = 999;
    when(bookingStore.getBookingsByCentre(centreId)).thenReturn(new HashSet<>());

    // Act
    Set<Booking> result = bookingService.getBookingsOfCentre(centreId);

    // Assert
    assertNotNull(result);
    assertEquals(0, result.size());
    verify(bookingStore, times(1)).getBookingsByCentre(centreId);
  }

  @Test
  @DisplayName("Should get booking by ID successfully")
  void testGetBookingSuccess() {
    // Arrange
    int bookingId = 1;
    Booking booking = new Booking(bookingId, 1, 1, null, BookingStatus.BOOKED);
    when(bookingStore.getBooking(bookingId)).thenReturn(booking);

    // Act
    Booking result = bookingService.getBooking(bookingId);

    // Assert
    assertNotNull(result);
    assertEquals(bookingId, result.getId());
    verify(bookingStore, times(1)).getBooking(bookingId);
  }

  @Test
  @DisplayName("Should return null when booking not found")
  void testGetBookingNotFound() {
    // Arrange
    int bookingId = 999;
    when(bookingStore.getBooking(bookingId)).thenReturn(null);

    // Act
    Booking result = bookingService.getBooking(bookingId);

    // Assert
    assertEquals(null, result);
    verify(bookingStore, times(1)).getBooking(bookingId);
  }

  @Test
  @DisplayName("Should get all bookings successfully")
  void testGetBookingsSuccess() {
    // Arrange
    Set<Booking> bookings = new HashSet<>();
    bookings.add(new Booking(1, 1, 1, null, BookingStatus.BOOKED));
    bookings.add(new Booking(2, 2, 2, null, BookingStatus.CANCELLED));
    when(bookingStore.getBookings()).thenReturn(bookings);

    // Act
    Set<Booking> result = bookingService.getBookings();

    // Assert
    assertNotNull(result);
    assertEquals(2, result.size());
    verify(bookingStore, times(1)).getBookings();
  }

  @Test
  @DisplayName("Should get empty set when no bookings exist")
  void testGetBookingsEmpty() {
    // Arrange
    when(bookingStore.getBookings()).thenReturn(new HashSet<>());

    // Act
    Set<Booking> result = bookingService.getBookings();

    // Assert
    assertNotNull(result);
    assertEquals(0, result.size());
    verify(bookingStore, times(1)).getBookings();
  }

  @Test
  @DisplayName("Should maintain booking status after addition")
  void testBookingStatusAfterAddition() {
    // Arrange
    int slotId = 1;
    int userId = 1;
    int bookingId = 1;
    int fitnessCenterId = 1;
    Slot slot = new Slot(slotId, LocalDate.now(), Activity.CARDIO, 10, 11, 20, fitnessCenterId);
    
    when(sequenceGenerator.getNext("Booking")).thenReturn(bookingId);
    when(fitnessCenterSlotStore.getSlotById(slotId)).thenReturn(slot);

    // Act
    Booking result = bookingService.addBooking(slotId, userId);

    // Assert
    assertEquals(BookingStatus.BOOKED, result.getStatus());
  }

  @Test
  @DisplayName("Should handle concurrent booking additions")
  void testConcurrentBookingAdditions() {
    // Arrange
    Slot slot = new Slot(1, LocalDate.now(), Activity.WEIGHTS, 8, 9, 15, 1);
    when(sequenceGenerator.getNext("Booking")).thenReturn(1).thenReturn(2);
    when(fitnessCenterSlotStore.getSlotById(1)).thenReturn(slot);

    // Act
    Booking booking1 = bookingService.addBooking(1, 1);
    Booking booking2 = bookingService.addBooking(1, 2);

    // Assert
    assertNotNull(booking1);
    assertNotNull(booking2);
    assertEquals(1, booking1.getId());
    assertEquals(2, booking2.getId());
  }
}
