package com.rsh.fitness_centre.entity.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Set;
import lombok.Data;

@Data
@Schema(description = "Fitness centre creation request")
public class AddFitnessCentreRequest {

  @NotBlank(message = "Fitness centre name is required")
  @Size(min = 2, max = 100, message = "Name must be between 2-100 characters")
  @Schema(description = "Name of the fitness centre", example = "FitZone Gym")
  private String name;
  
  @NotEmpty(message = "Timings cannot be empty")
  @Schema(description = "Operating timings as a set of [open, close] hour pairs", example = "[[6, 22], [8, 20]]")
  private Set<List<Integer>> timings;
  
  @NotEmpty(message = "Supported activities cannot be empty")
  @Schema(description = "List of supported activities", example = "[\"CARDIO\", \"YOGA\"]")
  private Set<String> supportedActivities;

}
