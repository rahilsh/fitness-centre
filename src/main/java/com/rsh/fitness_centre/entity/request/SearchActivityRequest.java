package com.rsh.fitness_centre.entity.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Activity search request")
public class SearchActivityRequest {

  @NotBlank(message = "Activity name is required")
  @Schema(description = "Type of activity to search for", example = "YOGA")
  private String activity;
  
  @NotBlank(message = "Fitness centre name is required")
  @Schema(description = "Name of the fitness centre (optional, can be empty)", example = "FitZone Gym")
  private String fitnessCentreName;

}
