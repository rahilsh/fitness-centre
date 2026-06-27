package com.rsh.fitness_centre.entity.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
@Schema(description = "Booking creation request")
public class AddBookingRequest {

  @NotNull(message = "Slot ID is required")
  @Positive(message = "Slot ID must be positive")
  @Schema(description = "ID of the activity slot to book", example = "1")
  private Long slotId;
  
  @NotNull(message = "User ID is required")
  @Positive(message = "User ID must be positive")
  @Schema(description = "ID of the user making the booking", example = "1")
  private Long userId;

}
