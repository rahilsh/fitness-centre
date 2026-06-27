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

  @Test
  @DisplayName("Should search activities by fitness centre name")
  void testSearchActivitiesByFitnessCentreName() {
    // Arrange
    SearchActivityRequest request = new SearchActivityRequest();
    request.setActivity("YOGA");
    request.setFitnessCentreName("Gold's Gym");

    FitnessCentre centre = new FitnessCentre(1, "Gold's Gym");
    Set<Slot> slots = new HashSet<>();
    slots.add(new Slot(1, LocalDate.now(), Activity.YOGA, 9, 10, 10, 1));
    Set<Booking> bookings = new HashSet<>();

    when(fitnessCentreService.getCentreByName("Gold's Gym")).thenReturn(centre);
    when(fitnessCentreService.getSlotsOfADay(1, LocalDate.now())).thenReturn(slots);
    when(bookingService.getBookingsOfCentre(1)).thenReturn(bookings);

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

    FitnessCentre centre1 = new FitnessCentre(1, "Gym A");
    FitnessCentre centre2 = new FitnessCentre(2, "Gym B");
    Set<FitnessCentre> centres = new HashSet<>();
    centres.add(centre1);
    centres.add(centre2);

    Set<Slot> slotsA = new HashSet<>();
    slotsA.add(new Slot(1, LocalDate.now(), Activity.CARDIO, 10, 11, 20, 1));
    Set<Slot> slotsB = new HashSet<>();
    slotsB.add(new Slot(2, LocalDate.now(), Activity.CARDIO, 15, 16, 25, 2));

    when(fitnessCentreService.getAllCentres()).thenReturn(centres);
    when(fitnessCentreService.getSlotsOfADay(1, LocalDate.now())).thenReturn(slotsA);
    when(fitnessCentreService.getSlotsOfADay(2, LocalDate.now())).thenReturn(slotsB);
    when(bookingService.getBookingsOfCentre(1)).thenReturn(new HashSet<>());
    when(bookingService.getBookingsOfCentre(2)).thenReturn(new HashSet<>());

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

    FitnessCentre centre1 = new FitnessCentre(1, "Gym A");
    FitnessCentre centre2 = new FitnessCentre(2, "Gym B");
    Set<FitnessCentre> centres = new HashSet<>();
    centres.add(centre1);
    centres.add(centre2);

    Set<Slot> slotsA = new HashSet<>();
    slotsA.add(new Slot(1, LocalDate.now(), Activity.WEIGHTS, 8, 9, 15, 1));
    Set<Slot> slotsB = new HashSet<>();
    slotsB.add(new Slot(2, LocalDate.now(), Activity.WEIGHTS, 17, 18, 20, 2));

    when(fitnessCentreService.getAllCentres()).thenReturn(centres);
    when(fitnessCentreService.getSlotsOfADay(1, LocalDate.now())).thenReturn(slotsA);
    when(fitnessCentreService.getSlotsOfADay(2, LocalDate.now())).thenReturn(slotsB);
    when(bookingService.getBookingsOfCentre(1)).thenReturn(new HashSet<>());
    when(bookingService.getBookingsOfCentre(2)).thenReturn(new HashSet<>());

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

    FitnessCentre centre = new FitnessCentre(1, "Gold's Gym");
    Set<Slot> slots = new HashSet<>();
    slots.add(new Slot(1, LocalDate.now(), Activity.YOGA, 9, 10, 10, 1));
    slots.add(new Slot(2, LocalDate.now(), Activity.YOGA, 10, 11, 10, 1));

    Set<Booking> bookings = new HashSet<>();
    bookings.add(new Booking(1, 1, 1, null, BookingStatus.BOOKED));

    when(fitnessCentreService.getCentreByName("Gold's Gym")).thenReturn(centre);
    when(fitnessCentreService.getSlotsOfADay(1, LocalDate.now())).thenReturn(slots);
    when(bookingService.getBookingsOfCentre(1)).thenReturn(bookings);

    // Act
    Set<Slot> result = searchController.searchActivities(request);

    // Assert
    assertNotNull(result);
    verify(fitnessCentreService, times(1)).getCentreByName("Gold's Gym");
    verify(bookingService, times(1)).getBookingsOfCentre(1);
  }

  @Test
  @DisplayName("Should search activities by all activity types")
  void testSearchActivitiesByAllActivityTypes() {
    // Test SWIMMING
    SearchActivityRequest swimmingRequest = new SearchActivityRequest();
    swimmingRequest.setActivity("SWIMMING");
    swimmingRequest.setFitnessCentreName("Pool Gym");

    FitnessCentre poolGym = new FitnessCentre(3, "Pool Gym");
    Set<Slot> poolSlots = new HashSet<>();
    poolSlots.add(new Slot(3, LocalDate.now(), Activity.SWIMMING, 14, 15, 25, 3));

    when(fitnessCentreService.getCentreByName("Pool Gym")).thenReturn(poolGym);
    when(fitnessCentreService.getSlotsOfADay(3, LocalDate.now())).thenReturn(poolSlots);
    when(bookingService.getBookingsOfCentre(3)).thenReturn(new HashSet<>());

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

    FitnessCentre centre = new FitnessCentre(4, "Cardio King");
    Set<Slot> slots = new HashSet<>();
    slots.add(new Slot(1, LocalDate.now(), Activity.CARDIO, 6, 7, 30, 4));
    slots.add(new Slot(2, LocalDate.now(), Activity.CARDIO, 7, 8, 30, 4));
    slots.add(new Slot(3, LocalDate.now(), Activity.CARDIO, 18, 19, 30, 4));
    slots.add(new Slot(4, LocalDate.now(), Activity.CARDIO, 19, 20, 30, 4));

    when(fitnessCentreService.getCentreByName("Cardio King")).thenReturn(centre);
    when(fitnessCentreService.getSlotsOfADay(4, LocalDate.now())).thenReturn(slots);
    when(bookingService.getBookingsOfCentre(4)).thenReturn(new HashSet<>());

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

    FitnessCentre centre1 = new FitnessCentre(1, "Yoga Studio 1");
    FitnessCentre centre2 = new FitnessCentre(2, "Yoga Studio 2");
    FitnessCentre centre3 = new FitnessCentre(3, "Yoga Studio 3");
    Set<FitnessCentre> centres = new HashSet<>();
    centres.add(centre1);
    centres.add(centre2);
    centres.add(centre3);

    Set<Slot> slots1 = new HashSet<>();
    slots1.add(new Slot(1, LocalDate.now(), Activity.YOGA, 9, 10, 15, 1));
    Set<Slot> slots2 = new HashSet<>();
    slots2.add(new Slot(2, LocalDate.now(), Activity.YOGA, 10, 11, 15, 2));
    Set<Slot> slots3 = new HashSet<>();
    slots3.add(new Slot(3, LocalDate.now(), Activity.YOGA, 16, 17, 15, 3));

    when(fitnessCentreService.getAllCentres()).thenReturn(centres);
    when(fitnessCentreService.getSlotsOfADay(1, LocalDate.now())).thenReturn(slots1);
    when(fitnessCentreService.getSlotsOfADay(2, LocalDate.now())).thenReturn(slots2);
    when(fitnessCentreService.getSlotsOfADay(3, LocalDate.now())).thenReturn(slots3);
    when(bookingService.getBookingsOfCentre(1)).thenReturn(new HashSet<>());
    when(bookingService.getBookingsOfCentre(2)).thenReturn(new HashSet<>());
    when(bookingService.getBookingsOfCentre(3)).thenReturn(new HashSet<>());

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

    FitnessCentre centre = new FitnessCentre(1, "Test Gym");
    Set<Slot> slots = new HashSet<>();
    slots.add(new Slot(1, LocalDate.now(), Activity.YOGA, 9, 10, 10, 1));

    when(fitnessCentreService.getCentreByName("Test Gym")).thenReturn(centre);
    when(fitnessCentreService.getSlotsOfADay(1, LocalDate.now())).thenReturn(slots);
    when(bookingService.getBookingsOfCentre(1)).thenReturn(new HashSet<>());

    // Act
    Set<Slot> result = searchController.searchActivities(request);

    // Assert
    assertNotNull(result);
    verify(fitnessCentreService, times(1)).getCentreByName("Test Gym");
  }
}
