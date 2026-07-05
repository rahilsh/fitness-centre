package com.rsh.fitness_centre.repository;

import com.rsh.fitness_centre.entity.FitnessCentre;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

/**
 * Repository interface for FitnessCentre entity.
 * Implementation uses JDBC instead of JPA.
 */
public interface FitnessCentreRepository {
  
  FitnessCentre save(FitnessCentre centre);

  Optional<FitnessCentre> findById(Long id);

  Optional<FitnessCentre> findByName(String name);

  List<FitnessCentre> findAll();
  
  /**
   * Find all fitness centres with pagination.
   * @param pageable pagination parameters
   * @return Page of fitness centres
   */
  default Page<FitnessCentre> findAll(Pageable pageable) {
    List<FitnessCentre> all = findAll();
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
