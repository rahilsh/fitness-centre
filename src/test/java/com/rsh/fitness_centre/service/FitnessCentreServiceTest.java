package com.rsh.fitness_centre.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.rsh.fitness_centre.entity.Activity;
import com.rsh.fitness_centre.entity.FitnessCentre;
import com.rsh.fitness_centre.entity.Slot;
import com.rsh.fitness_centre.repository.FitnessCentreRepository;
import com.rsh.fitness_centre.repository.SlotRepository;
import com.rsh.fitness_centre.util.SequenceGenerator;
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
@DisplayName("FitnessCentreService Tests")
class FitnessCentreServiceTest {

  private FitnessCentreService fitnessCentreService;

  @Mock
  private FitnessCentreRepository fitnessCentreRepository;

  @Mock
  private SlotRepository slotRepository;

  @Mock
  private SequenceGenerator sequenceGenerator;

  @BeforeEach
  void setUp() {
    fitnessCentreService = new FitnessCentreService(sequenceGenerator, fitnessCentreRepository, slotRepository);
  }

  // Test: addCentre successfully
  @Test
  @DisplayName("Should add fitness centre successfully")
  void testAddCentreSuccess() {
    // Arrange
    String centreName = "Gold Gym Downtown";
    int centreId = 1;
    Set<List<Integer>> timings = new HashSet<>();
    Set<Activity> activities = new HashSet<>();
    
    FitnessCentre expectedCentre = new FitnessCentre(centreId, centreName);

    when(sequenceGenerator.getNext("FitnessCentre")).thenReturn(centreId);
    when(fitnessCentreRepository.save(expectedCentre)).thenReturn(expectedCentre);

    // Act
    FitnessCentre result = fitnessCentreService.addCentre(centreName, timings, activities);

    // Assert
    assertNotNull(result);
    assertEquals(centreId, result.getId());
    assertEquals(centreName, result.getName());
    verify(sequenceGenerator, times(1)).getNext("FitnessCentre");
    verify(fitnessCentreRepository, times(1)).save(result);
  }

  // Test: addCentre with sequential IDs
  @Test
  @DisplayName("Should generate sequential centre IDs")
  void testSequentialCentreIds() {
    // Arrange
    Set<List<Integer>> timings = new HashSet<>();
    Set<Activity> activities = new HashSet<>();

    when(sequenceGenerator.getNext("FitnessCentre")).thenReturn(1).thenReturn(2).thenReturn(3);
    when(fitnessCentreRepository.save(any(FitnessCentre.class))).thenReturn(new FitnessCentre(1, "Gym 1"));

    // Act
    fitnessCentreService.addCentre("Gym 1", timings, activities);
    fitnessCentreService.addCentre("Gym 2", timings, activities);
    fitnessCentreService.addCentre("Gym 3", timings, activities);

    // Assert
    verify(sequenceGenerator, times(3)).getNext("FitnessCentre");
    verify(fitnessCentreRepository, times(3)).save(any(FitnessCentre.class));
  }

