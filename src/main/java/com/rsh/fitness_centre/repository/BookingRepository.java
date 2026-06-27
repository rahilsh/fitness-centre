package com.rsh.fitness_centre.repository;

import com.rsh.fitness_centre.entity.Booking;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
  
  @Query("SELECT b FROM Booking b WHERE b.bookedBy = :userId")
  List<Booking> getBookingsByUser(@Param("userId") int userId);
  
  @Query("SELECT b FROM Booking b WHERE b.slotId IN (SELECT s.id FROM Slot s WHERE s.fitnessCenterId = :centreId)")
  List<Booking> getBookingsByCentre(@Param("centreId") int centreId);
}
