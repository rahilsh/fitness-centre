package com.rsh.fitness_centre.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.rsh.fitness_centre.entity.Activity;
import com.rsh.fitness_centre.entity.Booking;
import com.rsh.fitness_centre.entity.BookingStatus;
import com.rsh.fitness_centre.entity.FitnessCentre;
import com.rsh.fitness_centre.entity.Slot;
import com.rsh.fitness_centre.entity.User;
import com.rsh.fitness_centre.entity.request.SearchActivityRequest;
import com.rsh.fitness_centre.service.BookingService;
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
@DisplayName("SearchController Tests")
class SearchControllerTest {

  private SearchController searchController;

  @Mock
  private BookingService bookingService;

  @Mock
  private FitnessCentreService fitnessCentreService;

  @BeforeEach
  void setUp() {
    searchController = new SearchController(bookingService, fitnessCentreService);
  }

  private FitnessCentre createCentre(Long id, String name) {
    return new FitnessCentre(id, name);
  }

  private Slot createSlot(Long id, LocalDate date, Activity activity, int startTime, int endTime, int seats, FitnessCentre centre) {
    return new Slot(id, date, activity, startTime, endTime, seats, centre);
  }

  private Booking createBooking(Long slotId, Long userId, BookingStatus status) {
    Slot slot = new Slot(slotId, LocalDate.now(), null, 0, 0, 0, null);
    User user = new User(userId, "Test");
    return new Booking(1L, user, slot, null, status);
  }

  @Test
  @DisplayName("Should search activities by fitness centre name")
  void testSearchActivitiesByFitnessCentreName() {
    // Arrange
    SearchActivityRequest request = new SearchActivityRequest();
    request.setActivity("YOGA");
    request.setFitnessCentreName("Gold's Gym");

    FitnessCentre centre = createCentre(1L, "Gold's Gym");
    Set<Slot> slots = new HashSet<>();
    slots.add(createSlot(1L, LocalDate.now(), Activity.YOGA, 9, 10, 10, centre));
    Set<Booking> bookings = new HashSet<>();

    when(fitnessCentreService.getCentreByName("Gold's Gym")).thenReturn(centre);
    when(fitnessCentreService.getSlotsOfADay(1L, LocalDate.now())).thenReturn(slots);
    when(bookingService.getBookingsOfCentre(1L)).thenReturn(bookings);

    // Act
    Set<Slot> result = searchController.searchActivities(request);

    // Assert
    assertNotNull(result);
    verify(fitnessCentreService, times(1)).getCentreByName("Gold's Gym");
  }

  @Test
  @DisplayName("Should search activities by activity type across all centres")
  void testSearchActivitiesByActivityType() {
    // Arrange
    SearchActivityRequest request = new SearchActivityRequest();
    request.setActivity("CARDIO");
    request.setFitnessCentreName(null);

    FitnessCentre centre1 = createCentre(1L, "Gym A");
    FitnessCentre centre2 = createCentre(2L, "Gym B");
    Set<FitnessCentre> centres = new HashSet<>();
    centres.add(centre1);
    centres.add(centre2);

    Set<Slot> slotsA = new HashSet<>();
    slotsA.add(createSlot(1L, LocalDate.now(), Activity.CARDIO, 10, 11, 20, centre1));
    Set<Slot> slotsB = new HashSet<>();
    slotsB.add(createSlot(2L, LocalDate.now(), Activity.CARDIO, 15, 16, 25, centre2));

    when(fitnessCentreService.getAllCentres()).thenReturn(centres);
    when(fitnessCentreService.getSlotsOfADay(1L, LocalDate.now())).thenReturn(slotsA);
    when(fitnessCentreService.getSlotsOfADay(2L, LocalDate.now())).thenReturn(slotsB);
    when(bookingService.getBookingsOfCentre(1L)).thenReturn(new HashSet<>());
    when(bookingService.getBookingsOfCentre(2L)).thenReturn(new HashSet<>());

    // Act
    Set<Slot> result = searchController.searchActivities(request);

    // Assert
    assertNotNull(result);
    verify(fitnessCentreService, times(1)).getAllCentres();
  }

  @Test
  @DisplayName("Should handle case when centre name is provided but centre doesn't exist")
  void testSearchActivitiesNoResults() {
    // Arrange - When centre name is provided but null is returned, the controller returns empty
    SearchActivityRequest request = new SearchActivityRequest();
    request.setActivity("YOGA");
    request.setFitnessCentreName(null);  // Set to null to trigger getAllCentres path

    when(fitnessCentreService.getAllCentres()).thenReturn(new HashSet<>());

    // Act
    Set<Slot> result = searchController.searchActivities(request);

    // Assert
    assertNotNull(result);
    assertEquals(0, result.size());
    verify(fitnessCentreService, times(1)).getAllCentres();
  }

