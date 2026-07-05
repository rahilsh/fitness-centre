package com.rsh.fitness_centre.repository;

import com.rsh.fitness_centre.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

/**
 * Repository interface for User entity.
 * Implementation uses JDBC instead of JPA.
 */
public interface UserRepository {
  
  User save(User user);

  Optional<User> findById(Long id);

  Optional<User> findByName(String name);

  Optional<User> findByEmail(String email);

  Optional<User> findWithRolesById(Long id);

  List<User> findAll();
  
  /**
   * Find all users with pagination.
   * @param pageable pagination parameters
   * @return Page of users
   */
  default Page<User> findAll(Pageable pageable) {
    List<User> all = findAll();
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
