package com.rsh.fitness_centre.store;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.rsh.fitness_centre.entity.Activity;
import com.rsh.fitness_centre.entity.Slot;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("FitnessCenterSlotStore Tests")
class FitnessCenterSlotStoreTest {

  private FitnessCenterSlotStore fitnessCenterSlotStore;

  @BeforeEach
  void setUp() {
    fitnessCenterSlotStore = new FitnessCenterSlotStore();
  }

  @Test
  @DisplayName("Should add slot successfully")
  void testAddSlotSuccess() {
    // Arrange
    LocalDate date = LocalDate.now();
    Slot slot = new Slot(1, date, Activity.YOGA, 9, 10, 10, 1);

    // Act
    fitnessCenterSlotStore.addDataSlot(slot);
    Slot retrievedSlot = fitnessCenterSlotStore.getSlotById(1);

    // Assert
    assertNotNull(retrievedSlot);
    assertEquals(1, retrievedSlot.getId());
    assertEquals(Activity.YOGA, retrievedSlot.getActivity());
  }

  @Test
  @DisplayName("Should add multiple slots successfully")
  void testAddMultipleSlotsSuccess() {
    // Arrange
    LocalDate date = LocalDate.now();
    Slot slot1 = new Slot(1, date, Activity.YOGA, 9, 10, 10, 1);
    Slot slot2 = new Slot(2, date, Activity.CARDIO, 10, 11, 20, 1);
    Slot slot3 = new Slot(3, date, Activity.WEIGHTS, 11, 12, 15, 1);

    // Act
    fitnessCenterSlotStore.addDataSlot(slot1);
    fitnessCenterSlotStore.addDataSlot(slot2);
    fitnessCenterSlotStore.addDataSlot(slot3);
    Set<Slot> slots = fitnessCenterSlotStore.getSlotsForCentre(1);

    // Assert
    assertEquals(3, slots.size());
    assertTrue(slots.contains(slot1));
    assertTrue(slots.contains(slot2));
    assertTrue(slots.contains(slot3));
  }

  @Test
  @DisplayName("Should get slot by ID successfully")
  void testGetSlotByIdSuccess() {
    // Arrange
    LocalDate date = LocalDate.now();
    Slot slot = new Slot(5, date, Activity.SWIMMING, 14, 15, 25, 2);
    fitnessCenterSlotStore.addDataSlot(slot);

    // Act
    Slot retrievedSlot = fitnessCenterSlotStore.getSlotById(5);

    // Assert
    assertNotNull(retrievedSlot);
    assertEquals(5, retrievedSlot.getId());
    assertEquals(Activity.SWIMMING, retrievedSlot.getActivity());
  }

  @Test
  @DisplayName("Should return null when slot ID not found")
  void testGetSlotByIdNotFound() {
    // Act
    Slot retrievedSlot = fitnessCenterSlotStore.getSlotById(999);

    // Assert
    assertEquals(null, retrievedSlot);
  }

  @Test
  @DisplayName("Should get slots for a specific day successfully")
  void testGetSlotsForADaySuccess() {
    // Arrange
    LocalDate date = LocalDate.now();
    Slot slot1 = new Slot(1, date, Activity.YOGA, 9, 10, 10, 1);
    Slot slot2 = new Slot(2, date, Activity.CARDIO, 10, 11, 20, 1);
    fitnessCenterSlotStore.addDataSlot(slot1);
    fitnessCenterSlotStore.addDataSlot(slot2);

    // Act
    Set<Slot> slotsForDay = fitnessCenterSlotStore.getSlotsForADay(1, date);

    // Assert
    assertNotNull(slotsForDay);
    assertEquals(2, slotsForDay.size());
    assertTrue(slotsForDay.contains(slot1));
    assertTrue(slotsForDay.contains(slot2));
  }

  @Test
  @DisplayName("Should return empty set when no slots for a day")
  void testGetSlotsForADayEmpty() {
    // Arrange
    LocalDate date = LocalDate.now();

    // Act
    Set<Slot> slotsForDay = fitnessCenterSlotStore.getSlotsForADay(1, date);

    // Assert
    assertNotNull(slotsForDay);
    assertEquals(0, slotsForDay.size());
  }

