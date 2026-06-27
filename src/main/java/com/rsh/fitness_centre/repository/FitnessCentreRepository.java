package com.rsh.fitness_centre.repository;

import com.rsh.fitness_centre.entity.FitnessCentre;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FitnessCentreRepository extends JpaRepository<FitnessCentre, Long> {
  
  Optional<FitnessCentre> findByName(String name);
}
