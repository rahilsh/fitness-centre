package com.rsh.fitness_centre.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("SequenceGenerator Tests")
class SequenceGeneratorTest {

  private SequenceGenerator sequenceGenerator;

  @BeforeEach
  void setUp() {
    sequenceGenerator = new SequenceGenerator();
  }

  @Test
  @DisplayName("Should generate first sequence starting from 1")
  void testGenerateFirstSequence() {
    // Act
    int result = sequenceGenerator.getNext("User");

    // Assert
    assertEquals(1, result);
  }

  @Test
  @DisplayName("Should generate sequential numbers")
  void testGenerateSequentialNumbers() {
    // Act
    int first = sequenceGenerator.getNext("User");
    int second = sequenceGenerator.getNext("User");
    int third = sequenceGenerator.getNext("User");

    // Assert
    assertEquals(1, first);
    assertEquals(2, second);
    assertEquals(3, third);
  }

  @Test
  @DisplayName("Should maintain separate sequences for different keys")
  void testSeparateSequencesForDifferentKeys() {
    // Act
    int userSeq1 = sequenceGenerator.getNext("User");
    int bookingSeq1 = sequenceGenerator.getNext("Booking");
    int userSeq2 = sequenceGenerator.getNext("User");
    int bookingSeq2 = sequenceGenerator.getNext("Booking");

    // Assert
    assertEquals(1, userSeq1);
    assertEquals(1, bookingSeq1);
    assertEquals(2, userSeq2);
    assertEquals(2, bookingSeq2);
  }

  @Test
  @DisplayName("Should generate unique IDs for multiple keys")
  void testUniqueIdsForMultipleKeys() {
    // Act
    int user1 = sequenceGenerator.getNext("User");
    int booking1 = sequenceGenerator.getNext("Booking");
    int centre1 = sequenceGenerator.getNext("FitnessCentre");
    int slot1 = sequenceGenerator.getNext("FitnessCentreSlot");

    int user2 = sequenceGenerator.getNext("User");
    int booking2 = sequenceGenerator.getNext("Booking");
    int centre2 = sequenceGenerator.getNext("FitnessCentre");
    int slot2 = sequenceGenerator.getNext("FitnessCentreSlot");

    // Assert
    assertEquals(1, user1);
    assertEquals(1, booking1);
    assertEquals(1, centre1);
    assertEquals(1, slot1);
    assertEquals(2, user2);
    assertEquals(2, booking2);
    assertEquals(2, centre2);
    assertEquals(2, slot2);
  }

  @Test
  @DisplayName("Should handle case-sensitive keys")
  void testCaseSensitiveKeys() {
    // Act
    int lowercase = sequenceGenerator.getNext("user");
    int uppercase = sequenceGenerator.getNext("User");
    int mixedCase = sequenceGenerator.getNext("USER");

    // Assert
    assertEquals(1, lowercase);
    assertEquals(1, uppercase);
    assertEquals(1, mixedCase);
  }

  @Test
  @DisplayName("Should generate 1000 sequential numbers correctly")
  void testGenerateThousandSequentialNumbers() {
    // Act
    int first = sequenceGenerator.getNext("LargeSeq");
    for (int i = 0; i < 998; i++) {
      sequenceGenerator.getNext("LargeSeq");
    }
    int last = sequenceGenerator.getNext("LargeSeq");

    // Assert
    assertEquals(1, first);
    assertEquals(1000, last);
  }

  @Test
  @DisplayName("Should not reset sequence on repeated calls")
  void testSequenceDoesNotReset() {
    // Act
    int first = sequenceGenerator.getNext("Counter");
    int second = sequenceGenerator.getNext("Counter");
    int third = sequenceGenerator.getNext("Counter");
    int fourth = sequenceGenerator.getNext("Counter");

    // Assert
    assertNotEquals(first, second);
    assertNotEquals(second, third);
    assertNotEquals(third, fourth);
    assertEquals(1, first);
    assertEquals(2, second);
    assertEquals(3, third);
    assertEquals(4, fourth);
  }

  @Test
  @DisplayName("Should handle special character keys")
  void testSpecialCharacterKeys() {
    // Act
    int result1 = sequenceGenerator.getNext("User@#$%");
    int result2 = sequenceGenerator.getNext("User@#$%");

    // Assert
    assertEquals(1, result1);
    assertEquals(2, result2);
  }

  @Test
  @DisplayName("Should handle empty string key")
  void testEmptyStringKey() {
    // Act
    int result1 = sequenceGenerator.getNext("");
    int result2 = sequenceGenerator.getNext("");

    // Assert
    assertEquals(1, result1);
    assertEquals(2, result2);
  }

  @Test
  @DisplayName("Should handle concurrent sequence generation")
  void testConcurrentSequenceGeneration() throws InterruptedException {
    // Arrange
    int[] userIds = new int[100];
    int[] bookingIds = new int[100];

    Thread thread1 = new Thread(() -> {
      for (int i = 0; i < 50; i++) {
        userIds[i] = sequenceGenerator.getNext("ConcurrentUser");
      }
    });

    Thread thread2 = new Thread(() -> {
      for (int i = 50; i < 100; i++) {
        userIds[i] = sequenceGenerator.getNext("ConcurrentUser");
      }
    });

    Thread thread3 = new Thread(() -> {
      for (int i = 0; i < 50; i++) {
        bookingIds[i] = sequenceGenerator.getNext("ConcurrentBooking");
      }
    });

    Thread thread4 = new Thread(() -> {
      for (int i = 50; i < 100; i++) {
        bookingIds[i] = sequenceGenerator.getNext("ConcurrentBooking");
      }
    });

    // Act
    thread1.start();
    thread2.start();
    thread3.start();
    thread4.start();

    thread1.join();
    thread2.join();
    thread3.join();
    thread4.join();

    // Assert - All IDs should be unique for each key
    for (int i = 0; i < 100; i++) {
      for (int j = i + 1; j < 100; j++) {
        assertNotEquals(userIds[i], userIds[j]);
        assertNotEquals(bookingIds[i], bookingIds[j]);
      }
    }
  }

  @Test
  @DisplayName("Should always return positive integers")
  void testAlwaysReturnsPositiveIntegers() {
    // Act
    for (int i = 0; i < 100; i++) {
      int result = sequenceGenerator.getNext("PositiveTest");
      // Assert
      assertEquals(i + 1, result);
    }
  }

  @Test
  @DisplayName("Should handle long string keys")
  void testLongStringKeys() {
    // Arrange
    String longKey = "A".repeat(10000);

    // Act
    int result1 = sequenceGenerator.getNext(longKey);
    int result2 = sequenceGenerator.getNext(longKey);

    // Assert
    assertEquals(1, result1);
    assertEquals(2, result2);
  }

  @Test
  @DisplayName("Should maintain state across multiple instances")
  void testStateIndependenceAcrossInstances() {
    // Arrange
    SequenceGenerator gen1 = new SequenceGenerator();
    SequenceGenerator gen2 = new SequenceGenerator();

    // Act
    int gen1Result = gen1.getNext("Test");
    int gen2Result = gen2.getNext("Test");

    // Assert - Each instance maintains its own state
    assertEquals(1, gen1Result);
    assertEquals(1, gen2Result);
  }

  @Test
  @DisplayName("Should increment correctly after reaching 100")
  void testIncrementAfterHundred() {
    // Arrange & Act
    for (int i = 0; i < 100; i++) {
      sequenceGenerator.getNext("HundredTest");
    }
    int afterHundred = sequenceGenerator.getNext("HundredTest");

    // Assert
    assertEquals(101, afterHundred);
  }
}
