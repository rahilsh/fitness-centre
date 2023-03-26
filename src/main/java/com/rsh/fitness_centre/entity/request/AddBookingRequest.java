package com.rsh.fitness_centre.entity.request;

import lombok.Data;

@Data
public class AddBookingRequest {

  private int slotId;
  private int userId;

}
