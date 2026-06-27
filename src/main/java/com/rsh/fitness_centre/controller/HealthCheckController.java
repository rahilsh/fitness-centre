package com.rsh.fitness_centre.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Health", description = "Application health check")
public class HealthCheckController {

  @GetMapping(path = "/health")
  @Operation(summary = "Check application health", description = "Verify that the application is running and healthy")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Application is healthy")
  })
  public String healthCheck(){
    return "Healthy!";
  }

}
