package com.rsh.fitness_centre.entity.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SearchActivityRequest {

  @NotBlank(message = "Activity name is required")
  private String activity;
  
  @NotBlank(message = "Fitness centre name is required")
  private String fitnessCentreName;

}
