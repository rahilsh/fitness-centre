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
import com.rsh.fitness_centre.entity.User;
import com.rsh.fitness_centre.exception.BookingNotFoundException;
import com.rsh.fitness_centre.repository.BookingRepository;
import com.rsh.fitness_centre.repository.SlotRepository;
import com.rsh.fitness_centre.repository.UserRepository;
import java.time.LocalDate;
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
  private UserRepository userRepository;

  @BeforeEach
  void setUp() {
    bookingService = new BookingService(bookingRepository, slotRepository, userRepository);
  }

  private User createUser(Long id, String name) {
    return new User(id, name);
  }

  private Slot createSlot(Long id) {
    return new Slot(id, LocalDate.now(), null, 9, 10, 5, null);
  }

  private Booking createBooking(Long id, User user, Slot slot, BookingStatus status) {
    return new Booking(id, user, slot, null, status);
  }

  @Test
  @DisplayName("Should add booking successfully with valid slot and user")
  void testAddBookingSuccess() {
    // Arrange
    Long slotId = 1L;
    Long userId = 100L;
    User mockUser = createUser(userId, "John");
    Slot mockSlot = createSlot(slotId);
    Booking expectedBooking = createBooking(1L, mockUser, mockSlot, BookingStatus.BOOKED);

    when(slotRepository.findByIdWithPessimisticLock(slotId)).thenReturn(Optional.of(mockSlot));
    when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
    when(bookingRepository.save(any(Booking.class))).thenReturn(expectedBooking);

    // Act
    Booking result = bookingService.addBooking(slotId, userId);

    // Assert
    assertNotNull(result);
    assertEquals(1L, result.getId());
    assertEquals(slotId, result.getSlot().getId());
    assertEquals(userId, result.getUser().getId());
    assertEquals(BookingStatus.BOOKED, result.getStatus());
    verify(slotRepository, times(1)).findByIdWithPessimisticLock(slotId);
    verify(userRepository, times(1)).findById(userId);
    verify(bookingRepository, times(1)).save(any(Booking.class));
  }

  @Test
  @DisplayName("Should throw exception when slot is missing")
  void testAddBookingWithMissingSlot() {
    // Arrange
    Long slotId = 999L;
    Long userId = 100L;
    User mockUser = createUser(userId, "John");

    when(slotRepository.findByIdWithPessimisticLock(slotId)).thenReturn(Optional.empty());
    when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

    // Act & Assert
    assertThrows(BookingNotFoundException.class, () -> {
      bookingService.addBooking(slotId, userId);
    });
  }

  @Test
  @DisplayName("Should cancel booking successfully")
  void testCancelBookingSuccess() {
    // Arrange
    Long bookingId = 1L;
    User user = createUser(100L, "John");
    Slot slot = createSlot(1L);
    Booking existingBooking = createBooking(bookingId, user, slot, BookingStatus.BOOKED);
    Booking cancelledBooking = createBooking(bookingId, user, slot, BookingStatus.CANCELLED);

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

  @Test
  @DisplayName("Should throw BookingNotFoundException when trying to cancel non-existent booking")
  void testCancelBookingNotFound() {
    // Arrange
    Long bookingId = 999L;
    when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(BookingNotFoundException.class, () -> {
      bookingService.cancelBooking(bookingId);
    });
    verify(bookingRepository, times(1)).findById(bookingId);
  }

  @Test
  @DisplayName("Should get all bookings of a centre successfully")
  void testGetBookingsOfCentreSuccess() {
    // Arrange
    Long centreId = 1L;
    List<Booking> bookings = new ArrayList<>();
    User user1 = createUser(100L, "John");
    User user2 = createUser(101L, "Jane");
    Slot slot1 = createSlot(1L);
    Slot slot2 = createSlot(2L);
    bookings.add(createBooking(1L, user1, slot1, BookingStatus.BOOKED));
    bookings.add(createBooking(2L, user2, slot2, BookingStatus.BOOKED));

    when(bookingRepository.getBookingsByCentre(centreId)).thenReturn(bookings);

    // Act
    Set<Booking> result = bookingService.getBookingsOfCentre(centreId);

    // Assert
    assertNotNull(result);
    assertEquals(2, result.size());
    verify(bookingRepository, times(1)).getBookingsByCentre(centreId);
  }

  @Test
  @DisplayName("Should return empty set when centre has no bookings")
  void testGetBookingsOfCentreEmpty() {
    // Arrange
    Long centreId = 999L;
    List<Booking> emptyList = new ArrayList<>();

    when(bookingRepository.getBookingsByCentre(centreId)).thenReturn(emptyList);

    // Act
    Set<Booking> result = bookingService.getBookingsOfCentre(centreId);

    // Assert
    assertNotNull(result);
    assertEquals(0, result.size());
    verify(bookingRepository, times(1)).getBookingsByCentre(centreId);
  }

  @Test
  @DisplayName("Should get booking by ID successfully")
  void testGetBookingSuccess() {
    // Arrange
    Long bookingId = 1L;
    User user = createUser(100L, "John");
    Slot slot = createSlot(1L);
    Booking mockBooking = createBooking(bookingId, user, slot, BookingStatus.BOOKED);

    when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(mockBooking));

    // Act
    Booking result = bookingService.getBooking(bookingId);

    // Assert
    assertNotNull(result);
    assertEquals(bookingId, result.getId());
    verify(bookingRepository, times(1)).findById(bookingId);
  }

  @Test
  @DisplayName("Should return null when booking ID does not exist")
  void testGetBookingNotFound() {
    // Arrange
    Long bookingId = 999L;
    when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

    // Act
    Booking result = bookingService.getBooking(bookingId);

    // Assert
    assertNull(result);
    verify(bookingRepository, times(1)).findById(bookingId);
  }

  @Test
  @DisplayName("Should get all bookings successfully")
  void testGetAllBookingsSuccess() {
    // Arrange
    List<Booking> bookings = new ArrayList<>();
    User user1 = createUser(100L, "John");
    User user2 = createUser(101L, "Jane");
    Slot slot1 = createSlot(1L);
    Slot slot2 = createSlot(2L);
    Slot slot3 = createSlot(3L);
    Slot slot4 = createSlot(4L);
    bookings.add(createBooking(1L, user1, slot1, BookingStatus.BOOKED));
    bookings.add(createBooking(2L, user2, slot2, BookingStatus.BOOKED));
    bookings.add(createBooking(3L, user1, slot3, BookingStatus.CANCELLED));
    bookings.add(createBooking(4L, user2, slot4, BookingStatus.BOOKED));

    when(bookingRepository.findAll()).thenReturn(bookings);

    // Act
    Set<Booking> result = bookingService.getBookings();

    // Assert
    assertNotNull(result);
    assertEquals(4, result.size());
    verify(bookingRepository, times(1)).findAll();
  }

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

  @Test
  @DisplayName("Should handle bookings with different statuses correctly")
  void testGetBookingsWithDifferentStatuses() {
    // Arrange
    List<Booking> bookings = new ArrayList<>();
    User user1 = createUser(100L, "John");
    User user2 = createUser(101L, "Jane");
    Slot slot1 = createSlot(1L);
    Slot slot2 = createSlot(2L);
    Slot slot3 = createSlot(3L);
    bookings.add(createBooking(1L, user1, slot1, BookingStatus.BOOKED));
    bookings.add(createBooking(2L, user2, slot2, BookingStatus.CANCELLED));
    bookings.add(createBooking(3L, user1, slot3, BookingStatus.BOOKED));

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

  @Test
  @DisplayName("Should get bookings by user")
  void testGetBookingsByUser() {
    // Arrange
    Long userId = 100L;
    List<Booking> bookings = new ArrayList<>();
    User user = createUser(userId, "John");
    Slot slot1 = createSlot(1L);
    Slot slot2 = createSlot(2L);
    bookings.add(createBooking(1L, user, slot1, BookingStatus.BOOKED));
    bookings.add(createBooking(2L, user, slot2, BookingStatus.BOOKED));

    when(bookingRepository.getBookingsByUser(userId)).thenReturn(bookings);

    // Act
    Set<Booking> result = bookingService.getBookingsByUser(userId);

    // Assert
    assertNotNull(result);
    assertEquals(2, result.size());
    verify(bookingRepository, times(1)).getBookingsByUser(userId);
  }
}
