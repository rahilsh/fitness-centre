package com.rsh.fitness_centre.entity.request;

import com.rsh.fitness_centre.entity.Activity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Activity slot creation request")
public class AddActivityRequest {
  
  @NotNull(message = "Activity is required")
  @Schema(description = "Type of activity", example = "YOGA")
  private Activity activity;
  
  @NotNull(message = "Start time is required")
  @Min(value = 0, message = "Start time must be at least 0")
  @Schema(description = "Start time in 24-hour format", example = "9")
  private Integer startTime;
  
  @NotNull(message = "End time is required")
  @Min(value = 0, message = "End time must be at least 0")
  @Schema(description = "End time in 24-hour format", example = "10")
  private Integer endTime;

  @NotNull(message = "Number of slots is required")
  @Min(value = 1, message = "Number of slots must be positive")
  @Schema(description = "Number of available slots", example = "20")
  private Integer noOfSlots;

}
