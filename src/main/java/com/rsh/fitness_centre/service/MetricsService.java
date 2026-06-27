package com.rsh.fitness_centre.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service for collecting custom application metrics for Prometheus/Grafana.
 */
@Service
public class MetricsService {

  private final Counter registrationCounter;
  private final Counter successfulLoginCounter;
  private final Counter failedLoginCounter;
  private final Counter bookingCreationCounter;
  private final Counter bookingCancellationCounter;

  @Autowired
  public MetricsService(MeterRegistry meterRegistry) {
    this.registrationCounter = Counter.builder("app.user.registration")
        .description("Number of user registrations")
        .register(meterRegistry);

    this.successfulLoginCounter = Counter.builder("app.user.login.success")
        .description("Number of successful user logins")
        .register(meterRegistry);

    this.failedLoginCounter = Counter.builder("app.user.login.failure")
        .description("Number of failed user logins")
        .register(meterRegistry);

    this.bookingCreationCounter = Counter.builder("app.booking.created")
        .description("Number of bookings created")
        .register(meterRegistry);

    this.bookingCancellationCounter = Counter.builder("app.booking.cancelled")
        .description("Number of bookings cancelled")
        .register(meterRegistry);
  }

  public void incrementRegistration() {
    registrationCounter.increment();
  }

  public void incrementSuccessfulLogin() {
    successfulLoginCounter.increment();
  }

  public void incrementFailedLogin() {
    failedLoginCounter.increment();
  }

  public void incrementBookingCreation() {
    bookingCreationCounter.increment();
  }

  public void incrementBookingCancellation() {
    bookingCancellationCounter.increment();
  }
}
