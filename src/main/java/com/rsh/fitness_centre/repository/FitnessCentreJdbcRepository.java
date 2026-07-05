package com.rsh.fitness_centre.repository;

import com.rsh.fitness_centre.entity.FitnessCentre;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
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
public class FitnessCentreJdbcRepository implements FitnessCentreRepository {

  private static final Logger logger = LoggerFactory.getLogger(FitnessCentreJdbcRepository.class);
  private final NamedParameterJdbcTemplate jdbcTemplate;

  @Autowired
  public FitnessCentreJdbcRepository(NamedParameterJdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  private static class FitnessCentreRowMapper implements RowMapper<FitnessCentre> {
    @Override
    public FitnessCentre mapRow(ResultSet rs, int rowNum) throws SQLException {
      FitnessCentre centre = new FitnessCentre();
      centre.setId(rs.getLong("id"));
      centre.setName(rs.getString("name"));
      centre.setCreatedAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null);
      centre.setUpdatedAt(rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null);
      return centre;
    }
  }

  public FitnessCentre save(FitnessCentre centre) {
    if (centre.getId() == null) {
      // Insert
      String sql = "INSERT INTO fitness_centre (name, created_at, updated_at) " +
          "VALUES (:name, :createdAt, :updatedAt)";
      
      MapSqlParameterSource params = new MapSqlParameterSource()
          .addValue("name", centre.getName())
          .addValue("createdAt", java.sql.Timestamp.valueOf(centre.getCreatedAt() != null ? centre.getCreatedAt() : java.time.LocalDateTime.now()))
          .addValue("updatedAt", java.sql.Timestamp.valueOf(centre.getUpdatedAt() != null ? centre.getUpdatedAt() : java.time.LocalDateTime.now()));

      KeyHolder keyHolder = new GeneratedKeyHolder();
      jdbcTemplate.update(sql, params, keyHolder, new String[]{"id"});
      centre.setId(keyHolder.getKey().longValue());
      logger.info("FitnessCentre inserted with id: {}", centre.getId());
    } else {
      // Update
      String sql = "UPDATE fitness_centre SET name = :name, updated_at = :updatedAt WHERE id = :id";
      
      MapSqlParameterSource params = new MapSqlParameterSource()
          .addValue("id", centre.getId())
          .addValue("name", centre.getName())
          .addValue("updatedAt", java.sql.Timestamp.valueOf(centre.getUpdatedAt() != null ? centre.getUpdatedAt() : java.time.LocalDateTime.now()));

      jdbcTemplate.update(sql, params);
      logger.info("FitnessCentre updated with id: {}", centre.getId());
    }
    return centre;
  }

  public Optional<FitnessCentre> findById(Long id) {
    String sql = "SELECT * FROM fitness_centre WHERE id = :id";
    MapSqlParameterSource params = new MapSqlParameterSource().addValue("id", id);
    try {
      FitnessCentre centre = jdbcTemplate.queryForObject(sql, params, new FitnessCentreRowMapper());
      return Optional.of(centre);
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  public Optional<FitnessCentre> findByName(String name) {
    String sql = "SELECT * FROM fitness_centre WHERE name = :name";
    MapSqlParameterSource params = new MapSqlParameterSource().addValue("name", name);
    try {
      FitnessCentre centre = jdbcTemplate.queryForObject(sql, params, new FitnessCentreRowMapper());
      return Optional.of(centre);
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  public List<FitnessCentre> findAll() {
    String sql = "SELECT * FROM fitness_centre";
    return jdbcTemplate.query(sql, new FitnessCentreRowMapper());
  }

  public void delete(Long id) {
    String sql = "DELETE FROM fitness_centre WHERE id = :id";
    MapSqlParameterSource params = new MapSqlParameterSource().addValue("id", id);
    jdbcTemplate.update(sql, params);
    logger.info("FitnessCentre deleted with id: {}", id);
  }

  public void deleteById(Long id) {
    delete(id);
  }

  public void deleteAll() {
    String sql = "DELETE FROM fitness_centre";
    jdbcTemplate.update(sql, new MapSqlParameterSource());
    logger.info("All fitness centres deleted");
  }

  public long count() {
    String sql = "SELECT COUNT(*) FROM fitness_centre";
    Integer count = jdbcTemplate.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
    return count != null ? count : 0;
  }

  public boolean existsById(Long id) {
    String sql = "SELECT EXISTS(SELECT 1 FROM fitness_centre WHERE id = :id)";
    MapSqlParameterSource params = new MapSqlParameterSource().addValue("id", id);
    Boolean exists = jdbcTemplate.queryForObject(sql, params, Boolean.class);
    return exists != null && exists;
  }
}
