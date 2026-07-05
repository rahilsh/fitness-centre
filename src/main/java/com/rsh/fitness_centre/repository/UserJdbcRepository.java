package com.rsh.fitness_centre.repository;

import com.rsh.fitness_centre.entity.User;
import com.rsh.fitness_centre.entity.UserRole;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class UserJdbcRepository implements UserRepository {

  private static final Logger logger = LoggerFactory.getLogger(UserJdbcRepository.class);
  private final NamedParameterJdbcTemplate jdbcTemplate;

  @Autowired
  public UserJdbcRepository(NamedParameterJdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  private static class UserRowMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
      User user = new User();
      user.setId(rs.getLong("id"));
      user.setName(rs.getString("name"));
      user.setEmail(rs.getString("email"));
      user.setPasswordHash(rs.getString("password_hash"));
      user.setEnabled(rs.getBoolean("enabled"));
      user.setCreatedAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null);
      user.setUpdatedAt(rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null);
      user.setLastLogin(rs.getTimestamp("last_login") != null ? rs.getTimestamp("last_login").toLocalDateTime() : null);
      return user;
    }
  }

  public User save(User user) {
    if (user.getId() == null) {
      // Insert
      String sql = "INSERT INTO app_user (name, email, password_hash, enabled, created_at, updated_at, last_login) " +
          "VALUES (:name, :email, :passwordHash, :enabled, :createdAt, :updatedAt, :lastLogin)";
      
      MapSqlParameterSource params = new MapSqlParameterSource()
          .addValue("name", user.getName())
          .addValue("email", user.getEmail())
          .addValue("passwordHash", user.getPasswordHash())
          .addValue("enabled", user.isEnabled())
          .addValue("createdAt", java.sql.Timestamp.valueOf(user.getCreatedAt() != null ? user.getCreatedAt() : java.time.LocalDateTime.now()))
          .addValue("updatedAt", java.sql.Timestamp.valueOf(user.getUpdatedAt() != null ? user.getUpdatedAt() : java.time.LocalDateTime.now()))
          .addValue("lastLogin", user.getLastLogin() != null ? java.sql.Timestamp.valueOf(user.getLastLogin()) : null);

      KeyHolder keyHolder = new GeneratedKeyHolder();
      jdbcTemplate.update(sql, params, keyHolder, new String[]{"id"});
      user.setId(keyHolder.getKey().longValue());

      // Save roles
      if (user.getRoles() != null && !user.getRoles().isEmpty()) {
        for (UserRole role : user.getRoles()) {
          String roleSql = "INSERT INTO user_roles (user_id, role) VALUES (:userId, :role)";
          MapSqlParameterSource roleParams = new MapSqlParameterSource()
              .addValue("userId", user.getId())
              .addValue("role", role.name());
          jdbcTemplate.update(roleSql, roleParams);
        }
      }
      logger.info("User inserted with id: {}", user.getId());
    } else {
      // Update
      String sql = "UPDATE app_user SET name = :name, email = :email, password_hash = :passwordHash, " +
          "enabled = :enabled, updated_at = :updatedAt, last_login = :lastLogin WHERE id = :id";
      
      MapSqlParameterSource params = new MapSqlParameterSource()
          .addValue("id", user.getId())
          .addValue("name", user.getName())
          .addValue("email", user.getEmail())
          .addValue("passwordHash", user.getPasswordHash())
          .addValue("enabled", user.isEnabled())
          .addValue("updatedAt", java.sql.Timestamp.valueOf(user.getUpdatedAt() != null ? user.getUpdatedAt() : java.time.LocalDateTime.now()))
          .addValue("lastLogin", user.getLastLogin() != null ? java.sql.Timestamp.valueOf(user.getLastLogin()) : null);

      jdbcTemplate.update(sql, params);

      // Update roles: delete old and insert new
      jdbcTemplate.update("DELETE FROM user_roles WHERE user_id = :userId", new MapSqlParameterSource().addValue("userId", user.getId()));
      if (user.getRoles() != null && !user.getRoles().isEmpty()) {
        for (UserRole role : user.getRoles()) {
          String roleSql = "INSERT INTO user_roles (user_id, role) VALUES (:userId, :role)";
          MapSqlParameterSource roleParams = new MapSqlParameterSource()
              .addValue("userId", user.getId())
              .addValue("role", role.name());
          jdbcTemplate.update(roleSql, roleParams);
        }
      }
      logger.info("User updated with id: {}", user.getId());
    }
    return user;
  }

  public Optional<User> findById(Long id) {
    String sql = "SELECT * FROM app_user WHERE id = :id";
    MapSqlParameterSource params = new MapSqlParameterSource().addValue("id", id);
    try {
      User user = jdbcTemplate.queryForObject(sql, params, new UserRowMapper());
      if (user != null) {
        user.setRoles(findUserRoles(id));
      }
      return Optional.of(user);
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  public Optional<User> findByName(String name) {
    String sql = "SELECT * FROM app_user WHERE name = :name";
    MapSqlParameterSource params = new MapSqlParameterSource().addValue("name", name);
    try {
      User user = jdbcTemplate.queryForObject(sql, params, new UserRowMapper());
      if (user != null) {
        user.setRoles(findUserRoles(user.getId()));
      }
      return Optional.of(user);
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  public Optional<User> findByEmail(String email) {
    String sql = "SELECT * FROM app_user WHERE email = :email";
    MapSqlParameterSource params = new MapSqlParameterSource().addValue("email", email);
    try {
      User user = jdbcTemplate.queryForObject(sql, params, new UserRowMapper());
      if (user != null) {
        user.setRoles(findUserRoles(user.getId()));
      }
      return Optional.of(user);
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  public Optional<User> findWithRolesById(Long id) {
    // Same as findById but explicitly eager-load roles
    return findById(id);
  }

  public List<User> findAll() {
    String sql = "SELECT * FROM app_user";
    List<User> users = jdbcTemplate.query(sql, new UserRowMapper());
    for (User user : users) {
      user.setRoles(findUserRoles(user.getId()));
    }
    return users;
  }

  public void delete(Long id) {
    // Delete roles first
    jdbcTemplate.update("DELETE FROM user_roles WHERE user_id = :userId", new MapSqlParameterSource().addValue("userId", id));
    // Then delete user
    String sql = "DELETE FROM app_user WHERE id = :id";
    MapSqlParameterSource params = new MapSqlParameterSource().addValue("id", id);
    jdbcTemplate.update(sql, params);
    logger.info("User deleted with id: {}", id);
  }

  public void deleteById(Long id) {
    delete(id);
  }

  public void deleteAll() {
    // Delete all user roles first
    jdbcTemplate.update("DELETE FROM user_roles", new MapSqlParameterSource());
    // Then delete all users
    String sql = "DELETE FROM app_user";
    jdbcTemplate.update(sql, new MapSqlParameterSource());
    logger.info("All users deleted");
  }

  public long count() {
    String sql = "SELECT COUNT(*) FROM app_user";
    Integer count = jdbcTemplate.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
    return count != null ? count : 0;
  }

  public boolean existsById(Long id) {
    String sql = "SELECT EXISTS(SELECT 1 FROM app_user WHERE id = :id)";
    MapSqlParameterSource params = new MapSqlParameterSource().addValue("id", id);
    Boolean exists = jdbcTemplate.queryForObject(sql, params, Boolean.class);
    return exists != null && exists;
  }

  private Set<UserRole> findUserRoles(Long userId) {
    String sql = "SELECT role FROM user_roles WHERE user_id = :userId";
    MapSqlParameterSource params = new MapSqlParameterSource().addValue("userId", userId);
    List<String> roles = jdbcTemplate.queryForList(sql, params, String.class);
    Set<UserRole> roleSet = new HashSet<>();
    for (String role : roles) {
      roleSet.add(UserRole.valueOf(role));
    }
    return roleSet;
  }
}
