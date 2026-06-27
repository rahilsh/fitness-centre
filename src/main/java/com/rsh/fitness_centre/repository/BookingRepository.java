package com.rsh.fitness_centre.repository;

import com.rsh.fitness_centre.entity.Booking;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
  
  @Query("SELECT b FROM Booking b WHERE b.user.id = :userId")
  List<Booking> getBookingsByUser(@Param("userId") Long userId);
  
  @Query("SELECT b FROM Booking b WHERE b.slot.fitnessCentre.id = :centreId")
  List<Booking> getBookingsByCentre(@Param("centreId") Long centreId);
}
