package com.rsh.fitness_centre.controller;

import com.rsh.fitness_centre.entity.Activity;
import com.rsh.fitness_centre.entity.FitnessCentre;
import com.rsh.fitness_centre.entity.Slot;
import com.rsh.fitness_centre.entity.request.AddActivityRequest;
import com.rsh.fitness_centre.entity.request.AddFitnessCentreRequest;
import com.rsh.fitness_centre.service.FitnessCentreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Fitness Centres", description = "Fitness centre management endpoints")
public class FitnessCentreController {

  private final FitnessCentreService fitnessCentreService;

  @Autowired
  public FitnessCentreController(FitnessCentreService fitnessCentreService) {
    this.fitnessCentreService = fitnessCentreService;
  }

  @PostMapping
  @Operation(summary = "Create a new fitness centre", description = "Register a new fitness centre with timings and supported activities")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Fitness centre created successfully",
          content = @Content(mediaType = "application/json", schema = @Schema(implementation = FitnessCentre.class))),
      @ApiResponse(responseCode = "400", description = "Invalid input - name, timings, and activities are required")
  })
  public FitnessCentre addFitnessCentre(@Valid @RequestBody AddFitnessCentreRequest request) {
    return fitnessCentreService.addCentre(
        request.getName(),
        request.getTimings(),
        request.getSupportedActivities().stream()
            .map(Activity::valueOf)
            .collect(Collectors.toSet()));
  }

  @PostMapping("/{fitnessCentreId}/slots")
  @Operation(summary = "Add activity slot", description = "Add a new activity slot to a fitness centre")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Activity slot added successfully",
          content = @Content(mediaType = "application/json", schema = @Schema(implementation = Slot.class))),
      @ApiResponse(responseCode = "400", description = "Invalid input - activity details are required")
  })
  public Slot addActivity(
      @Valid @RequestBody AddActivityRequest request,
      @Parameter(description = "Fitness Centre ID") @PathVariable Long fitnessCentreId) {
    return fitnessCentreService.addActivity(
        fitnessCentreId,
        request.getActivity(),
        request.getStartTime(),
        request.getEndTime(),
        request.getNoOfSlots());
  }

  @GetMapping("/{fitnessCentreId}/slots")
  @Operation(summary = "Get fitness centre slots", description = "Retrieve all activity slots for a specific fitness centre")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Slots retrieved successfully",
          content = @Content(mediaType = "application/json", schema = @Schema(implementation = Slot.class)))
  })
  public Set<Slot> getActivities(
      @Parameter(description = "Fitness Centre ID") @PathVariable Long fitnessCentreId) {
    return fitnessCentreService.getCentreActivities(fitnessCentreId);
  }
}
