package com.rsh.fitness_centre.controller;

import com.rsh.fitness_centre.entity.Activity;
import com.rsh.fitness_centre.entity.FitnessCentre;
import com.rsh.fitness_centre.entity.Slot;
import com.rsh.fitness_centre.entity.request.AddActivityRequest;
import com.rsh.fitness_centre.entity.request.AddFitnessCentreRequest;
import com.rsh.fitness_centre.service.FitnessCentreService;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
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

  @PutMapping
  public FitnessCentre addFitnessCentre(@RequestBody AddFitnessCentreRequest request) {
    return fitnessCentreService.addCentre(
        request.getName(),
        request.getTimings(),
        request.getSupportedActivities().stream()
            .map(Activity::valueOf)
            .collect(Collectors.toSet()));
  }

  @PutMapping("/{fitnessCentreId}/slots")
  public Slot addActivity(
      @RequestBody AddActivityRequest request, @PathVariable int fitnessCentreId) {
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
