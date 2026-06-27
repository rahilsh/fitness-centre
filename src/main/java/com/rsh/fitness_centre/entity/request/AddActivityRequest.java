package com.rsh.fitness_centre.entity.request;

import com.rsh.fitness_centre.entity.Activity;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class AddActivityRequest {

  @NotNull(message = "Fitness centre ID is required")
  @Positive(message = "Fitness centre ID must be positive")
  private Integer fitnessCentreId;
  
  @NotNull(message = "Activity is required")
  private Activity activity;
  
  @NotNull(message = "Start time is required")
  @Min(value = 0, message = "Start time must be at least 0")
  private Integer startTime;
  
  @NotNull(message = "End time is required")
  @Min(value = 0, message = "End time must be at least 0")
  private Integer endTime;

  @NotNull(message = "Number of slots is required")
  @Positive(message = "Number of slots must be positive")
  private Integer noOfSlots;

}
