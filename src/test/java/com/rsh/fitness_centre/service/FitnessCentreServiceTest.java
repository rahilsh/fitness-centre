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

  @BeforeEach
  void setUp() {
    fitnessCentreService = new FitnessCentreService(fitnessCentreRepository, slotRepository);
  }

  private FitnessCentre createCentre(Long id, String name) {
    return new FitnessCentre(id, name);
  }

  private Slot createSlot(Long id, Activity activity, FitnessCentre centre) {
    return new Slot(id, LocalDate.now(), activity, 9, 10, 20, centre);
  }

  @Test
  @DisplayName("Should add fitness centre successfully")
  void testAddCentreSuccess() {
    // Arrange
    String centreName = "Gold Gym Downtown";
    Set<List<Integer>> timings = new HashSet<>();
    Set<Activity> activities = new HashSet<>();
    
    FitnessCentre expectedCentre = createCentre(1L, centreName);
    when(fitnessCentreRepository.save(any(FitnessCentre.class))).thenReturn(expectedCentre);

    // Act
    FitnessCentre result = fitnessCentreService.addCentre(centreName, timings, activities);

    // Assert
    assertNotNull(result);
    assertEquals(1L, result.getId());
    assertEquals(centreName, result.getName());
    verify(fitnessCentreRepository, times(1)).save(any(FitnessCentre.class));
  }

  @Test
  @DisplayName("Should get centre by name successfully")
  void testGetCentreByNameSuccess() {
    // Arrange
    String centreName = "Gold Gym Downtown";
    FitnessCentre mockCentre = createCentre(1L, centreName);

    when(fitnessCentreRepository.findByName(centreName)).thenReturn(Optional.of(mockCentre));

    // Act
    FitnessCentre result = fitnessCentreService.getCentreByName(centreName);

    // Assert
    assertNotNull(result);
    assertEquals(1L, result.getId());
    assertEquals(centreName, result.getName());
    verify(fitnessCentreRepository, times(1)).findByName(centreName);
  }

  @Test
  @DisplayName("Should return null when centre name does not exist")
  void testGetCentreByNameNotFound() {
    // Arrange
    String centreName = "Non-existent Gym";

    when(fitnessCentreRepository.findByName(centreName)).thenReturn(Optional.empty());

    // Act
    FitnessCentre result = fitnessCentreService.getCentreByName(centreName);

    // Assert
    assertNull(result);
    verify(fitnessCentreRepository, times(1)).findByName(centreName);
  }

  @Test
  @DisplayName("Should get centre by ID successfully")
  void testGetCentreByIdSuccess() {
    // Arrange
    Long centreId = 1L;
    FitnessCentre mockCentre = createCentre(centreId, "Gold Gym");

    when(fitnessCentreRepository.findById(centreId)).thenReturn(Optional.of(mockCentre));

    // Act
    FitnessCentre result = fitnessCentreService.getCentreById(centreId);

    // Assert
    assertNotNull(result);
    assertEquals(centreId, result.getId());
    verify(fitnessCentreRepository, times(1)).findById(centreId);
  }

  @Test
  @DisplayName("Should get all centres successfully")
  void testGetAllCentresSuccess() {
    // Arrange
    List<FitnessCentre> centres = new ArrayList<>();
    centres.add(createCentre(1L, "Gym A"));
    centres.add(createCentre(2L, "Gym B"));
    centres.add(createCentre(3L, "Gym C"));

    when(fitnessCentreRepository.findAll()).thenReturn(centres);

    // Act
    Set<FitnessCentre> result = fitnessCentreService.getAllCentres();

    // Assert
    assertNotNull(result);
    assertEquals(3, result.size());
    verify(fitnessCentreRepository, times(1)).findAll();
  }

  @Test
  @DisplayName("Should return empty set when no centres exist")
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

  @Test
  @DisplayName("Should add activity successfully")
  void testAddActivitySuccess() {
    // Arrange
    Long centreId = 1L;
    FitnessCentre mockCentre = createCentre(centreId, "Gold Gym");
    Slot expectedSlot = createSlot(1L, Activity.YOGA, mockCentre);

    when(fitnessCentreRepository.findById(centreId)).thenReturn(Optional.of(mockCentre));
    when(slotRepository.save(any(Slot.class))).thenReturn(expectedSlot);

    // Act
    Slot result = fitnessCentreService.addActivity(centreId, Activity.YOGA, 9, 10, 20);

    // Assert
    assertNotNull(result);
    assertEquals(1L, result.getId());
    assertEquals(Activity.YOGA, result.getActivity());
    verify(fitnessCentreRepository, times(1)).findById(centreId);
    verify(slotRepository, times(1)).save(any(Slot.class));
  }

  @Test
  @DisplayName("Should return null when adding activity to non-existent centre")
  void testAddActivityToNonExistentCentre() {
    // Arrange
    Long centreId = 999L;

    when(fitnessCentreRepository.findById(centreId)).thenReturn(Optional.empty());

    // Act
    Slot result = fitnessCentreService.addActivity(centreId, Activity.YOGA, 9, 10, 20);

    // Assert
    assertNull(result);
    verify(fitnessCentreRepository, times(1)).findById(centreId);
  }

  @Test
  @DisplayName("Should get slots of a day successfully")
  void testGetSlotsOfADaySuccess() {
    // Arrange
    Long centreId = 1L;
    LocalDate date = LocalDate.now();
    FitnessCentre centre = createCentre(centreId, "Gold Gym");
    FitnessCentre centre2 = createCentre(1L, "Gold Gym");  // Need separate instances for assertEquals
    List<Slot> slots = new ArrayList<>();
    Slot slot1 = new Slot(1L, date, Activity.YOGA, 9, 10, 20, centre);
    Slot slot2 = new Slot(2L, date, Activity.CARDIO, 10, 11, 25, centre2);
    slots.add(slot1);
    slots.add(slot2);

    when(slotRepository.getSlotsByDate(centreId, date)).thenReturn(slots);

    // Act
    Set<Slot> result = fitnessCentreService.getSlotsOfADay(centreId, date);

    // Assert
    assertNotNull(result);
    assertEquals(2, result.size());
    verify(slotRepository, times(1)).getSlotsByDate(centreId, date);
  }

  @Test
  @DisplayName("Should get centre activities successfully")
  void testGetCentreActivitiesSuccess() {
    // Arrange
    Long centreId = 1L;
    FitnessCentre centre = createCentre(centreId, "Gold Gym");
    List<Slot> slots = new ArrayList<>();
    Slot slot1 = new Slot(1L, LocalDate.now(), Activity.YOGA, 9, 10, 20, centre);
    Slot slot2 = new Slot(2L, LocalDate.now(), Activity.CARDIO, 10, 11, 25, centre);
    Slot slot3 = new Slot(3L, LocalDate.now(), Activity.WEIGHTS, 8, 9, 15, centre);
    slots.add(slot1);
    slots.add(slot2);
    slots.add(slot3);

    when(slotRepository.getSlotsByCenter(centreId)).thenReturn(slots);

    // Act
    Set<Slot> result = fitnessCentreService.getCentreActivities(centreId);

    // Assert
    assertNotNull(result);
    assertEquals(3, result.size());
    verify(slotRepository, times(1)).getSlotsByCenter(centreId);
  }

  @Test
  @DisplayName("Should return empty set when centre has no activities")
  void testGetCentreActivitiesEmpty() {
    // Arrange
    Long centreId = 999L;
    List<Slot> emptyList = new ArrayList<>();

    when(slotRepository.getSlotsByCenter(centreId)).thenReturn(emptyList);

    // Act
    Set<Slot> result = fitnessCentreService.getCentreActivities(centreId);

    // Assert
    assertNotNull(result);
    assertEquals(0, result.size());
    verify(slotRepository, times(1)).getSlotsByCenter(centreId);
  }

  @Test
  @DisplayName("Should handle adding multiple activities with different types")
  void testAddMultipleActivitiesWithDifferentTypes() {
    // Arrange
    Long centreId = 1L;
    FitnessCentre mockCentre = createCentre(centreId, "Gold Gym");

    when(fitnessCentreRepository.findById(centreId)).thenReturn(Optional.of(mockCentre));
    when(slotRepository.save(any(Slot.class)))
        .thenReturn(createSlot(1L, Activity.YOGA, mockCentre))
        .thenReturn(createSlot(2L, Activity.CARDIO, mockCentre))
        .thenReturn(createSlot(3L, Activity.WEIGHTS, mockCentre));

    // Act
    Slot yoga = fitnessCentreService.addActivity(centreId, Activity.YOGA, 9, 10, 20);
    Slot cardio = fitnessCentreService.addActivity(centreId, Activity.CARDIO, 10, 11, 25);
    Slot weights = fitnessCentreService.addActivity(centreId, Activity.WEIGHTS, 8, 9, 15);

    // Assert
    assertEquals(Activity.YOGA, yoga.getActivity());
    assertEquals(Activity.CARDIO, cardio.getActivity());
    assertEquals(Activity.WEIGHTS, weights.getActivity());
    verify(fitnessCentreRepository, times(3)).findById(centreId);
    verify(slotRepository, times(3)).save(any(Slot.class));
  }

  @Test
  @DisplayName("Should verify service delegates to repositories correctly")
  void testServiceDelegatesToRepositories() {
    // Arrange
    String centreName = "Test Gym";
    FitnessCentre mockCentre = createCentre(1L, centreName);
    Set<List<Integer>> timings = new HashSet<>();
    Set<Activity> activities = new HashSet<>();

    when(fitnessCentreRepository.save(any(FitnessCentre.class))).thenReturn(mockCentre);
    when(fitnessCentreRepository.findByName(centreName)).thenReturn(Optional.of(mockCentre));
    when(fitnessCentreRepository.findAll()).thenReturn(java.util.List.of(mockCentre));

    // Act
    fitnessCentreService.addCentre(centreName, timings, activities);
    fitnessCentreService.getCentreByName(centreName);
    fitnessCentreService.getAllCentres();

    // Assert
    verify(fitnessCentreRepository, times(1)).save(any(FitnessCentre.class));
    verify(fitnessCentreRepository, times(1)).findByName(centreName);
    verify(fitnessCentreRepository, times(1)).findAll();
  }
}
