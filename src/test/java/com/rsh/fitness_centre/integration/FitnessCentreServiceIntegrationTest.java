package com.rsh.fitness_centre.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.rsh.fitness_centre.entity.Activity;
import com.rsh.fitness_centre.entity.FitnessCentre;
import com.rsh.fitness_centre.entity.Slot;
import com.rsh.fitness_centre.repository.FitnessCentreRepository;
import com.rsh.fitness_centre.repository.SlotRepository;
import com.rsh.fitness_centre.service.FitnessCentreService;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("FitnessCentreService Integration Tests")
@Transactional
class FitnessCentreServiceIntegrationTest {

  @Autowired
  private FitnessCentreService fitnessCentreService;

  @Autowired
  private FitnessCentreRepository fitnessCentreRepository;

  @Autowired
  private SlotRepository slotRepository;

  @BeforeEach
  void setUp() {
    fitnessCentreRepository.deleteAll();
    slotRepository.deleteAll();
  }

  @Test
  @DisplayName("Should add fitness centre and persist to database")
  void testAddCentrePersistsToDatabase() {
    // Act
    FitnessCentre centre = fitnessCentreService.addCentre(
        "Gold Gym",
        new HashSet<>(),
        new HashSet<>()
    );

    // Assert
    assertNotNull(centre);
    assertNotNull(centre.getId());
    assertEquals("Gold Gym", centre.getName());

    // Verify persistence
    FitnessCentre persistedCentre = fitnessCentreRepository.findById(centre.getId()).orElse(null);
    assertNotNull(persistedCentre);
    assertEquals("Gold Gym", persistedCentre.getName());
  }

  @Test
  @DisplayName("Should retrieve fitness centre by name from database")
  void testGetCentreByNameFetchesFromDatabase() {
    // Arrange
    FitnessCentre addedCentre = fitnessCentreService.addCentre(
        "Fitness Plus",
        new HashSet<>(),
        new HashSet<>()
    );

    // Act
    FitnessCentre retrievedCentre = fitnessCentreService.getCentreByName("Fitness Plus");

    // Assert
    assertNotNull(retrievedCentre);
    assertEquals("Fitness Plus", retrievedCentre.getName());
    assertEquals(addedCentre.getId(), retrievedCentre.getId());
  }

  @Test
  @DisplayName("Should return null when centre not found by name")
  void testGetCentreByNameReturnsNullWhenNotFound() {
    // Act
    FitnessCentre centre = fitnessCentreService.getCentreByName("NonExistent");

    // Assert
    assertNull(centre);
  }

  @Test
  @DisplayName("Should add activity/slot and persist to database")
  void testAddActivityPersistsSlotToDatabase() {
    // Arrange
    FitnessCentre centre = fitnessCentreService.addCentre(
        "Sport Arena",
        new HashSet<>(),
        new HashSet<>()
    );

    // Act
    Slot slot = fitnessCentreService.addActivity(
        centre.getId(),
        Activity.WEIGHTS,
        9,
        10,
        20
    );

    // Assert
    assertNotNull(slot);
    assertNotNull(slot.getId());
    assertEquals(Activity.WEIGHTS, slot.getActivity());
    assertEquals(9, slot.getStartTime());
    assertEquals(10, slot.getEndTime());
    assertEquals(20, slot.getNoOfSeats());
    assertEquals(centre.getId(), slot.getFitnessCentre().getId());

    // Verify persistence
    Slot persistedSlot = slotRepository.findById(slot.getId()).orElse(null);
    assertNotNull(persistedSlot);
    assertEquals(Activity.WEIGHTS, persistedSlot.getActivity());
  }

  @Test
  @DisplayName("Should retrieve all fitness centres from database")
  void testGetAllCentresFetchesFromDatabase() {
    // Arrange
    fitnessCentreService.addCentre("Gym 1", new HashSet<>(), new HashSet<>());
    fitnessCentreService.addCentre("Gym 2", new HashSet<>(), new HashSet<>());
    fitnessCentreService.addCentre("Gym 3", new HashSet<>(), new HashSet<>());

    // Act
    Set<FitnessCentre> allCentres = fitnessCentreService.getAllCentres();

    // Assert
    assertEquals(3, allCentres.size());
    assertTrue(allCentres.stream().anyMatch(c -> c.getName().equals("Gym 1")));
    assertTrue(allCentres.stream().anyMatch(c -> c.getName().equals("Gym 2")));
    assertTrue(allCentres.stream().anyMatch(c -> c.getName().equals("Gym 3")));
  }

