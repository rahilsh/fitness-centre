package com.rsh.fitness_centre.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.rsh.fitness_centre.entity.Activity;
import com.rsh.fitness_centre.entity.FitnessCentre;
import com.rsh.fitness_centre.entity.Slot;
import com.rsh.fitness_centre.store.FitnessCenterSlotStore;
import com.rsh.fitness_centre.store.FitnessCenterStore;
import com.rsh.fitness_centre.util.SequenceGenerator;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("FitnessCentreService Tests")
class FitnessCentreServiceTest {

  private FitnessCentreService fitnessCentreService;

  @Mock
  private SequenceGenerator sequenceGenerator;

  @Mock
  private FitnessCenterStore fitnessCenterStore;

  @Mock
  private FitnessCenterSlotStore fitnessCenterSlotStore;

  @BeforeEach
  void setUp() {
    fitnessCentreService =
        new FitnessCentreService(sequenceGenerator, fitnessCenterStore, fitnessCenterSlotStore);
  }

  @Test
  @DisplayName("Should add fitness centre successfully")
  void testAddCentreSuccess() {
    // Arrange
    String centreName = "Gold's Gym";
    int centreId = 1;
    Set<List<Integer>> timings = new HashSet<>();
    Set<Activity> activities = new HashSet<>();

    when(sequenceGenerator.getNext("FitnessCentre")).thenReturn(centreId);

    // Act
    FitnessCentre result = fitnessCentreService.addCentre(centreName, timings, activities);

    // Assert
    assertNotNull(result);
    assertEquals(centreId, result.getId());
    assertEquals(centreName, result.getName());
    verify(sequenceGenerator, times(1)).getNext("FitnessCentre");
    verify(fitnessCenterStore, times(1)).addFitnessCentre(result);
  }

  @Test
  @DisplayName("Should get centre by name successfully")
  void testGetCentreByNameSuccess() {
    // Arrange
    String centreName = "Gym A";
    FitnessCentre centre = new FitnessCentre(1, centreName);
    when(fitnessCenterStore.getFitnessCentre(centreName)).thenReturn(centre);

    // Act
    FitnessCentre result = fitnessCentreService.getCentreByName(centreName);

    // Assert
    assertNotNull(result);
    assertEquals(centreName, result.getName());
    verify(fitnessCenterStore, times(1)).getFitnessCentre(centreName);
  }

  @Test
  @DisplayName("Should return null when centre not found by name")
  void testGetCentreByNameNotFound() {
    // Arrange
    String centreName = "Non-existent Gym";
    when(fitnessCenterStore.getFitnessCentre(centreName)).thenReturn(null);

    // Act
    FitnessCentre result = fitnessCentreService.getCentreByName(centreName);

    // Assert
    assertEquals(null, result);
    verify(fitnessCenterStore, times(1)).getFitnessCentre(centreName);
  }

  @Test
  @DisplayName("Should get slots of a day successfully")
  void testGetSlotsOfADaySuccess() {
    // Arrange
    int centreId = 1;
    LocalDate date = LocalDate.now();
    Set<Slot> slots = new HashSet<>();
    slots.add(
        new Slot(1, date, Activity.YOGA, 9, 10, 10, centreId));
    slots.add(
        new Slot(2, date, Activity.CARDIO, 10, 11, 20, centreId));
    when(fitnessCenterSlotStore.getSlotsForADay(centreId, date)).thenReturn(slots);

    // Act
    Set<Slot> result = fitnessCentreService.getSlotsOfADay(centreId, date);

    // Assert
    assertNotNull(result);
    assertEquals(2, result.size());
    verify(fitnessCenterSlotStore, times(1)).getSlotsForADay(centreId, date);
  }

  @Test
  @DisplayName("Should return empty set when no slots for a day")
  void testGetSlotsOfADayEmpty() {
    // Arrange
    int centreId = 1;
    LocalDate date = LocalDate.now();
    when(fitnessCenterSlotStore.getSlotsForADay(centreId, date)).thenReturn(new HashSet<>());

    // Act
    Set<Slot> result = fitnessCentreService.getSlotsOfADay(centreId, date);

    // Assert
    assertNotNull(result);
    assertEquals(0, result.size());
    verify(fitnessCenterSlotStore, times(1)).getSlotsForADay(centreId, date);
  }

  @Test
  @DisplayName("Should add activity successfully")
  void testAddActivitySuccess() {
    // Arrange
    int fitnessCentreId = 1;
    Activity activity = Activity.WEIGHTS;
    int startTime = 8;
    int endTime = 9;
    int noOfSlots = 15;
    int slotId = 1;

    when(sequenceGenerator.getNext("FitnessCentreSlot")).thenReturn(slotId);

    // Act
    Slot result = fitnessCentreService.addActivity(fitnessCentreId, activity, startTime, endTime, noOfSlots);

    // Assert
    assertNotNull(result);
    assertEquals(slotId, result.getId());
    assertEquals(activity, result.getActivity());
    assertEquals(startTime, result.getStartTime());
    assertEquals(endTime, result.getEndTime());
    assertEquals(noOfSlots, result.getNoOfSeats());
    assertEquals(fitnessCentreId, result.getFitnessCenterId());
    verify(sequenceGenerator, times(1)).getNext("FitnessCentreSlot");
    verify(fitnessCenterSlotStore, times(1)).addDataSlot(result);
  }