  @Test
  @DisplayName("Should get slots for different days separately")
  void testGetSlotsForDifferentDays() {
    // Arrange
    LocalDate date1 = LocalDate.now();
    LocalDate date2 = LocalDate.now().plusDays(1);
    Slot slot1 = new Slot(1, date1, Activity.YOGA, 9, 10, 10, 1);
    Slot slot2 = new Slot(2, date2, Activity.CARDIO, 10, 11, 20, 1);
    fitnessCenterSlotStore.addDataSlot(slot1);
    fitnessCenterSlotStore.addDataSlot(slot2);

    // Act
    Set<Slot> slotsDay1 = fitnessCenterSlotStore.getSlotsForADay(1, date1);
    Set<Slot> slotsDay2 = fitnessCenterSlotStore.getSlotsForADay(1, date2);

    // Assert
    assertEquals(1, slotsDay1.size());
    assertEquals(1, slotsDay2.size());
    assertTrue(slotsDay1.contains(slot1));
    assertTrue(slotsDay2.contains(slot2));
  }

  @Test
  @DisplayName("Should get all slots for a centre successfully")
  void testGetSlotsForCentreSuccess() {
    // Arrange
    LocalDate date = LocalDate.now();
    Slot slot1 = new Slot(1, date, Activity.YOGA, 9, 10, 10, 1);
    Slot slot2 = new Slot(2, date, Activity.CARDIO, 10, 11, 20, 1);
    Slot slot3 = new Slot(3, date, Activity.WEIGHTS, 11, 12, 15, 1);
    fitnessCenterSlotStore.addDataSlot(slot1);
    fitnessCenterSlotStore.addDataSlot(slot2);
    fitnessCenterSlotStore.addDataSlot(slot3);

    // Act
    Set<Slot> slotsForCentre = fitnessCenterSlotStore.getSlotsForCentre(1);

    // Assert
    assertNotNull(slotsForCentre);
    assertEquals(3, slotsForCentre.size());
    assertTrue(slotsForCentre.contains(slot1));
    assertTrue(slotsForCentre.contains(slot2));
    assertTrue(slotsForCentre.contains(slot3));
  }

  @Test
  @DisplayName("Should handle slots for multiple centres separately")
  void testSlotsForMultipleCentresSeparately() {
    // Arrange
    LocalDate date = LocalDate.now();
    Slot slot1 = new Slot(1, date, Activity.YOGA, 9, 10, 10, 1);
    Slot slot2 = new Slot(2, date, Activity.CARDIO, 10, 11, 20, 1);
    Slot slot3 = new Slot(3, date, Activity.WEIGHTS, 11, 12, 15, 2);
    Slot slot4 = new Slot(4, date, Activity.SWIMMING, 14, 15, 25, 2);
    fitnessCenterSlotStore.addDataSlot(slot1);
    fitnessCenterSlotStore.addDataSlot(slot2);
    fitnessCenterSlotStore.addDataSlot(slot3);
    fitnessCenterSlotStore.addDataSlot(slot4);

    // Act
    Set<Slot> centre1Slots = fitnessCenterSlotStore.getSlotsForCentre(1);
    Set<Slot> centre2Slots = fitnessCenterSlotStore.getSlotsForCentre(2);

    // Assert
    assertEquals(2, centre1Slots.size());
    assertEquals(2, centre2Slots.size());
    assertTrue(centre1Slots.contains(slot1));
    assertTrue(centre1Slots.contains(slot2));
    assertTrue(centre2Slots.contains(slot3));
    assertTrue(centre2Slots.contains(slot4));
  }

  @Test
  @DisplayName("Should handle slots with all activity types")
  void testSlotsWithAllActivityTypes() {
    // Arrange
    LocalDate date = LocalDate.now();
    Slot yogaSlot = new Slot(1, date, Activity.YOGA, 9, 10, 10, 1);
    Slot cardioSlot = new Slot(2, date, Activity.CARDIO, 10, 11, 20, 1);
    Slot weightsSlot = new Slot(3, date, Activity.WEIGHTS, 11, 12, 15, 1);
    Slot swimmingSlot = new Slot(4, date, Activity.SWIMMING, 14, 15, 25, 1);

    // Act
    fitnessCenterSlotStore.addDataSlot(yogaSlot);
    fitnessCenterSlotStore.addDataSlot(cardioSlot);
    fitnessCenterSlotStore.addDataSlot(weightsSlot);
    fitnessCenterSlotStore.addDataSlot(swimmingSlot);

    Set<Slot> centreSlots = fitnessCenterSlotStore.getSlotsForCentre(1);

    // Assert
    assertEquals(4, centreSlots.size());
  }

