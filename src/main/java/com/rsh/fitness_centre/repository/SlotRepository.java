package com.rsh.fitness_centre.repository;

import com.rsh.fitness_centre.entity.Slot;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Slot entity.
 * Implementation uses JDBC instead of JPA.
 */
public interface SlotRepository {
  
  Slot save(Slot slot);

  Optional<Slot> findById(Long id);

  List<Slot> getSlotsByDate(Long centreId, LocalDate date);
  
  List<Slot> getSlotsByCenter(Long centreId);

  /**
   * Find slot by ID with pessimistic write lock (simulated using "SELECT ... FOR UPDATE").
   * @param id the slot ID
   * @return the slot if found
   */
  Optional<Slot> findByIdWithPessimisticLock(Long id);

  List<Slot> findAll();

  void delete(Long id);

  void deleteById(Long id);

  void deleteAll();

  long count();

  boolean existsById(Long id);
}