  @Test
  @DisplayName("Should retrieve slots for a specific date from database")
  void testGetSlotsOfADayFetchesFromDatabase() {
    // Arrange
    FitnessCentre centre = fitnessCentreService.addCentre(
        "Multi Activity Gym",
        new HashSet<>(),
        new HashSet<>()
    );

    LocalDate targetDate = LocalDate.now();
    Slot slot1 = fitnessCentreService.addActivity(centre.getId(), Activity.YOGA, 8, 9, 15);
    Slot slot2 = fitnessCentreService.addActivity(centre.getId(), Activity.CARDIO, 10, 11, 20);

    // Act
    Set<Slot> slotsForDay = fitnessCentreService.getSlotsOfADay(centre.getId(), targetDate);

    // Assert
    assertNotNull(slotsForDay);
    assertEquals(2, slotsForDay.size());
    assertTrue(slotsForDay.stream().anyMatch(s -> s.getActivity() == Activity.YOGA));
    assertTrue(slotsForDay.stream().anyMatch(s -> s.getActivity() == Activity.CARDIO));
  }

  @Test
  @DisplayName("Should retrieve activities for a centre from database")
  void testGetCentreActivitiesFetchesFromDatabase() {
    // Arrange
    FitnessCentre centre = fitnessCentreService.addCentre(
        "Premium Gym",
        new HashSet<>(),
        new HashSet<>()
    );

    fitnessCentreService.addActivity(centre.getId(), Activity.WEIGHTS, 6, 7, 10);
    fitnessCentreService.addActivity(centre.getId(), Activity.SWIMMING, 9, 10, 30);
    fitnessCentreService.addActivity(centre.getId(), Activity.YOGA, 18, 19, 20);

    // Act
    Set<Slot> activities = fitnessCentreService.getCentreActivities(centre.getId());

    // Assert
    assertEquals(3, activities.size());
    assertTrue(activities.stream().anyMatch(s -> s.getActivity() == Activity.WEIGHTS));
    assertTrue(activities.stream().anyMatch(s -> s.getActivity() == Activity.SWIMMING));
    assertTrue(activities.stream().anyMatch(s -> s.getActivity() == Activity.YOGA));
  }

  @Test
  @DisplayName("Should return empty set when no slots for date")
  void testGetSlotsOfADayReturnsEmptySetWhenNotFound() {
    // Arrange
    FitnessCentre centre = fitnessCentreService.addCentre(
        "Empty Gym",
        new HashSet<>(),
        new HashSet<>()
    );

    // Act
    Set<Slot> slotsForFutureDate = fitnessCentreService.getSlotsOfADay(
        centre.getId(),
        LocalDate.now().plusDays(100)
    );

    // Assert
    assertNotNull(slotsForFutureDate);
    assertTrue(slotsForFutureDate.isEmpty());
  }

  @Test
  @DisplayName("Should persist multiple centres independently")
  void testMultipleCentresPersistenceIndependence() {
    // Act
    FitnessCentre centre1 = fitnessCentreService.addCentre("Centre 1", new HashSet<>(), new HashSet<>());
    FitnessCentre centre2 = fitnessCentreService.addCentre("Centre 2", new HashSet<>(), new HashSet<>());

    fitnessCentreService.addActivity(centre1.getId(), Activity.YOGA, 8, 9, 15);
    fitnessCentreService.addActivity(centre2.getId(), Activity.CARDIO, 10, 11, 20);

    // Assert
    Set<Slot> centre1Activities = fitnessCentreService.getCentreActivities(centre1.getId());
    Set<Slot> centre2Activities = fitnessCentreService.getCentreActivities(centre2.getId());

    assertEquals(1, centre1Activities.size());
    assertEquals(1, centre2Activities.size());
    assertTrue(centre1Activities.stream().allMatch(s -> s.getActivity() == Activity.YOGA));
    assertTrue(centre2Activities.stream().allMatch(s -> s.getActivity() == Activity.CARDIO));
  }
}
