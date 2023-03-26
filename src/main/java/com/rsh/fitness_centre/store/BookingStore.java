package com.rsh.fitness_centre.store;

import com.rsh.fitness_centre.entity.Booking;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class BookingStore {

  private final Map<Integer, Set<Booking>> userBookings = new HashMap<>();

  private final Map<Integer, Booking> bookings = new HashMap<>();

  private final Map<Integer, Set<Booking>> centreBookings = new HashMap<>();

  public void addBooking(Booking booking, int fitnessCenterId) {
    bookings.put(booking.getId(), booking);
    if (centreBookings.containsKey(fitnessCenterId)) {
      centreBookings.computeIfPresent(
          fitnessCenterId,
          (i, bs) -> {
            Set<Booking> bookings1 = new HashSet<>(bs);
            bookings1.add(booking);
            return bookings1;
          });
    } else {
      centreBookings.computeIfAbsent(fitnessCenterId, i -> Set.of(booking));
    }
    if (userBookings.containsKey(booking.getBookedBy())) {
      userBookings.computeIfPresent(
          booking.getBookedBy(),
          (i, bookings) -> {
            Set<Booking> bookings1 = new HashSet<>(bookings);
            bookings1.add(booking);
            return bookings1;
          });
    } else {
      userBookings.computeIfAbsent(booking.getBookedBy(), i -> Set.of(booking));
    }
  }

  public Booking getBooking(int bookingId) {
    return bookings.get(bookingId);
  }

  public Set<Booking> getBookingsByCentre(int centre) {
    if (!centreBookings.containsKey(centre)) {
      return Set.of();
    }
    return centreBookings.get(centre);
  }

  public Set<Booking> getBookings() {
    Set<Booking> bks = new HashSet<>();
    bookings.forEach((k, v) -> bks.add(v));
    return bks;
  }
}
