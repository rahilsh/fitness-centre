package com.rsh.fitness_centre.controller;

import com.rsh.fitness_centre.entity.Booking;
import com.rsh.fitness_centre.entity.request.AddBookingRequest;
import com.rsh.fitness_centre.service.BookingService;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bookings")
public class BookingController {

  private final BookingService bookingService;

  @Autowired
  public BookingController(BookingService bookingService) {
    this.bookingService = bookingService;
  }

  @PutMapping
  public Booking addBooking(@RequestBody AddBookingRequest request) {
    return bookingService.addBooking(request.getSlotId(), request.getUserId());
  }

  @PatchMapping("/{bookingId}")
  public Booking cancelBooking(@PathVariable int bookingId) {
    return bookingService.cancelBooking(bookingId);
  }

  @GetMapping("/{bookingId}")
  public Booking getBooking(@PathVariable int bookingId) {
    return bookingService.getBooking(bookingId);
  }

  @GetMapping
  public Set<Booking> getBookings(){
	  return bookingService.getBookings();
  }
}