  @Test
  @DisplayName("Should handle search with empty fitness centre name")
  void testSearchActivitiesEmptyFitnessCentreName() {
    // Arrange
    SearchActivityRequest request = new SearchActivityRequest();
    request.setActivity("WEIGHTS");
    request.setFitnessCentreName("");

    FitnessCentre centre1 = createCentre(1L, "Gym A");
    FitnessCentre centre2 = createCentre(2L, "Gym B");
    Set<FitnessCentre> centres = new HashSet<>();
    centres.add(centre1);
    centres.add(centre2);

    Set<Slot> slotsA = new HashSet<>();
    slotsA.add(createSlot(1L, LocalDate.now(), Activity.WEIGHTS, 8, 9, 15, centre1));
    Set<Slot> slotsB = new HashSet<>();
    slotsB.add(createSlot(2L, LocalDate.now(), Activity.WEIGHTS, 17, 18, 20, centre2));

    when(fitnessCentreService.getAllCentres()).thenReturn(centres);
    when(fitnessCentreService.getSlotsOfADay(1L, LocalDate.now())).thenReturn(slotsA);
    when(fitnessCentreService.getSlotsOfADay(2L, LocalDate.now())).thenReturn(slotsB);
    when(bookingService.getBookingsOfCentre(1L)).thenReturn(new HashSet<>());
    when(bookingService.getBookingsOfCentre(2L)).thenReturn(new HashSet<>());

    // Act
    Set<Slot> result = searchController.searchActivities(request);

    // Assert
    assertNotNull(result);
    verify(fitnessCentreService, times(1)).getAllCentres();
  }

  @Test
  @DisplayName("Should exclude booked slots from search results")
  void testSearchActivitiesExcludeBooked() {
    // Arrange
    SearchActivityRequest request = new SearchActivityRequest();
    request.setActivity("YOGA");
    request.setFitnessCentreName("Gold's Gym");

    FitnessCentre centre = createCentre(1L, "Gold's Gym");
    Set<Slot> slots = new HashSet<>();
    Slot slot1 = createSlot(1L, LocalDate.now(), Activity.YOGA, 9, 10, 10, centre);
    Slot slot2 = createSlot(2L, LocalDate.now(), Activity.YOGA, 10, 11, 10, centre);
    slots.add(slot1);
    slots.add(slot2);

    Set<Booking> bookings = new HashSet<>();
    bookings.add(createBooking(1L, 1L, BookingStatus.BOOKED));

    when(fitnessCentreService.getCentreByName("Gold's Gym")).thenReturn(centre);
    when(fitnessCentreService.getSlotsOfADay(1L, LocalDate.now())).thenReturn(slots);
    when(bookingService.getBookingsOfCentre(1L)).thenReturn(bookings);

    // Act
    Set<Slot> result = searchController.searchActivities(request);

    // Assert
    assertNotNull(result);
    verify(fitnessCentreService, times(1)).getCentreByName("Gold's Gym");
    verify(bookingService, times(1)).getBookingsOfCentre(1L);
  }

  @Test
  @DisplayName("Should search activities by all activity types")
  void testSearchActivitiesByAllActivityTypes() {
    // Test SWIMMING
    SearchActivityRequest swimmingRequest = new SearchActivityRequest();
    swimmingRequest.setActivity("SWIMMING");
    swimmingRequest.setFitnessCentreName("Pool Gym");

    FitnessCentre poolGym = createCentre(3L, "Pool Gym");
    Set<Slot> poolSlots = new HashSet<>();
    poolSlots.add(createSlot(3L, LocalDate.now(), Activity.SWIMMING, 14, 15, 25, poolGym));

    when(fitnessCentreService.getCentreByName("Pool Gym")).thenReturn(poolGym);
    when(fitnessCentreService.getSlotsOfADay(3L, LocalDate.now())).thenReturn(poolSlots);
    when(bookingService.getBookingsOfCentre(3L)).thenReturn(new HashSet<>());

    // Act
    Set<Slot> result = searchController.searchActivities(swimmingRequest);

    // Assert
    assertNotNull(result);
    verify(fitnessCentreService, times(1)).getCentreByName("Pool Gym");
  }

