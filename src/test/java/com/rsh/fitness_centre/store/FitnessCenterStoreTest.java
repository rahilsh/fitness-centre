package com.rsh.fitness_centre.store;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.rsh.fitness_centre.entity.FitnessCentre;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("FitnessCenterStore Tests")
class FitnessCenterStoreTest {

  private FitnessCenterStore fitnessCenterStore;

  @BeforeEach
  void setUp() {
    fitnessCenterStore = new FitnessCenterStore();
  }

  @Test
  @DisplayName("Should add fitness centre successfully")
  void testAddFitnessCentreSuccess() {
    // Arrange
    FitnessCentre centre = new FitnessCentre(1, "Gold's Gym");

    // Act
    fitnessCenterStore.addFitnessCentre(centre);
    FitnessCentre retrievedCentre = fitnessCenterStore.getFitnessCentre("Gold's Gym");

    // Assert
    assertNotNull(retrievedCentre);
    assertEquals(1, retrievedCentre.getId());
    assertEquals("Gold's Gym", retrievedCentre.getName());
  }

  @Test
  @DisplayName("Should add multiple fitness centres successfully")
  void testAddMultipleFitnessCentresSuccess() {
    // Arrange
    FitnessCentre centre1 = new FitnessCentre(1, "Gold's Gym");
    FitnessCentre centre2 = new FitnessCentre(2, "Fitness Plus");
    FitnessCentre centre3 = new FitnessCentre(3, "Power House");

    // Act
    fitnessCenterStore.addFitnessCentre(centre1);
    fitnessCenterStore.addFitnessCentre(centre2);
    fitnessCenterStore.addFitnessCentre(centre3);
    Set<FitnessCentre> centres = fitnessCenterStore.getFitnessCentres();

    // Assert
    assertEquals(3, centres.size());
    assertTrue(centres.contains(centre1));
    assertTrue(centres.contains(centre2));
    assertTrue(centres.contains(centre3));
  }

  @Test
  @DisplayName("Should get fitness centre by name successfully")
  void testGetFitnessCentreByNameSuccess() {
    // Arrange
    FitnessCentre centre = new FitnessCentre(5, "Fitness Hub");
    fitnessCenterStore.addFitnessCentre(centre);

    // Act
    FitnessCentre retrievedCentre = fitnessCenterStore.getFitnessCentre("Fitness Hub");

    // Assert
    assertNotNull(retrievedCentre);
    assertEquals(5, retrievedCentre.getId());
    assertEquals("Fitness Hub", retrievedCentre.getName());
  }

  @Test
  @DisplayName("Should return null when fitness centre not found")
  void testGetFitnessCentreNotFound() {
    // Act
    FitnessCentre retrievedCentre = fitnessCenterStore.getFitnessCentre("Non-existent Gym");

    // Assert
    assertEquals(null, retrievedCentre);
  }

  @Test
  @DisplayName("Should get all fitness centres successfully")
  void testGetAllFitnessCentresSuccess() {
    // Arrange
    FitnessCentre centre1 = new FitnessCentre(1, "Gym A");
    FitnessCentre centre2 = new FitnessCentre(2, "Gym B");
    FitnessCentre centre3 = new FitnessCentre(3, "Gym C");

    // Act
    fitnessCenterStore.addFitnessCentre(centre1);
    fitnessCenterStore.addFitnessCentre(centre2);
    fitnessCenterStore.addFitnessCentre(centre3);
    Set<FitnessCentre> centres = fitnessCenterStore.getFitnessCentres();

    // Assert
    assertNotNull(centres);
    assertEquals(3, centres.size());
  }

  @Test
  @DisplayName("Should return empty set when no fitness centres exist")
  void testGetAllFitnessCentresEmpty() {
    // Act
    Set<FitnessCentre> centres = fitnessCenterStore.getFitnessCentres();

    // Assert
    assertNotNull(centres);
    assertEquals(0, centres.size());
  }

  @Test
  @DisplayName("Should allow duplicate fitness centre IDs (HashMap overwrites)")
  void testAddDuplicateFitnessCentreId() {
    // Arrange
    FitnessCentre centre1 = new FitnessCentre(1, "Gym A");
    FitnessCentre centre2 = new FitnessCentre(1, "Gym B");

    // Act
    fitnessCenterStore.addFitnessCentre(centre1);
    fitnessCenterStore.addFitnessCentre(centre2);
    Set<FitnessCentre> centres = fitnessCenterStore.getFitnessCentres();

    // Assert - Both are in getFitnessCentres because they have different names
    assertEquals(2, centres.size());
  }

  @Test
  @DisplayName("Should allow duplicate fitness centre names with different IDs")
  void testAddDuplicateFitnessCentreName() {
    // Arrange
    FitnessCentre centre1 = new FitnessCentre(1, "Gold's Gym");
    FitnessCentre centre2 = new FitnessCentre(2, "Gold's Gym");

    // Act
    fitnessCenterStore.addFitnessCentre(centre1);
    fitnessCenterStore.addFitnessCentre(centre2);

    // The second add will overwrite the first in the name index
    FitnessCentre retrievedCentre = fitnessCenterStore.getFitnessCentre("Gold's Gym");

    // Assert
    assertEquals(2, retrievedCentre.getId());
  }

