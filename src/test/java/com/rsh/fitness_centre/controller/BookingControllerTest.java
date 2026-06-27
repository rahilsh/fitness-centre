package com.rsh.fitness_centre.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.rsh.fitness_centre.entity.Booking;
import com.rsh.fitness_centre.entity.BookingStatus;
import com.rsh.fitness_centre.entity.request.AddBookingRequest;
import com.rsh.fitness_centre.exception.BookingNotFoundException;
import com.rsh.fitness_centre.service.BookingService;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookingController Tests")
class BookingControllerTest {

  private BookingController bookingController;

  @Mock
  private BookingService bookingService;

  @BeforeEach
  void setUp() {
    bookingController = new BookingController(bookingService);
  }

  @Test
  @DisplayName("Should add booking successfully")
  void testAddBookingSuccess() {
    // Arrange
    AddBookingRequest request = new AddBookingRequest();
    request.setSlotId(1);
    request.setUserId(1);
    Booking booking = new Booking(1, 1, 1, null, BookingStatus.BOOKED);
    when(bookingService.addBooking(1, 1)).thenReturn(booking);

    // Act
    Booking result = bookingController.addBooking(request);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.getId());
    assertEquals(BookingStatus.BOOKED, result.getStatus());
    verify(bookingService, times(1)).addBooking(1, 1);
  }

  @Test
  @DisplayName("Should cancel booking successfully")
  void testCancelBookingSuccess() {
    // Arrange
    int bookingId = 1;
    Booking booking = new Booking(bookingId, 1, 1, null, BookingStatus.CANCELLED);
    when(bookingService.cancelBooking(bookingId)).thenReturn(booking);

    // Act
    Booking result = bookingController.cancelBooking(bookingId);

    // Assert
    assertNotNull(result);
    assertEquals(BookingStatus.CANCELLED, result.getStatus());
    verify(bookingService, times(1)).cancelBooking(bookingId);
  }

  @Test
  @DisplayName("Should get booking by ID successfully")
  void testGetBookingSuccess() {
    // Arrange
    int bookingId = 1;
    Booking booking = new Booking(bookingId, 1, 1, null, BookingStatus.BOOKED);
    when(bookingService.getBooking(bookingId)).thenReturn(booking);

    // Act
    Booking result = bookingController.getBooking(bookingId);

    // Assert
    assertNotNull(result);
    assertEquals(bookingId, result.getId());
    verify(bookingService, times(1)).getBooking(bookingId);
  }

  @Test
  @DisplayName("Should get all bookings successfully")
  void testGetAllBookingsSuccess() {
    // Arrange
    Set<Booking> bookings = new HashSet<>();
    bookings.add(new Booking(1, 1, 1, null, BookingStatus.BOOKED));
    bookings.add(new Booking(2, 2, 2, null, BookingStatus.BOOKED));
    when(bookingService.getBookings()).thenReturn(bookings);

    // Act
    Set<Booking> result = bookingController.getBookings();

    // Assert
    assertNotNull(result);
    assertEquals(2, result.size());
    verify(bookingService, times(1)).getBookings();
  }

  @Test
  @DisplayName("Should throw exception when cancelling non-existent booking")
  void testCancelBookingNotFound() {
    // Arrange
    int bookingId = 999;
    when(bookingService.cancelBooking(bookingId))
        .thenThrow(new BookingNotFoundException("Invalid bookingId: " + bookingId));

    // Act & Assert
    try {
      bookingController.cancelBooking(bookingId);
    } catch (BookingNotFoundException e) {
      assertEquals("Invalid bookingId: 999", e.getMessage());
    }
    verify(bookingService, times(1)).cancelBooking(bookingId);
  }

  @Test
  @DisplayName("Should return null when getting non-existent booking")
  void testGetBookingNotFound() {
    // Arrange
    int bookingId = 999;
    when(bookingService.getBooking(bookingId)).thenReturn(null);

    // Act
    Booking result = bookingController.getBooking(bookingId);

    // Assert
    assertEquals(null, result);
    verify(bookingService, times(1)).getBooking(bookingId);
  }

  @Test
  @DisplayName("Should add booking with different slot and user IDs")
  void testAddBookingWithDifferentIds() {
    // Arrange
    AddBookingRequest request = new AddBookingRequest();
    request.setSlotId(5);
    request.setUserId(10);
    Booking booking = new Booking(1, 5, 10, null, BookingStatus.BOOKED);
    when(bookingService.addBooking(5, 10)).thenReturn(booking);

    // Act
    Booking result = bookingController.addBooking(request);

    // Assert
    assertEquals(5, result.getSlotId());
    assertEquals(10, result.getBookedBy());
    verify(bookingService, times(1)).addBooking(5, 10);
  }

  @Test
  @DisplayName("Should handle multiple booking additions")
  void testAddMultipleBookings() {
    // Arrange
    AddBookingRequest request1 = new AddBookingRequest();
    request1.setSlotId(1);
    request1.setUserId(1);

    AddBookingRequest request2 = new AddBookingRequest();
    request2.setSlotId(2);
    request2.setUserId(2);

    Booking booking1 = new Booking(1, 1, 1, null, BookingStatus.BOOKED);
    Booking booking2 = new Booking(2, 2, 2, null, BookingStatus.BOOKED);

    when(bookingService.addBooking(1, 1)).thenReturn(booking1);
    when(bookingService.addBooking(2, 2)).thenReturn(booking2);

    // Act
    Booking result1 = bookingController.addBooking(request1);
    Booking result2 = bookingController.addBooking(request2);

    // Assert
    assertEquals(1, result1.getId());
    assertEquals(2, result2.getId());
    verify(bookingService, times(1)).addBooking(1, 1);
    verify(bookingService, times(1)).addBooking(2, 2);
  }

  @Test
  @DisplayName("Should get empty set of bookings")
  void testGetAllBookingsEmpty() {
    // Arrange
    when(bookingService.getBookings()).thenReturn(new HashSet<>());

    // Act
    Set<Booking> result = bookingController.getBookings();

    // Assert
    assertNotNull(result);
    assertEquals(0, result.size());
  }

  @Test
  @DisplayName("Should handle booking status transitions")
  void testBookingStatusTransitions() {
    // Arrange
    int bookingId = 1;
    Booking bookedBooking = new Booking(bookingId, 1, 1, null, BookingStatus.BOOKED);
    Booking cancelledBooking = new Booking(bookingId, 1, 1, null, BookingStatus.CANCELLED);

    when(bookingService.getBooking(bookingId)).thenReturn(bookedBooking);
    when(bookingService.cancelBooking(bookingId)).thenReturn(cancelledBooking);

    // Act
    Booking initialBooking = bookingController.getBooking(bookingId);
    Booking cancelledResult = bookingController.cancelBooking(bookingId);

    // Assert
    assertEquals(BookingStatus.BOOKED, initialBooking.getStatus());
    assertEquals(BookingStatus.CANCELLED, cancelledResult.getStatus());
  }
}