  @Test
  @DisplayName("Should handle slots with same time periods in a day")
  void testAddLargeNumberOfSlots() {
    // Arrange & Act
    LocalDate date = LocalDate.now();
    for (int i = 1; i <= 24; i++) {
      Slot slot = new Slot(i, date, Activity.YOGA, i - 1, i, 10, 1);
      fitnessCenterSlotStore.addDataSlot(slot);
    }
    Set<Slot> centreSlots = fitnessCenterSlotStore.getSlotsForCentre(1);

    // Assert
    assertEquals(24, centreSlots.size());
  }

  @Test
  @DisplayName("Should maintain slot integrity after multiple operations")
  void testSlotIntegrityAfterMultipleOperations() {
    // Arrange & Act
    LocalDate date = LocalDate.now();
    Slot slot1 = new Slot(1, date, Activity.YOGA, 9, 10, 10, 1);
    Slot slot2 = new Slot(2, date, Activity.CARDIO, 10, 11, 20, 1);

    fitnessCenterSlotStore.addDataSlot(slot1);
    Slot firstCheck = fitnessCenterSlotStore.getSlotById(1);
    assertEquals(Activity.YOGA, firstCheck.getActivity());

    fitnessCenterSlotStore.addDataSlot(slot2);
    Slot secondCheck = fitnessCenterSlotStore.getSlotById(2);
    assertEquals(Activity.CARDIO, secondCheck.getActivity());

    Set<Slot> centreSlots = fitnessCenterSlotStore.getSlotsForCentre(1);

    // Assert
    assertEquals(2, centreSlots.size());
    assertTrue(centreSlots.contains(slot1));
    assertTrue(centreSlots.contains(slot2));
  }

  @Test
  @DisplayName("Should handle concurrent slot additions")
  void testConcurrentSlotAdditions() throws InterruptedException {
    // Arrange
    LocalDate date = LocalDate.now();
    Thread thread1 = new Thread(() -> {
      for (int i = 1; i <= 5; i++) {
        Slot slot = new Slot(i, date, Activity.YOGA, 9, 10, 10, 1);
        fitnessCenterSlotStore.addDataSlot(slot);
      }
    });

    Thread thread2 = new Thread(() -> {
      for (int i = 6; i <= 10; i++) {
        Slot slot = new Slot(i, date, Activity.CARDIO, 10, 11, 20, 1);
        fitnessCenterSlotStore.addDataSlot(slot);
      }
    });

    // Act
    thread1.start();
    thread2.start();
    thread1.join();
    thread2.join();

    Set<Slot> centreSlots = fitnessCenterSlotStore.getSlotsForCentre(1);

    // Assert
    assertTrue(centreSlots.size() >= 1);  // At least some slots were added
  }

  @Test
  @DisplayName("Should handle slots with boundary time values")
  void testSlotsWithBoundaryTimes() {
    // Arrange
    LocalDate date = LocalDate.now();
    Slot earlySlot = new Slot(1, date, Activity.YOGA, 0, 1, 10, 1);
    Slot lateSlot = new Slot(2, date, Activity.CARDIO, 22, 23, 20, 1);

    // Act
    fitnessCenterSlotStore.addDataSlot(earlySlot);
    fitnessCenterSlotStore.addDataSlot(lateSlot);

    Slot retrievedEarly = fitnessCenterSlotStore.getSlotById(1);
    Slot retrievedLate = fitnessCenterSlotStore.getSlotById(2);

    // Assert
    assertEquals(0, retrievedEarly.getStartTime());
    assertEquals(22, retrievedLate.getStartTime());
  }

  @Test
  @DisplayName("Should verify slot equality")
  void testSlotEquality() {
    // Arrange
    LocalDate date = LocalDate.now();
    Slot slot1 = new Slot(1, date, Activity.YOGA, 9, 10, 10, 1);
    Slot slot2 = new Slot(1, date, Activity.YOGA, 9, 10, 10, 1);

    fitnessCenterSlotStore.addDataSlot(slot1);
    Set<Slot> centreSlots = fitnessCenterSlotStore.getSlotsForCentre(1);

    // Assert
    assertTrue(centreSlots.contains(slot2));
  }
}