  @Test
  @DisplayName("Should get all centres successfully")
  void testGetAllCentresSuccess() {
    // Arrange
    Set<FitnessCentre> centres = new HashSet<>();
    centres.add(new FitnessCentre(1, "Gym A"));
    centres.add(new FitnessCentre(2, "Gym B"));
    when(fitnessCenterStore.getFitnessCentres()).thenReturn(centres);

    // Act
    Set<FitnessCentre> result = fitnessCentreService.getAllCentres();

    // Assert
    assertNotNull(result);
    assertEquals(2, result.size());
    verify(fitnessCenterStore, times(1)).getFitnessCentres();
  }

  @Test
  @DisplayName("Should return empty set when no centres exist")
  void testGetAllCentresEmpty() {
    // Arrange
    when(fitnessCenterStore.getFitnessCentres()).thenReturn(new HashSet<>());

    // Act
    Set<FitnessCentre> result = fitnessCentreService.getAllCentres();

    // Assert
    assertNotNull(result);
    assertEquals(0, result.size());
    verify(fitnessCenterStore, times(1)).getFitnessCentres();
  }

  @Test
  @DisplayName("Should get centre activities successfully")
  void testGetCentreActivitiesSuccess() {
    // Arrange
    int centreId = 1;
    Set<Slot> slots = new HashSet<>();
    slots.add(
        new Slot(1, LocalDate.now(), Activity.YOGA, 9, 10, 10, centreId));
    slots.add(
        new Slot(2, LocalDate.now(), Activity.WEIGHTS, 10, 11, 20, centreId));
    slots.add(
        new Slot(3, LocalDate.now(), Activity.CARDIO, 11, 12, 15, centreId));
    when(fitnessCenterSlotStore.getSlotsForCentre(centreId)).thenReturn(slots);

    // Act
    Set<Slot> result = fitnessCentreService.getCentreActivities(centreId);

    // Assert
    assertNotNull(result);
    assertEquals(3, result.size());
    verify(fitnessCenterSlotStore, times(1)).getSlotsForCentre(centreId);
  }

  @Test
  @DisplayName("Should return empty set when centre has no activities")
  void testGetCentreActivitiesEmpty() {
    // Arrange
    int centreId = 999;
    when(fitnessCenterSlotStore.getSlotsForCentre(centreId)).thenReturn(new HashSet<>());

    // Act
    Set<Slot> result = fitnessCentreService.getCentreActivities(centreId);

    // Assert
    assertNotNull(result);
    assertEquals(0, result.size());
    verify(fitnessCenterSlotStore, times(1)).getSlotsForCentre(centreId);
  }

  @Test
  @DisplayName("Should add activity with all workout types")
  void testAddActivityWithDifferentTypes() {
    // Arrange
    int fitnessCentreId = 1;
    when(sequenceGenerator.getNext("FitnessCentreSlot")).thenReturn(1).thenReturn(2).thenReturn(3).thenReturn(4);

    // Act
    Slot yogaSlot = fitnessCentreService.addActivity(fitnessCentreId, Activity.YOGA, 9, 10, 10);
    Slot cardioSlot = fitnessCentreService.addActivity(fitnessCentreId, Activity.CARDIO, 10, 11, 20);
    Slot weightsSlot = fitnessCentreService.addActivity(fitnessCentreId, Activity.WEIGHTS, 8, 9, 15);
    Slot swimmingSlot = fitnessCentreService.addActivity(fitnessCentreId, Activity.SWIMMING, 14, 15, 25);

    // Assert
    assertEquals(Activity.YOGA, yogaSlot.getActivity());
    assertEquals(Activity.CARDIO, cardioSlot.getActivity());
    assertEquals(Activity.WEIGHTS, weightsSlot.getActivity());
    assertEquals(Activity.SWIMMING, swimmingSlot.getActivity());
  }

  @Test
  @DisplayName("Should handle centre name with special characters")
  void testAddCentreWithSpecialCharacters() {
    // Arrange
    String centreName = "Gym@#$%Fitness";
    int centreId = 5;
    when(sequenceGenerator.getNext("FitnessCentre")).thenReturn(centreId);

    // Act
    FitnessCentre result = fitnessCentreService.addCentre(centreName, new HashSet<>(), new HashSet<>());

    // Assert
    assertEquals(centreName, result.getName());
  }
}
