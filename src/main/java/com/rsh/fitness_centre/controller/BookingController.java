package com.rsh.fitness_centre.controller;

import com.rsh.fitness_centre.entity.Booking;
import com.rsh.fitness_centre.entity.request.AddBookingRequest;
import com.rsh.fitness_centre.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bookings")
@Tag(name = "Bookings", description = "Booking management endpoints")
public class BookingController {

  private final BookingService bookingService;

  @Autowired
  public BookingController(BookingService bookingService) {
    this.bookingService = bookingService;
  }

  @PostMapping
  @Operation(summary = "Create a new booking", description = "Book a fitness activity slot for a user")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Booking created successfully",
          content = @Content(mediaType = "application/json", schema = @Schema(implementation = Booking.class))),
      @ApiResponse(responseCode = "400", description = "Invalid input - slot ID and user ID are required")
  })
  public Booking addBooking(@Valid @RequestBody AddBookingRequest request) {
    return bookingService.addBooking(request.getSlotId(), request.getUserId());
  }

  @PatchMapping("/{bookingId}")
  @Operation(summary = "Cancel a booking", description = "Cancel an existing booking by booking ID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Booking cancelled successfully",
          content = @Content(mediaType = "application/json", schema = @Schema(implementation = Booking.class))),
      @ApiResponse(responseCode = "404", description = "Booking not found")
  })
  public Booking cancelBooking(
      @Parameter(description = "Booking ID") @PathVariable int bookingId) {
    return bookingService.cancelBooking(bookingId);
  }

  @GetMapping("/{bookingId}")
  @Operation(summary = "Get booking details", description = "Retrieve details of a specific booking")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Booking retrieved successfully",
          content = @Content(mediaType = "application/json", schema = @Schema(implementation = Booking.class))),
      @ApiResponse(responseCode = "404", description = "Booking not found")
  })
  public Booking getBooking(
      @Parameter(description = "Booking ID") @PathVariable int bookingId) {
    return bookingService.getBooking(bookingId);
  }

  @GetMapping
  @Operation(summary = "Get all bookings", description = "Retrieve a list of all bookings")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Bookings retrieved successfully",
          content = @Content(mediaType = "application/json", schema = @Schema(implementation = Booking.class)))
  })
  public Set<Booking> getBookings(){
	  return bookingService.getBookings();
  }
}
