package com.rsh.fitness_centre.entity.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class AddBookingRequest {

  @NotNull(message = "Slot ID is required")
  @Positive(message = "Slot ID must be positive")
  private Integer slotId;
  
  @NotNull(message = "User ID is required")
  @Positive(message = "User ID must be positive")
  private Integer userId;

}
