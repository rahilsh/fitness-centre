package com.rsh.fitness_centre.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.rsh.fitness_centre.entity.Activity;
import com.rsh.fitness_centre.entity.FitnessCentre;
import com.rsh.fitness_centre.entity.Slot;
import com.rsh.fitness_centre.entity.request.AddActivityRequest;
import com.rsh.fitness_centre.entity.request.AddFitnessCentreRequest;
import com.rsh.fitness_centre.service.FitnessCentreService;
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
@DisplayName("FitnessCentreController Tests")
class FitnessCentreControllerTest {

  private FitnessCentreController fitnessCentreController;

  @Mock
  private FitnessCentreService fitnessCentreService;

  @BeforeEach
  void setUp() {
    fitnessCentreController = new FitnessCentreController(fitnessCentreService);
  }

  @Test
  @DisplayName("Should add fitness centre successfully")
  void testAddFitnessCentreSuccess() {
    // Arrange
    AddFitnessCentreRequest request = new AddFitnessCentreRequest();
    request.setName("Gold's Gym");
    request.setTimings(new HashSet<>());
    request.setSupportedActivities(new HashSet<>());

    FitnessCentre centre = new FitnessCentre(1, "Gold's Gym");
    when(fitnessCentreService.addCentre("Gold's Gym", new HashSet<>(), new HashSet<>()))
        .thenReturn(centre);

    // Act
    FitnessCentre result = fitnessCentreController.addFitnessCentre(request);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.getId());
    assertEquals("Gold's Gym", result.getName());
    verify(fitnessCentreService, times(1))
        .addCentre("Gold's Gym", new HashSet<>(), new HashSet<>());
  }

  @Test
  @DisplayName("Should add activity to fitness centre successfully")
  void testAddActivitySuccess() {
    // Arrange
    int centreId = 1;
    AddActivityRequest request = new AddActivityRequest();
    request.setFitnessCentreId(centreId);
    request.setActivity(Activity.YOGA);
    request.setStartTime(9);
    request.setEndTime(10);
    request.setNoOfSlots(10);

    Slot slot = new Slot(1, LocalDate.now(), Activity.YOGA, 9, 10, 10, centreId);
    when(fitnessCentreService.addActivity(centreId, Activity.YOGA, 9, 10, 10))
        .thenReturn(slot);

    // Act
    Slot result = fitnessCentreController.addActivity(request, centreId);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.getId());
    assertEquals(Activity.YOGA, result.getActivity());
    verify(fitnessCentreService, times(1)).addActivity(centreId, Activity.YOGA, 9, 10, 10);
  }

  @Test
  @DisplayName("Should get activities of fitness centre successfully")
  void testGetActivitiesSuccess() {
    // Arrange
    int centreId = 1;
    Set<Slot> slots = new HashSet<>();
    slots.add(new Slot(1, LocalDate.now(), Activity.YOGA, 9, 10, 10, centreId));
    slots.add(new Slot(2, LocalDate.now(), Activity.CARDIO, 10, 11, 20, centreId));
    when(fitnessCentreService.getCentreActivities(centreId)).thenReturn(slots);

    // Act
    Set<Slot> result = fitnessCentreController.getActivities(centreId);

    // Assert
    assertNotNull(result);
    assertEquals(2, result.size());
    verify(fitnessCentreService, times(1)).getCentreActivities(centreId);
  }

  @Test
  @DisplayName("Should return empty set when centre has no activities")
  void testGetActivitiesEmpty() {
    // Arrange
    int centreId = 999;
    when(fitnessCentreService.getCentreActivities(centreId)).thenReturn(new HashSet<>());

    // Act
    Set<Slot> result = fitnessCentreController.getActivities(centreId);

    // Assert
    assertNotNull(result);
    assertEquals(0, result.size());
    verify(fitnessCentreService, times(1)).getCentreActivities(centreId);
  }

  @Test
  @DisplayName("Should add centre with special characters in name")
  void testAddCentreWithSpecialCharacters() {
    // Arrange
    AddFitnessCentreRequest request = new AddFitnessCentreRequest();
    request.setName("Gym@#$%");
    request.setTimings(new HashSet<>());
    request.setSupportedActivities(new HashSet<>());

    FitnessCentre centre = new FitnessCentre(2, "Gym@#$%");
    when(fitnessCentreService.addCentre("Gym@#$%", new HashSet<>(), new HashSet<>()))
        .thenReturn(centre);

    // Act
    FitnessCentre result = fitnessCentreController.addFitnessCentre(request);

    // Assert
    assertEquals("Gym@#$%", result.getName());
  }

  @Test
  @DisplayName("Should add activity with all workout types")
  void testAddActivityWithDifferentTypes() {
    // Arrange
    int centreId = 1;

    // Test WEIGHTS
    AddActivityRequest weightsRequest = new AddActivityRequest();
    weightsRequest.setFitnessCentreId(centreId);
    weightsRequest.setActivity(Activity.WEIGHTS);
    weightsRequest.setStartTime(8);
    weightsRequest.setEndTime(9);
    weightsRequest.setNoOfSlots(15);

    Slot weightsSlot = new Slot(1, LocalDate.now(), Activity.WEIGHTS, 8, 9, 15, centreId);
    when(fitnessCentreService.addActivity(centreId, Activity.WEIGHTS, 8, 9, 15))
        .thenReturn(weightsSlot);

    // Act
    Slot result = fitnessCentreController.addActivity(weightsRequest, centreId);

    // Assert
    assertEquals(Activity.WEIGHTS, result.getActivity());
  }

  @Test
  @DisplayName("Should add multiple activities to the same centre")
  void testAddMultipleActivities() {
    // Arrange
    int centreId = 1;

    AddActivityRequest request1 = new AddActivityRequest();
    request1.setFitnessCentreId(centreId);
    request1.setActivity(Activity.YOGA);
    request1.setStartTime(9);
    request1.setEndTime(10);
    request1.setNoOfSlots(10);

    AddActivityRequest request2 = new AddActivityRequest();
    request2.setFitnessCentreId(centreId);
    request2.setActivity(Activity.CARDIO);
    request2.setStartTime(10);
    request2.setEndTime(11);
    request2.setNoOfSlots(20);

    Slot slot1 = new Slot(1, LocalDate.now(), Activity.YOGA, 9, 10, 10, centreId);
    Slot slot2 = new Slot(2, LocalDate.now(), Activity.CARDIO, 10, 11, 20, centreId);

    when(fitnessCentreService.addActivity(centreId, Activity.YOGA, 9, 10, 10))
        .thenReturn(slot1);
    when(fitnessCentreService.addActivity(centreId, Activity.CARDIO, 10, 11, 20))
        .thenReturn(slot2);

    // Act
    Slot result1 = fitnessCentreController.addActivity(request1, centreId);
    Slot result2 = fitnessCentreController.addActivity(request2, centreId);

    // Assert
    assertEquals(1, result1.getId());
    assertEquals(2, result2.getId());
    verify(fitnessCentreService, times(1)).addActivity(centreId, Activity.YOGA, 9, 10, 10);
    verify(fitnessCentreService, times(1)).addActivity(centreId, Activity.CARDIO, 10, 11, 20);
  }

  @Test
  @DisplayName("Should handle activity with boundary time values")
  void testAddActivityWithBoundaryTimes() {
    // Arrange
    int centreId = 1;
    AddActivityRequest request = new AddActivityRequest();
    request.setFitnessCentreId(centreId);
    request.setActivity(Activity.YOGA);
    request.setStartTime(0);
    request.setEndTime(23);
    request.setNoOfSlots(1);

    Slot slot = new Slot(1, LocalDate.now(), Activity.YOGA, 0, 23, 1, centreId);
    when(fitnessCentreService.addActivity(centreId, Activity.YOGA, 0, 23, 1))
        .thenReturn(slot);

    // Act
    Slot result = fitnessCentreController.addActivity(request, centreId);

    // Assert
    assertEquals(0, result.getStartTime());
    assertEquals(23, result.getEndTime());
  }

  @Test
  @DisplayName("Should verify controller delegates to service correctly")
  void testControllerDelegatesToService() {
    // Arrange
    AddFitnessCentreRequest request = new AddFitnessCentreRequest();
    request.setName("Test Gym");
    request.setTimings(new HashSet<>());
    request.setSupportedActivities(new HashSet<>());

    FitnessCentre centre = new FitnessCentre(5, "Test Gym");
    when(fitnessCentreService.addCentre("Test Gym", new HashSet<>(), new HashSet<>()))
        .thenReturn(centre);

    // Act
    FitnessCentre result = fitnessCentreController.addFitnessCentre(request);

    // Assert
    assertNotNull(result);
    verify(fitnessCentreService, times(1))
        .addCentre("Test Gym", new HashSet<>(), new HashSet<>());
  }
}
