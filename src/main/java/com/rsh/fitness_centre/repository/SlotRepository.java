package com.rsh.fitness_centre.repository;

import com.rsh.fitness_centre.entity.Slot;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SlotRepository extends JpaRepository<Slot, Integer> {
  
  @Query("SELECT s FROM Slot s WHERE s.fitnessCenterId = :centreId AND s.date = :date")
  List<Slot> getSlotsByDate(@Param("centreId") int centreId, @Param("date") LocalDate date);
  
  @Query("SELECT s FROM Slot s WHERE s.fitnessCenterId = :centreId")
  List<Slot> getSlotsByCenter(@Param("centreId") int centreId);
}
