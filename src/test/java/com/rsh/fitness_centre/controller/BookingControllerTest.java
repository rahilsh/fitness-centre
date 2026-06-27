package com.rsh.fitness_centre.controller;

import com.rsh.fitness_centre.entity.*;
import com.rsh.fitness_centre.entity.request.AddBookingRequest;
import com.rsh.fitness_centre.entity.response.BookingResponse;
import com.rsh.fitness_centre.exception.BookingNotFoundException;
import com.rsh.fitness_centre.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingControllerTest {

  @Mock
  private BookingService bookingService;

  @InjectMocks
  private BookingController bookingController;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  private User createUser(Long id, String name) {
    return new User(id, name);
  }

  private Slot createSlot(Long id) {
    return new Slot(id, null, Activity.YOGA, 9, 10, 20, null);
  }

  private Booking createBooking(Long id, User user, Slot slot, BookingStatus status) {
    return new Booking(id, user, slot, null, status);
  }

  @Test
  @DisplayName("Should add booking successfully")
  void testAddBookingSuccess() {
    // Arrange
    AddBookingRequest request = new AddBookingRequest();
    request.setSlotId(1L);
    request.setUserId(1L);
    User user = createUser(1L, "John");
    Slot slot = createSlot(1L);
    Booking booking = createBooking(1L, user, slot, BookingStatus.BOOKED);
    when(bookingService.addBooking(1L, 1L)).thenReturn(booking);

    // Act
    BookingResponse result = bookingController.addBooking(request).getBody();

    // Assert
    assertNotNull(result);
    assertEquals(1L, result.getId());
    assertEquals("BOOKED", result.getStatus());
    verify(bookingService, times(1)).addBooking(1L, 1L);
  }

  @Test
  @DisplayName("Should cancel booking successfully")
  void testCancelBookingSuccess() {
    // Arrange
    Long bookingId = 1L;
    User user = createUser(1L, "John");
    Slot slot = createSlot(1L);
    Booking booking = createBooking(bookingId, user, slot, BookingStatus.CANCELLED);
    when(bookingService.cancelBooking(bookingId)).thenReturn(booking);

    // Act
    BookingResponse result = bookingController.cancelBooking(bookingId).getBody();

    // Assert
    assertNotNull(result);
    assertEquals("CANCELLED", result.getStatus());
    verify(bookingService, times(1)).cancelBooking(bookingId);
  }

  @Test
  @DisplayName("Should get booking by ID successfully")
  void testGetBookingSuccess() {
    // Arrange
    Long bookingId = 1L;
    User user = createUser(1L, "John");
    Slot slot = createSlot(1L);
    Booking booking = createBooking(bookingId, user, slot, BookingStatus.BOOKED);
    when(bookingService.getBooking(bookingId)).thenReturn(booking);

    // Act
    BookingResponse result = bookingController.getBooking(bookingId).getBody();

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
    bookings.add(createBooking(1L, createUser(1L, "John"), createSlot(1L), BookingStatus.BOOKED));
    bookings.add(createBooking(2L, createUser(2L, "Jane"), createSlot(2L), BookingStatus.BOOKED));
    org.springframework.data.domain.Page<Booking> page = new org.springframework.data.domain.PageImpl<>(new java.util.ArrayList<>(bookings));
    when(bookingService.getBookings(0, 20, "bookedAt")).thenReturn(page);

    // Act
    ResponseEntity<?> response = bookingController.getBookings(0, 20, "bookedAt");
    org.springframework.data.domain.Page<BookingResponse> result = (org.springframework.data.domain.Page<BookingResponse>) response.getBody();

    // Assert
    assertNotNull(result);
    assertEquals(2, result.getContent().size());
    verify(bookingService, times(1)).getBookings(0, 20, "bookedAt");
  }

  @Test
  @DisplayName("Should throw exception when cancelling non-existent booking")
  void testCancelBookingNotFound() {
    // Arrange
    Long bookingId = 999L;
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
    Long bookingId = 999L;
    when(bookingService.getBooking(bookingId)).thenReturn(null);

    // Act
    BookingResponse result = bookingController.getBooking(bookingId).getBody();

    // Assert
    assertNull(result);
    verify(bookingService, times(1)).getBooking(bookingId);
  }

  @Test
  @DisplayName("Should add booking with different slot and user IDs")
  void testAddBookingWithDifferentIds() {
    // Arrange
    AddBookingRequest request = new AddBookingRequest();
    request.setSlotId(5L);
    request.setUserId(10L);
    User user = createUser(10L, "Jane");
    Slot slot = createSlot(5L);
    Booking booking = createBooking(1L, user, slot, BookingStatus.BOOKED);
    when(bookingService.addBooking(5L, 10L)).thenReturn(booking);

    // Act
    BookingResponse result = bookingController.addBooking(request).getBody();

    // Assert
    assertEquals(5L, result.getSlotId());
    assertEquals(10L, result.getUserId());
    verify(bookingService, times(1)).addBooking(5L, 10L);
  }

  @Test
  @DisplayName("Should handle multiple booking additions")
  void testAddMultipleBookings() {
    // Arrange
    AddBookingRequest request1 = new AddBookingRequest();
    request1.setSlotId(1L);
    request1.setUserId(1L);

    AddBookingRequest request2 = new AddBookingRequest();
    request2.setSlotId(2L);
    request2.setUserId(2L);

    Booking booking1 = createBooking(1L, createUser(1L, "John"), createSlot(1L), BookingStatus.BOOKED);
    Booking booking2 = createBooking(2L, createUser(2L, "Jane"), createSlot(2L), BookingStatus.BOOKED);

    when(bookingService.addBooking(1L, 1L)).thenReturn(booking1);
    when(bookingService.addBooking(2L, 2L)).thenReturn(booking2);

    // Act
    BookingResponse result1 = bookingController.addBooking(request1).getBody();
    BookingResponse result2 = bookingController.addBooking(request2).getBody();

    // Assert
    assertEquals(1L, result1.getId());
    assertEquals(2L, result2.getId());
    verify(bookingService, times(1)).addBooking(1L, 1L);
    verify(bookingService, times(1)).addBooking(2L, 2L);
  }

  @Test
  @DisplayName("Should get empty set of bookings")
  void testGetAllBookingsEmpty() {
    // Arrange
    org.springframework.data.domain.Page<Booking> page = new org.springframework.data.domain.PageImpl<>(new java.util.ArrayList<>());
    when(bookingService.getBookings(0, 20, "bookedAt")).thenReturn(page);

    // Act
    ResponseEntity<?> response = bookingController.getBookings(0, 20, "bookedAt");
    org.springframework.data.domain.Page<BookingResponse> result = (org.springframework.data.domain.Page<BookingResponse>) response.getBody();

    // Assert
    assertNotNull(result);
    assertEquals(0, result.getContent().size());
  }

  @Test
  @DisplayName("Should handle booking status transitions")
  void testBookingStatusTransitions() {
    // Arrange
    Long bookingId = 1L;
    User user = createUser(1L, "John");
    Slot slot = createSlot(1L);
    Booking bookedBooking = createBooking(bookingId, user, slot, BookingStatus.BOOKED);
    Booking cancelledBooking = createBooking(bookingId, user, slot, BookingStatus.CANCELLED);

    when(bookingService.getBooking(bookingId)).thenReturn(bookedBooking);
    when(bookingService.cancelBooking(bookingId)).thenReturn(cancelledBooking);

    // Act
    BookingResponse initialBooking = bookingController.getBooking(bookingId).getBody();
    BookingResponse cancelledResult = bookingController.cancelBooking(bookingId).getBody();

    // Assert
    assertEquals("BOOKED", initialBooking.getStatus());
    assertEquals("CANCELLED", cancelledResult.getStatus());
  }
}