  @Test
  @DisplayName("Should handle fitness centre with special characters in name")
  void testAddFitnessCentreWithSpecialCharacters() {
    // Arrange
    FitnessCentre centre = new FitnessCentre(7, "Gym@#$%Fitness");

    // Act
    fitnessCenterStore.addFitnessCentre(centre);
    FitnessCentre retrievedCentre = fitnessCenterStore.getFitnessCentre("Gym@#$%Fitness");

    // Assert
    assertNotNull(retrievedCentre);
    assertEquals("Gym@#$%Fitness", retrievedCentre.getName());
  }

  @Test
  @DisplayName("Should handle fitness centre with very long name")
  void testAddFitnessCentreWithVeryLongName() {
    // Arrange
    String longName = "A".repeat(10000);
    FitnessCentre centre = new FitnessCentre(8, longName);

    // Act
    fitnessCenterStore.addFitnessCentre(centre);
    FitnessCentre retrievedCentre = fitnessCenterStore.getFitnessCentre(longName);

    // Assert
    assertNotNull(retrievedCentre);
    assertEquals(longName, retrievedCentre.getName());
  }

  @Test
  @DisplayName("Should handle large number of fitness centres")
  void testAddLargeNumberOfFitnessCentres() {
    // Arrange & Act
    for (int i = 1; i <= 1000; i++) {
      fitnessCenterStore.addFitnessCentre(new FitnessCentre(i, "Gym" + i));
    }
    Set<FitnessCentre> centres = fitnessCenterStore.getFitnessCentres();

    // Assert
    assertEquals(1000, centres.size());
  }

  @Test
  @DisplayName("Should maintain centre integrity after multiple operations")
  void testCentreIntegrityAfterMultipleOperations() {
    // Arrange & Act
    FitnessCentre centre1 = new FitnessCentre(1, "Gym1");
    FitnessCentre centre2 = new FitnessCentre(2, "Gym2");
    FitnessCentre centre3 = new FitnessCentre(3, "Gym3");

    fitnessCenterStore.addFitnessCentre(centre1);
    FitnessCentre firstCheck = fitnessCenterStore.getFitnessCentre("Gym1");
    assertNotNull(firstCheck);

    fitnessCenterStore.addFitnessCentre(centre2);
    FitnessCentre secondCheck = fitnessCenterStore.getFitnessCentre("Gym2");
    assertNotNull(secondCheck);

    fitnessCenterStore.addFitnessCentre(centre3);
    Set<FitnessCentre> finalSet = fitnessCenterStore.getFitnessCentres();

    // Assert
    assertEquals(3, finalSet.size());
    assertTrue(finalSet.contains(centre1));
    assertTrue(finalSet.contains(centre2));
    assertTrue(finalSet.contains(centre3));
  }

  @Test
  @DisplayName("Should handle concurrent additions")
  void testConcurrentAdditions() throws InterruptedException {
    // Arrange
    Thread thread1 = new Thread(() -> {
      for (int i = 1; i <= 10; i++) {
        fitnessCenterStore.addFitnessCentre(new FitnessCentre(i, "Gym" + i));
      }
    });

    Thread thread2 = new Thread(() -> {
      for (int i = 11; i <= 20; i++) {
        fitnessCenterStore.addFitnessCentre(new FitnessCentre(i, "Gym" + i));
      }
    });

    // Act
    thread1.start();
    thread2.start();
    thread1.join();
    thread2.join();

    Set<FitnessCentre> centres = fitnessCenterStore.getFitnessCentres();

    // Assert
    assertTrue(centres.size() >= 10);  // At least 10 centres should be added
  }

  @Test
  @DisplayName("Should verify fitness centre equality based on name")
  void testFitnessCentreEquality() {
    // Arrange
    FitnessCentre centre1 = new FitnessCentre(1, "Gold's Gym");
    FitnessCentre centre2 = new FitnessCentre(1, "Gold's Gym");

    fitnessCenterStore.addFitnessCentre(centre1);
    Set<FitnessCentre> centres = fitnessCenterStore.getFitnessCentres();

    // Assert
    assertTrue(centres.contains(centre2));
  }

  @Test
  @DisplayName("Should handle empty fitness centre name")
  void testAddFitnessCentreWithEmptyName() {
    // Arrange
    FitnessCentre centre = new FitnessCentre(10, "");

    // Act
    fitnessCenterStore.addFitnessCentre(centre);
    FitnessCentre retrievedCentre = fitnessCenterStore.getFitnessCentre("");

    // Assert
    assertNotNull(retrievedCentre);
    assertEquals(10, retrievedCentre.getId());
  }
}
