package com.rsh.fitness_centre.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

  @GetMapping(path = "/healthCheck")
  public String healthCheck(){
    return "Healthy!";
  }

}
