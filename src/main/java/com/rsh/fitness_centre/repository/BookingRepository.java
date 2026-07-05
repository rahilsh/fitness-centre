package com.rsh.fitness_centre.repository;

import com.rsh.fitness_centre.entity.Booking;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

/**
 * Repository interface for Booking entity.
 * Implementation uses JDBC instead of JPA.
 */
public interface BookingRepository {
  
  Booking save(Booking booking);

  Optional<Booking> findById(Long id);

  List<Booking> getBookingsByUser(Long userId);
  
  List<Booking> getBookingsByCentre(Long centreId);

  List<Booking> findAll();
  
  /**
   * Find all bookings with pagination.
   * @param pageable pagination parameters
   * @return Page of bookings
   */
  default Page<Booking> findAll(Pageable pageable) {
    List<Booking> all = findAll();
    int start = (int) pageable.getOffset();
    int end = Math.min(start + pageable.getPageSize(), all.size());
    return new PageImpl<>(all.subList(start, end), pageable, all.size());
  }

  void delete(Long id);

  void deleteById(Long id);

  void deleteAll();

  long count();

  boolean existsById(Long id);
}
