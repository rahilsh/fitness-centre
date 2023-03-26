package com.rsh.fitness_centre.controller;

import com.rsh.fitness_centre.entity.Activity;
import com.rsh.fitness_centre.entity.Booking;
import com.rsh.fitness_centre.entity.FitnessCentre;
import com.rsh.fitness_centre.entity.Slot;
import com.rsh.fitness_centre.entity.request.SearchActivityRequest;
import com.rsh.fitness_centre.service.BookingService;
import com.rsh.fitness_centre.service.FitnessCentreService;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/search")
public class SearchController {

  private final BookingService bookingService;
  private final FitnessCentreService fitnessCentreService;

  @Autowired
  public SearchController(
      BookingService bookingService, FitnessCentreService fitnessCentreService) {
    this.bookingService = bookingService;
    this.fitnessCentreService = fitnessCentreService;
  }

  @GetMapping
  public Set<Slot> searchActivities(@RequestBody SearchActivityRequest request) {

    if (request.getFitnessCentreName() != null && !request.getFitnessCentreName().isEmpty()) {
      FitnessCentre centre = fitnessCentreService.getCentreByName(request.getFitnessCentreName());
      return getSlotsByCentre(request.getActivity(), centre);
    }
    return getSlotsByType(request.getActivity());
  }

  private Set<Slot> getSlotsByType(String activity) {
    Set<FitnessCentre> addCentres = fitnessCentreService.getAllCentres();
    Set<Slot> slots = new HashSet<>();
    addCentres.forEach(
        c -> {
          Set<Slot> slotsByCentre = getSlotsByCentre(activity, c);
          slots.addAll(slotsByCentre);
        });
    return slots;
  }

  // check noOfSeats
  private Set<Slot> getSlotsByCentre(String activity, FitnessCentre centre) {

    Set<Slot> slotsOfADay = fitnessCentreService.getSlotsOfADay(centre.getId(), LocalDate.now());

    Set<Booking> bookingOfCentre = bookingService.getBookingsOfCentre(centre.getId());

    Set<Slot> availableSlots = new HashSet<>();

    slotsOfADay.stream()
        .filter(s -> s.getActivity().equals(Activity.valueOf(activity)))
        .forEach(
            s -> {
              Optional<Booking> optionalBooking =
                  bookingOfCentre.stream()
                      .filter(b -> s.getId() == b.getSlotId()) // check cancelled
                      .findAny();
              if (optionalBooking.isEmpty()) {
                availableSlots.add(s);
              }
            });
    return availableSlots;
  }
}
