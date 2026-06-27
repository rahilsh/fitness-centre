package com.rsh.fitness_centre.controller;

import com.rsh.fitness_centre.entity.Activity;
import com.rsh.fitness_centre.entity.FitnessCentre;
import com.rsh.fitness_centre.entity.Slot;
import com.rsh.fitness_centre.entity.request.AddActivityRequest;
import com.rsh.fitness_centre.entity.request.AddFitnessCentreRequest;
import com.rsh.fitness_centre.service.FitnessCentreService;
import jakarta.validation.Valid;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(("/fitnessCentres"))
public class FitnessCentreController {

  private final FitnessCentreService fitnessCentreService;

  @Autowired
  public FitnessCentreController(FitnessCentreService fitnessCentreService) {
    this.fitnessCentreService = fitnessCentreService;
  }

  @PostMapping
  public FitnessCentre addFitnessCentre(@Valid @RequestBody AddFitnessCentreRequest request) {
    return fitnessCentreService.addCentre(
        request.getName(),
        request.getTimings(),
        request.getSupportedActivities().stream()
            .map(Activity::valueOf)
            .collect(Collectors.toSet()));
  }

  @PostMapping("/{fitnessCentreId}/slots")
  public Slot addActivity(
      @Valid @RequestBody AddActivityRequest request, @PathVariable int fitnessCentreId) {
    return fitnessCentreService.addActivity(
        request.getFitnessCentreId(),
        request.getActivity(),
        request.getStartTime(),
        request.getEndTime(),
        request.getNoOfSlots());
  }

  @GetMapping("/{fitnessCentreId}/slots")
  public Set<Slot> getActivities(@PathVariable int fitnessCentreId) {
    return fitnessCentreService.getCentreActivities(fitnessCentreId);
  }
}
