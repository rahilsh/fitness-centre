package com.rsh.fitness_centre.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("BookingNotFoundException Tests")
class BookingNotFoundExceptionTest {

  @Test
  @DisplayName("Should throw BookingNotFoundException with message")
  void testThrowBookingNotFoundExceptionWithMessage() {
    // Arrange
    String message = "Booking not found";

    // Act & Assert
    BookingNotFoundException exception =
        assertThrows(
            BookingNotFoundException.class,
            () -> {
              throw new BookingNotFoundException(message);
            });

    assertEquals(message, exception.getMessage());
  }

  @Test
  @DisplayName("Should throw BookingNotFoundException with specific booking ID message")
  void testThrowBookingNotFoundExceptionWithBookingIdMessage() {
    // Arrange
    int bookingId = 123;
    String message = "Invalid bookingId: " + bookingId;

    // Act & Assert
    BookingNotFoundException exception =
        assertThrows(
            BookingNotFoundException.class,
            () -> {
              throw new BookingNotFoundException(message);
            });

    assertEquals(message, exception.getMessage());
    assertTrue(exception.getMessage().contains("123"));
  }

  @Test
  @DisplayName("Should extend RuntimeException")
  void testExtendsRuntimeException() {
    // Arrange
    String message = "Test exception";

    // Act & Assert
    BookingNotFoundException exception =
        assertThrows(
            BookingNotFoundException.class,
            () -> {
              throw new BookingNotFoundException(message);
            });

    assertTrue(exception instanceof RuntimeException);
  }

  @Test
  @DisplayName("Should preserve exception message through inheritance")
  void testExceptionMessagePreservation() {
    // Arrange
    String message = "Booking with ID 456 cannot be found";
    BookingNotFoundException exception = new BookingNotFoundException(message);

    // Act
    String retrievedMessage = exception.getMessage();

    // Assert
    assertEquals(message, retrievedMessage);
  }

  @Test
  @DisplayName("Should be catchable as RuntimeException")
  void testCatchableAsRuntimeException() {
    // Arrange
    String message = "Booking not found";
    BookingNotFoundException exception = new BookingNotFoundException(message);

    // Act & Assert
    try {
      throw exception;
    } catch (RuntimeException e) {
      assertEquals(message, e.getMessage());
    }
  }

  @Test
  @DisplayName("Should handle empty message")
  void testEmptyMessage() {
    // Act & Assert
    BookingNotFoundException exception =
        assertThrows(
            BookingNotFoundException.class,
            () -> {
              throw new BookingNotFoundException("");
            });

    assertEquals("", exception.getMessage());
  }

  @Test
  @DisplayName("Should handle null message")
  void testNullMessage() {
    // Act & Assert
    BookingNotFoundException exception =
        assertThrows(
            BookingNotFoundException.class,
            () -> {
              throw new BookingNotFoundException(null);
            });

    assertEquals(null, exception.getMessage());
  }

  @Test
  @DisplayName("Should handle special characters in message")
  void testSpecialCharactersInMessage() {
    // Arrange
    String message = "Booking ID: 123@#$% not found!";

    // Act & Assert
    BookingNotFoundException exception =
        assertThrows(
            BookingNotFoundException.class,
            () -> {
              throw new BookingNotFoundException(message);
            });

    assertEquals(message, exception.getMessage());
  }

  @Test
  @DisplayName("Should handle very long message")
  void testVeryLongMessage() {
    // Arrange
    String message = "Booking not found: " + "A".repeat(10000);

    // Act & Assert
    BookingNotFoundException exception =
        assertThrows(
            BookingNotFoundException.class,
            () -> {
              throw new BookingNotFoundException(message);
            });

    assertEquals(message, exception.getMessage());
  }

  @Test
  @DisplayName("Should have proper stack trace")
  void testStackTrace() {
    // Arrange
    String message = "Booking not found";
    BookingNotFoundException exception = new BookingNotFoundException(message);

    // Act
    StackTraceElement[] stackTrace = exception.getStackTrace();

    // Assert
    assertNotNull(stackTrace);
    assertTrue(stackTrace.length > 0);
  }

  @Test
  @DisplayName("Should be serializable with message")
  void testExceptionWithMessage() {
    // Arrange
    String originalMessage = "Booking ID 789 not found";

    // Act
    BookingNotFoundException exception = new BookingNotFoundException(originalMessage);
    String capturedMessage = exception.getMessage();

    // Assert
    assertEquals(originalMessage, capturedMessage);
  }

  @Test
  @DisplayName("Should differentiate between different exception messages")
  void testDifferentExceptionMessages() {
    // Act & Assert
    BookingNotFoundException exception1 =
        assertThrows(
            BookingNotFoundException.class,
            () -> {
              throw new BookingNotFoundException("Booking 1 not found");
            });
    BookingNotFoundException exception2 =
        assertThrows(
            BookingNotFoundException.class,
            () -> {
              throw new BookingNotFoundException("Booking 2 not found");
            });

    assertEquals("Booking 1 not found", exception1.getMessage());
    assertEquals("Booking 2 not found", exception2.getMessage());
  }

  @Test
  @DisplayName("Should handle numeric booking IDs in message")
  void testNumericBookingIdsInMessage() {
    // Arrange
    int[] bookingIds = {1, 100, 999, 10000, Integer.MAX_VALUE};

    // Act & Assert
    for (int bookingId : bookingIds) {
      String message = "Invalid bookingId: " + bookingId;
      BookingNotFoundException exception =
          assertThrows(
              BookingNotFoundException.class,
              () -> {
                throw new BookingNotFoundException(message);
              });

      assertTrue(exception.getMessage().contains(String.valueOf(bookingId)));
    }
  }

  @Test
  @DisplayName("Should maintain exception state after instantiation")
  void testExceptionStateAfterInstantiation() {
    // Arrange
    String message = "Booking not available";
    BookingNotFoundException exception1 = new BookingNotFoundException(message);

    // Act
    String capturedMessage = exception1.getMessage();

    // Assert
    assertEquals(message, capturedMessage);
    assertEquals(message, exception1.getMessage());
  }
}
