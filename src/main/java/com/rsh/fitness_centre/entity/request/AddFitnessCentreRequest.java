package com.rsh.fitness_centre.entity.request;

import java.util.List;
import java.util.Set;
import lombok.Data;

@Data
public class AddFitnessCentreRequest {

  private String name;
  private Set<List<Integer>> timings;
  private Set<String> supportedActivities;

}
