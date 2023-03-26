package com.rsh.fitness_centre.entity.request;

import lombok.Data;

@Data
public class SearchActivityRequest {

  private String activity;
  private String fitnessCentreName;

}
