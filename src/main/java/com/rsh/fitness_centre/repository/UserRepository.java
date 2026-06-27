package com.rsh.fitness_centre.repository;

import com.rsh.fitness_centre.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  
  Optional<User> findByName(String name);

  Optional<User> findByEmail(String email);

  @EntityGraph(attributePaths = "roles")
  Optional<User> findWithRolesById(Long id);
}