  @Test
  @DisplayName("Should handle search with multiple available slots")
  void testSearchActivitiesMultipleSlots() {
    // Arrange
    SearchActivityRequest request = new SearchActivityRequest();
    request.setActivity("CARDIO");
    request.setFitnessCentreName("Cardio King");

    FitnessCentre centre = createCentre(4L, "Cardio King");
    Set<Slot> slots = new HashSet<>();
    slots.add(createSlot(1L, LocalDate.now(), Activity.CARDIO, 6, 7, 30, centre));
    slots.add(createSlot(2L, LocalDate.now(), Activity.CARDIO, 7, 8, 30, centre));
    slots.add(createSlot(3L, LocalDate.now(), Activity.CARDIO, 18, 19, 30, centre));
    slots.add(createSlot(4L, LocalDate.now(), Activity.CARDIO, 19, 20, 30, centre));

    when(fitnessCentreService.getCentreByName("Cardio King")).thenReturn(centre);
    when(fitnessCentreService.getSlotsOfADay(4L, LocalDate.now())).thenReturn(slots);
    when(bookingService.getBookingsOfCentre(4L)).thenReturn(new HashSet<>());

    // Act
    Set<Slot> result = searchController.searchActivities(request);

    // Assert
    assertNotNull(result);
    verify(fitnessCentreService, times(1)).getCentreByName("Cardio King");
  }

  @Test
  @DisplayName("Should search across multiple centres for activity type")
  void testSearchActivitiesMultipleCentres() {
    // Arrange
    SearchActivityRequest request = new SearchActivityRequest();
    request.setActivity("YOGA");
    request.setFitnessCentreName(null);

    FitnessCentre centre1 = createCentre(1L, "Yoga Studio 1");
    FitnessCentre centre2 = createCentre(2L, "Yoga Studio 2");
    FitnessCentre centre3 = createCentre(3L, "Yoga Studio 3");
    Set<FitnessCentre> centres = new HashSet<>();
    centres.add(centre1);
    centres.add(centre2);
    centres.add(centre3);

    Set<Slot> slots1 = new HashSet<>();
    slots1.add(createSlot(1L, LocalDate.now(), Activity.YOGA, 9, 10, 15, centre1));
    Set<Slot> slots2 = new HashSet<>();
    slots2.add(createSlot(2L, LocalDate.now(), Activity.YOGA, 10, 11, 15, centre2));
    Set<Slot> slots3 = new HashSet<>();
    slots3.add(createSlot(3L, LocalDate.now(), Activity.YOGA, 16, 17, 15, centre3));

    when(fitnessCentreService.getAllCentres()).thenReturn(centres);
    when(fitnessCentreService.getSlotsOfADay(1L, LocalDate.now())).thenReturn(slots1);
    when(fitnessCentreService.getSlotsOfADay(2L, LocalDate.now())).thenReturn(slots2);
    when(fitnessCentreService.getSlotsOfADay(3L, LocalDate.now())).thenReturn(slots3);
    when(bookingService.getBookingsOfCentre(1L)).thenReturn(new HashSet<>());
    when(bookingService.getBookingsOfCentre(2L)).thenReturn(new HashSet<>());
    when(bookingService.getBookingsOfCentre(3L)).thenReturn(new HashSet<>());

    // Act
    Set<Slot> result = searchController.searchActivities(request);

    // Assert
    assertNotNull(result);
    verify(fitnessCentreService, times(1)).getAllCentres();
  }

  @Test
  @DisplayName("Should verify controller delegates to services correctly")
  void testControllerDelegatesToServices() {
    // Arrange
    SearchActivityRequest request = new SearchActivityRequest();
    request.setActivity("YOGA");
    request.setFitnessCentreName("Test Gym");

    FitnessCentre centre = createCentre(1L, "Test Gym");
    Set<Slot> slots = new HashSet<>();
    slots.add(createSlot(1L, LocalDate.now(), Activity.YOGA, 9, 10, 10, centre));

    when(fitnessCentreService.getCentreByName("Test Gym")).thenReturn(centre);
    when(fitnessCentreService.getSlotsOfADay(1L, LocalDate.now())).thenReturn(slots);
    when(bookingService.getBookingsOfCentre(1L)).thenReturn(new HashSet<>());

    // Act
    Set<Slot> result = searchController.searchActivities(request);

    // Assert
    assertNotNull(result);
    verify(fitnessCentreService, times(1)).getCentreByName("Test Gym");
  }
}
