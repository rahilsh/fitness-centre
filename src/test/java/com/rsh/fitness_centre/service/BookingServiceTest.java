package com.rsh.fitness_centre.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.rsh.fitness_centre.entity.Booking;
import com.rsh.fitness_centre.entity.BookingStatus;
import com.rsh.fitness_centre.entity.Slot;
import com.rsh.fitness_centre.exception.BookingNotFoundException;
import com.rsh.fitness_centre.repository.BookingRepository;
import com.rsh.fitness_centre.repository.SlotRepository;
import com.rsh.fitness_centre.util.SequenceGenerator;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
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
  private BookingRepository bookingRepository;

  @Mock
  private SlotRepository slotRepository;

  @Mock
  private SequenceGenerator sequenceGenerator;

  @BeforeEach
  void setUp() {
    bookingService = new BookingService(bookingRepository, slotRepository, sequenceGenerator);
  }

  // Test: addBooking with valid slot and user
  @Test
  @DisplayName("Should add booking successfully with valid slot and user")
  void testAddBookingSuccess() {
    // Arrange
    int slotId = 1;
    int userId = 100;
    int bookingId = 1;
    Slot mockSlot = new Slot(slotId, LocalDate.now(), null, 9, 10, 5, 1);
    Booking expectedBooking = new Booking(bookingId, slotId, userId, LocalDateTime.now(), BookingStatus.BOOKED);

    when(sequenceGenerator.getNext("Booking")).thenReturn(bookingId);
    when(slotRepository.findById(slotId)).thenReturn(Optional.of(mockSlot));
    when(bookingRepository.save(expectedBooking)).thenReturn(expectedBooking);

    // Act
    Booking result = bookingService.addBooking(slotId, userId);

    // Assert
    assertNotNull(result);
    assertEquals(bookingId, result.getId());
    assertEquals(slotId, result.getSlotId());
    assertEquals(userId, result.getBookedBy());
    assertEquals(BookingStatus.BOOKED, result.getStatus());
    verify(sequenceGenerator, times(1)).getNext("Booking");
    verify(slotRepository, times(1)).findById(slotId);
    verify(bookingRepository, times(1)).save(result);
  }

  // Test: addBooking with missing/non-existent slot
  @Test
  @DisplayName("Should add booking even when slot is missing (service doesn't validate slot existence)")
  void testAddBookingWithMissingSlot() {
    // Arrange
    int slotId = 999;
    int userId = 100;
    int bookingId = 2;
    Booking expectedBooking = new Booking(bookingId, slotId, userId, LocalDateTime.now(), BookingStatus.BOOKED);

    when(sequenceGenerator.getNext("Booking")).thenReturn(bookingId);
    when(slotRepository.findById(slotId)).thenReturn(Optional.empty());
    when(bookingRepository.save(expectedBooking)).thenReturn(expectedBooking);

    // Act
    Booking result = bookingService.addBooking(slotId, userId);

    // Assert
    assertNotNull(result);
    assertEquals(bookingId, result.getId());
    assertEquals(slotId, result.getSlotId());
    verify(slotRepository, times(1)).findById(slotId);
    verify(bookingRepository, times(1)).save(result);
  }

  // Test: cancelBooking successfully
  @Test
  @DisplayName("Should cancel booking successfully")
  void testCancelBookingSuccess() {
    // Arrange
    int bookingId = 1;
    int slotId = 1;
    int userId = 100;
    Booking existingBooking = new Booking(bookingId, slotId, userId, LocalDateTime.now(), BookingStatus.BOOKED);
    Booking cancelledBooking = new Booking(bookingId, slotId, userId, LocalDateTime.now(), BookingStatus.CANCELLED);

    when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(existingBooking));
    when(bookingRepository.save(existingBooking)).thenReturn(cancelledBooking);

    // Act
    Booking result = bookingService.cancelBooking(bookingId);

    // Assert
    assertNotNull(result);
    assertEquals(bookingId, result.getId());
    assertEquals(BookingStatus.CANCELLED, result.getStatus());
    verify(bookingRepository, times(1)).findById(bookingId);
    verify(bookingRepository, times(1)).save(existingBooking);
  }

  // Test: cancelBooking with non-existent booking
  @Test
  @DisplayName("Should throw BookingNotFoundException when trying to cancel non-existent booking")
  void testCancelBookingNotFound() {
    // Arrange
    int bookingId = 999;
    when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(BookingNotFoundException.class, () -> {
      bookingService.cancelBooking(bookingId);
    });
    verify(bookingRepository, times(1)).findById(bookingId);
    verify(bookingRepository, times(0)).save(null);
  }

  // Test: getBookingsOfCentre
  @Test
  @DisplayName("Should get all bookings of a centre successfully")
  void testGetBookingsOfCentreSuccess() {
    // Arrange
    int centreId = 1;
    List<Booking> bookings = new ArrayList<>();
    bookings.add(new Booking(1, 1, 100, LocalDateTime.now(), BookingStatus.BOOKED));
    bookings.add(new Booking(2, 2, 101, LocalDateTime.now(), BookingStatus.BOOKED));
    bookings.add(new Booking(3, 3, 102, LocalDateTime.now(), BookingStatus.CANCELLED));

    when(bookingRepository.getBookingsByCentre(centreId)).thenReturn(bookings);

    // Act
    Set<Booking> result = bookingService.getBookingsOfCentre(centreId);

    // Assert
    assertNotNull(result);
    assertEquals(3, result.size());
    assertTrue(result.stream().anyMatch(b -> b.getId() == 1));
    assertTrue(result.stream().anyMatch(b -> b.getId() == 2));
    assertTrue(result.stream().anyMatch(b -> b.getId() == 3));
    verify(bookingRepository, times(1)).getBookingsByCentre(centreId);
  }

  // Test: getBookingsOfCentre with empty results
  @Test
  @DisplayName("Should return empty set when centre has no bookings")
  void testGetBookingsOfCentreEmpty() {
    // Arrange
    int centreId = 999;
    List<Booking> emptyList = new ArrayList<>();

    when(bookingRepository.getBookingsByCentre(centreId)).thenReturn(emptyList);

    // Act
    Set<Booking> result = bookingService.getBookingsOfCentre(centreId);

    // Assert
    assertNotNull(result);
    assertEquals(0, result.size());
    verify(bookingRepository, times(1)).getBookingsByCentre(centreId);
  }

  // Test: getBooking by ID - found
  @Test
  @DisplayName("Should get booking by ID successfully")
  void testGetBookingSuccess() {
    // Arrange
    int bookingId = 1;
    Booking mockBooking = new Booking(bookingId, 1, 100, LocalDateTime.now(), BookingStatus.BOOKED);

    when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(mockBooking));

    // Act
    Booking result = bookingService.getBooking(bookingId);

    // Assert
    assertNotNull(result);
    assertEquals(bookingId, result.getId());
    assertEquals(100, result.getBookedBy());
    verify(bookingRepository, times(1)).findById(bookingId);
  }

  // Test: getBooking by ID - not found
  @Test
  @DisplayName("Should return null when booking ID does not exist")
  void testGetBookingNotFound() {
    // Arrange
    int bookingId = 999;
    when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

    // Act
    Booking result = bookingService.getBooking(bookingId);

    // Assert
    assertNull(result);
    verify(bookingRepository, times(1)).findById(bookingId);
  }

  // Test: getBookings - return all bookings
  @Test
  @DisplayName("Should get all bookings successfully")
  void testGetAllBookingsSuccess() {
    // Arrange
    List<Booking> bookings = new ArrayList<>();
    bookings.add(new Booking(1, 1, 100, LocalDateTime.now(), BookingStatus.BOOKED));
    bookings.add(new Booking(2, 2, 101, LocalDateTime.now(), BookingStatus.BOOKED));
    bookings.add(new Booking(3, 3, 102, LocalDateTime.now(), BookingStatus.CANCELLED));
    bookings.add(new Booking(4, 4, 103, LocalDateTime.now(), BookingStatus.BOOKED));

    when(bookingRepository.findAll()).thenReturn(bookings);

    // Act
    Set<Booking> result = bookingService.getBookings();

    // Assert
    assertNotNull(result);
    assertEquals(4, result.size());
    assertTrue(result.stream().anyMatch(b -> b.getId() == 1));
    assertTrue(result.stream().anyMatch(b -> b.getId() == 4));
    verify(bookingRepository, times(1)).findAll();
  }

  // Test: getBookings with empty results
  @Test
  @DisplayName("Should return empty set when no bookings exist")
  void testGetAllBookingsEmpty() {
    // Arrange
    List<Booking> emptyList = new ArrayList<>();
    when(bookingRepository.findAll()).thenReturn(emptyList);

    // Act
    Set<Booking> result = bookingService.getBookings();

    // Assert
    assertNotNull(result);
    assertEquals(0, result.size());
    verify(bookingRepository, times(1)).findAll();
  }

  // Test: Multiple bookings with different statuses
  @Test
  @DisplayName("Should handle bookings with different statuses correctly")
  void testGetBookingsWithDifferentStatuses() {
    // Arrange
    List<Booking> bookings = new ArrayList<>();
    bookings.add(new Booking(1, 1, 100, LocalDateTime.now(), BookingStatus.BOOKED));
    bookings.add(new Booking(2, 2, 101, LocalDateTime.now(), BookingStatus.CANCELLED));
    bookings.add(new Booking(3, 3, 102, LocalDateTime.now(), BookingStatus.BOOKED));

    when(bookingRepository.findAll()).thenReturn(bookings);

    // Act
    Set<Booking> result = bookingService.getBookings();

    // Assert
    assertNotNull(result);
    assertEquals(3, result.size());
    long bookedCount = result.stream().filter(b -> b.getStatus() == BookingStatus.BOOKED).count();
    long cancelledCount = result.stream().filter(b -> b.getStatus() == BookingStatus.CANCELLED).count();
    assertEquals(2, bookedCount);
    assertEquals(1, cancelledCount);
    verify(bookingRepository, times(1)).findAll();
  }

  // Test: Sequential booking IDs
  @Test
  @DisplayName("Should generate sequential booking IDs")
  void testSequentialBookingIds() {
    // Arrange
    int slotId = 1;
    int userId = 100;
    Slot mockSlot = new Slot(slotId, LocalDate.now(), null, 9, 10, 5, 1);

    when(sequenceGenerator.getNext("Booking")).thenReturn(1).thenReturn(2).thenReturn(3);
    when(slotRepository.findById(slotId)).thenReturn(Optional.of(mockSlot));
    when(bookingRepository.save(any(Booking.class))).thenReturn(new Booking(1, slotId, userId, LocalDateTime.now(), BookingStatus.BOOKED));

    // Act
    bookingService.addBooking(slotId, userId);
    bookingService.addBooking(slotId, userId);
    bookingService.addBooking(slotId, userId);

    // Assert
    verify(sequenceGenerator, times(3)).getNext("Booking");
    verify(bookingRepository, times(3)).save(any(Booking.class));
  }
}
