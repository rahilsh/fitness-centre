package com.rsh.fitness_centre.entity.request;

import com.rsh.fitness_centre.entity.Activity;
import lombok.Data;

@Data
public class AddActivityRequest {

  private int fitnessCentreId;
  private Activity activity;
  private int startTime;
  private int endTime;

  private int noOfSlots;

}