  // Test: getCentreByName - found
  @Test
  @DisplayName("Should get centre by name successfully")
  void testGetCentreByNameSuccess() {
    // Arrange
    String centreName = "Gold Gym Downtown";
    FitnessCentre mockCentre = new FitnessCentre(1, centreName);

    when(fitnessCentreRepository.findByName(centreName)).thenReturn(Optional.of(mockCentre));

    // Act
    FitnessCentre result = fitnessCentreService.getCentreByName(centreName);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.getId());
    assertEquals(centreName, result.getName());
    verify(fitnessCentreRepository, times(1)).findByName(centreName);
  }

  // Test: getCentreByName - not found
  @Test
  @DisplayName("Should return null when centre name does not exist")
  void testGetCentreByNameNotFound() {
    // Arrange
    String centreName = "Non-Existent Gym";
    when(fitnessCentreRepository.findByName(centreName)).thenReturn(Optional.empty());

    // Act
    FitnessCentre result = fitnessCentreService.getCentreByName(centreName);

    // Assert
    assertNull(result);
    verify(fitnessCentreRepository, times(1)).findByName(centreName);
  }

  // Test: getSlotsOfADay successfully
  @Test
  @DisplayName("Should get slots of a day successfully")
  void testGetSlotsOfADaySuccess() {
    // Arrange
    int centreId = 1;
    LocalDate date = LocalDate.of(2024, 6, 27);
    List<Slot> slots = new ArrayList<>();
    slots.add(new Slot(1, date, Activity.WEIGHTS, 9, 10, 5, centreId));
    slots.add(new Slot(2, date, Activity.CARDIO, 10, 11, 5, centreId));
    slots.add(new Slot(3, date, Activity.YOGA, 18, 19, 10, centreId));

    when(slotRepository.getSlotsByDate(centreId, date)).thenReturn(slots);

    // Act
    Set<Slot> result = fitnessCentreService.getSlotsOfADay(centreId, date);

    // Assert
    assertNotNull(result);
    assertEquals(3, result.size());
    assertTrue(result.stream().anyMatch(s -> s.getId() == 1));
    assertTrue(result.stream().anyMatch(s -> s.getId() == 2));
    assertTrue(result.stream().anyMatch(s -> s.getId() == 3));
    verify(slotRepository, times(1)).getSlotsByDate(centreId, date);
  }

  // Test: getSlotsOfADay with empty results
  @Test
  @DisplayName("Should return empty set when centre has no slots on given date")
  void testGetSlotsOfADayEmpty() {
    // Arrange
    int centreId = 1;
    LocalDate date = LocalDate.of(2024, 1, 1);
    List<Slot> emptyList = new ArrayList<>();

    when(slotRepository.getSlotsByDate(centreId, date)).thenReturn(emptyList);

    // Act
    Set<Slot> result = fitnessCentreService.getSlotsOfADay(centreId, date);

    // Assert
    assertNotNull(result);
    assertEquals(0, result.size());
    verify(slotRepository, times(1)).getSlotsByDate(centreId, date);
  }

  // Test: addActivity successfully
  @Test
  @DisplayName("Should add activity (create slot) successfully")
  void testAddActivitySuccess() {
    // Arrange
    int fitnessCentreId = 1;
    Activity activity = Activity.WEIGHTS;
    int startTime = 9;
    int endTime = 10;
    int noOfSlots = 5;
    int slotId = 1;

    Slot expectedSlot = new Slot(slotId, LocalDate.now(), activity, startTime, endTime, noOfSlots, fitnessCentreId);

    when(sequenceGenerator.getNext("FitnessCentreSlot")).thenReturn(slotId);
    when(slotRepository.save(expectedSlot)).thenReturn(expectedSlot);

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
    verify(slotRepository, times(1)).save(result);
  }

  // Test: addActivity with different activities
  @Test
  @DisplayName("Should add activities of different types")
  void testAddActivityDifferentTypes() {
    // Arrange
    int fitnessCentreId = 1;
    Activity[] activities = {Activity.WEIGHTS, Activity.CARDIO, Activity.YOGA, Activity.SWIMMING};
    
    when(sequenceGenerator.getNext("FitnessCentreSlot")).thenReturn(1).thenReturn(2).thenReturn(3).thenReturn(4);
    when(slotRepository.save(any(Slot.class))).thenReturn(new Slot(1, LocalDate.now(), Activity.WEIGHTS, 9, 10, 5, fitnessCentreId));

    // Act & Assert
    for (Activity activity : activities) {
      fitnessCentreService.addActivity(fitnessCentreId, activity, 9, 10, 5);
    }
    
    verify(sequenceGenerator, times(4)).getNext("FitnessCentreSlot");
    verify(slotRepository, times(4)).save(any(Slot.class));
  }

  // Test: getAllCentres successfully
  @Test
  @DisplayName("Should get all fitness centres successfully")
  void testGetAllCentresSuccess() {
    // Arrange
    List<FitnessCentre> centres = new ArrayList<>();
    centres.add(new FitnessCentre(1, "Gold Gym"));
    centres.add(new FitnessCentre(2, "FitX"));
    centres.add(new FitnessCentre(3, "Wellness Center"));
    centres.add(new FitnessCentre(4, "CrossFit Box"));

    when(fitnessCentreRepository.findAll()).thenReturn(centres);

    // Act
    Set<FitnessCentre> result = fitnessCentreService.getAllCentres();

    // Assert
    assertNotNull(result);
    assertEquals(4, result.size());
    assertTrue(result.stream().anyMatch(c -> c.getId() == 1));
    assertTrue(result.stream().anyMatch(c -> c.getId() == 2));
    assertTrue(result.stream().anyMatch(c -> c.getId() == 3));
    assertTrue(result.stream().anyMatch(c -> c.getId() == 4));
    verify(fitnessCentreRepository, times(1)).findAll();
  }

  // Test: getAllCentres with empty results
  @Test
  @DisplayName("Should return empty set when no fitness centres exist")
  void testGetAllCentresEmpty() {
    // Arrange
    List<FitnessCentre> emptyList = new ArrayList<>();
    when(fitnessCentreRepository.findAll()).thenReturn(emptyList);

    // Act
    Set<FitnessCentre> result = fitnessCentreService.getAllCentres();

    // Assert
    assertNotNull(result);
    assertEquals(0, result.size());
    verify(fitnessCentreRepository, times(1)).findAll();
  }

  // Test: getCentreActivities successfully
  @Test
  @DisplayName("Should get all activities (slots) of a centre successfully")
  void testGetCentreActivitiesSuccess() {
    // Arrange
    int fitnessCentreId = 1;
    List<Slot> slots = new ArrayList<>();
    slots.add(new Slot(1, LocalDate.now(), Activity.WEIGHTS, 9, 10, 5, fitnessCentreId));
    slots.add(new Slot(2, LocalDate.now(), Activity.CARDIO, 10, 11, 5, fitnessCentreId));
    slots.add(new Slot(3, LocalDate.now().plusDays(1), Activity.YOGA, 18, 19, 10, fitnessCentreId));
    slots.add(new Slot(4, LocalDate.now().plusDays(1), Activity.SWIMMING, 14, 15, 8, fitnessCentreId));

    when(slotRepository.getSlotsByCenter(fitnessCentreId)).thenReturn(slots);

    // Act
    Set<Slot> result = fitnessCentreService.getCentreActivities(fitnessCentreId);

    // Assert
    assertNotNull(result);
    assertEquals(4, result.size());
    assertTrue(result.stream().anyMatch(s -> s.getId() == 1));
    assertTrue(result.stream().anyMatch(s -> s.getId() == 2));
    assertTrue(result.stream().anyMatch(s -> s.getId() == 3));
    assertTrue(result.stream().anyMatch(s -> s.getId() == 4));
    verify(slotRepository, times(1)).getSlotsByCenter(fitnessCentreId);
  }

  // Test: getCentreActivities with multiple activity types
  @Test
  @DisplayName("Should get centre activities with different activity types")
  void testGetCentreActivitiesMultipleTypes() {
    // Arrange
    int fitnessCentreId = 1;
    List<Slot> slots = new ArrayList<>();
    slots.add(new Slot(1, LocalDate.now(), Activity.WEIGHTS, 9, 10, 5, fitnessCentreId));
    slots.add(new Slot(2, LocalDate.now(), Activity.CARDIO, 10, 11, 5, fitnessCentreId));
    slots.add(new Slot(3, LocalDate.now(), Activity.YOGA, 18, 19, 10, fitnessCentreId));
    slots.add(new Slot(4, LocalDate.now(), Activity.SWIMMING, 14, 15, 8, fitnessCentreId));

    when(slotRepository.getSlotsByCenter(fitnessCentreId)).thenReturn(slots);

    // Act
    Set<Slot> result = fitnessCentreService.getCentreActivities(fitnessCentreId);

    // Assert
    assertNotNull(result);
    assertEquals(4, result.size());
    long weightsCount = result.stream().filter(s -> s.getActivity() == Activity.WEIGHTS).count();
    long cardioCount = result.stream().filter(s -> s.getActivity() == Activity.CARDIO).count();
    long yogaCount = result.stream().filter(s -> s.getActivity() == Activity.YOGA).count();
    long swimmingCount = result.stream().filter(s -> s.getActivity() == Activity.SWIMMING).count();
    assertEquals(1, weightsCount);
    assertEquals(1, cardioCount);
    assertEquals(1, yogaCount);
    assertEquals(1, swimmingCount);
    verify(slotRepository, times(1)).getSlotsByCenter(fitnessCentreId);
  }

  // Test: getCentreActivities with empty results
  @Test
  @DisplayName("Should return empty set when centre has no activities")
  void testGetCentreActivitiesEmpty() {
    // Arrange
    int fitnessCentreId = 999;
    List<Slot> emptyList = new ArrayList<>();

    when(slotRepository.getSlotsByCenter(fitnessCentreId)).thenReturn(emptyList);

    // Act
    Set<Slot> result = fitnessCentreService.getCentreActivities(fitnessCentreId);

    // Assert
    assertNotNull(result);
    assertEquals(0, result.size());
    verify(slotRepository, times(1)).getSlotsByCenter(fitnessCentreId);
  }

  // Test: Multiple centres with different names
  @Test
  @DisplayName("Should handle multiple centres with different names")
  void testMultipleCentresWithDifferentNames() {
    // Arrange
    List<FitnessCentre> centres = new ArrayList<>();
    centres.add(new FitnessCentre(1, "Gold Gym"));
    centres.add(new FitnessCentre(2, "FitX Premium"));
    centres.add(new FitnessCentre(3, "Yoga Studio"));

    when(fitnessCentreRepository.findAll()).thenReturn(centres);

    // Act
    Set<FitnessCentre> result = fitnessCentreService.getAllCentres();

    // Assert
    assertNotNull(result);
    assertEquals(3, result.size());
    assertTrue(result.stream().anyMatch(c -> c.getName().equals("Gold Gym")));
    assertTrue(result.stream().anyMatch(c -> c.getName().equals("FitX Premium")));
    assertTrue(result.stream().anyMatch(c -> c.getName().equals("Yoga Studio")));
  }

  // Test: addActivity with various time slots
  @Test
  @DisplayName("Should add activities with various time slot configurations")
  void testAddActivityVariousTimeSlots() {
    // Arrange
    int fitnessCentreId = 1;
    Activity activity = Activity.WEIGHTS;
    
    // Test different time configurations
    int[][] timeConfigs = {{6, 7}, {9, 10}, {12, 13}, {18, 19}, {20, 21}};
    
    when(sequenceGenerator.getNext("FitnessCentreSlot"))
        .thenReturn(1).thenReturn(2).thenReturn(3).thenReturn(4).thenReturn(5);
    when(slotRepository.save(any(Slot.class))).thenReturn(new Slot(1, LocalDate.now(), activity, 6, 7, 5, fitnessCentreId));

    // Act & Assert
    for (int i = 0; i < timeConfigs.length; i++) {
      fitnessCentreService.addActivity(fitnessCentreId, activity, timeConfigs[i][0], timeConfigs[i][1], 5);
    }
    
    verify(slotRepository, times(5)).save(any(Slot.class));
  }
}
