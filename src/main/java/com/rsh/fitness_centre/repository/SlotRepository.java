package com.rsh.fitness_centre.repository;

import com.rsh.fitness_centre.entity.Slot;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SlotRepository extends JpaRepository<Slot, Long> {
  
  @Query("SELECT s FROM Slot s WHERE s.fitnessCentre.id = :centreId AND s.date = :date")
  List<Slot> getSlotsByDate(@Param("centreId") Long centreId, @Param("date") LocalDate date);
  
  @Query("SELECT s FROM Slot s WHERE s.fitnessCentre.id = :centreId")
  List<Slot> getSlotsByCenter(@Param("centreId") Long centreId);
}
