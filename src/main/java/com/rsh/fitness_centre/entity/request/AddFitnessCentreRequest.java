package com.rsh.fitness_centre.entity.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Set;
import lombok.Data;

@Data
public class AddFitnessCentreRequest {

  @NotBlank(message = "Fitness centre name is required")
  @Size(min = 2, max = 100, message = "Name must be between 2-100 characters")
  private String name;
  
  @NotEmpty(message = "Timings cannot be empty")
  private Set<List<Integer>> timings;
  
  @NotEmpty(message = "Supported activities cannot be empty")
  private Set<String> supportedActivities;

}
