package com.rsh.fitness_centre.repository;

import com.rsh.fitness_centre.entity.Activity;
import com.rsh.fitness_centre.entity.Slot;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
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
public class SlotJdbcRepository implements SlotRepository {

  private static final Logger logger = LoggerFactory.getLogger(SlotJdbcRepository.class);
  private final NamedParameterJdbcTemplate jdbcTemplate;
  private final FitnessCentreJdbcRepository fitnessCentreJdbcRepository;

  @Autowired
  public SlotJdbcRepository(NamedParameterJdbcTemplate jdbcTemplate, FitnessCentreJdbcRepository fitnessCentreJdbcRepository) {
    this.jdbcTemplate = jdbcTemplate;
    this.fitnessCentreJdbcRepository = fitnessCentreJdbcRepository;
  }

  private class SlotRowMapper implements RowMapper<Slot> {
    @Override
    public Slot mapRow(ResultSet rs, int rowNum) throws SQLException {
      Slot slot = new Slot();
      slot.setId(rs.getLong("id"));
      slot.setDate(rs.getDate("slot_date").toLocalDate());
      slot.setActivity(Activity.valueOf(rs.getString("activity")));
      slot.setStartTime(rs.getInt("start_time"));
      slot.setEndTime(rs.getInt("end_time"));
      slot.setNoOfSeats(rs.getInt("no_of_seats"));
      slot.setCreatedAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null);
      slot.setUpdatedAt(rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null);
      
      Long centreId = rs.getLong("fitness_centre_id");
      fitnessCentreJdbcRepository.findById(centreId).ifPresent(slot::setFitnessCentre);
      
      return slot;
    }
  }

  public Slot save(Slot slot) {
    if (slot.getId() == null) {
      // Insert
      String sql = "INSERT INTO slot (fitness_centre_id, slot_date, activity, start_time, end_time, no_of_seats, created_at, updated_at) " +
          "VALUES (:fitnessCentreId, :date, :activity, :startTime, :endTime, :noOfSeats, :createdAt, :updatedAt)";
      
      MapSqlParameterSource params = new MapSqlParameterSource()
          .addValue("fitnessCentreId", slot.getFitnessCentre().getId())
          .addValue("date", java.sql.Date.valueOf(slot.getDate()))
          .addValue("activity", slot.getActivity().name())
          .addValue("startTime", slot.getStartTime())
          .addValue("endTime", slot.getEndTime())
          .addValue("noOfSeats", slot.getNoOfSeats())
          .addValue("createdAt", java.sql.Timestamp.valueOf(slot.getCreatedAt() != null ? slot.getCreatedAt() : java.time.LocalDateTime.now()))
          .addValue("updatedAt", java.sql.Timestamp.valueOf(slot.getUpdatedAt() != null ? slot.getUpdatedAt() : java.time.LocalDateTime.now()));

      KeyHolder keyHolder = new GeneratedKeyHolder();
      jdbcTemplate.update(sql, params, keyHolder, new String[]{"id"});
      slot.setId(keyHolder.getKey().longValue());
      logger.info("Slot inserted with id: {}", slot.getId());
    } else {
      // Update
      String sql = "UPDATE slot SET fitness_centre_id = :fitnessCentreId, slot_date = :date, activity = :activity, " +
          "start_time = :startTime, end_time = :endTime, no_of_seats = :noOfSeats, updated_at = :updatedAt WHERE id = :id";
      
      MapSqlParameterSource params = new MapSqlParameterSource()
          .addValue("id", slot.getId())
          .addValue("fitnessCentreId", slot.getFitnessCentre().getId())
          .addValue("date", java.sql.Date.valueOf(slot.getDate()))
          .addValue("activity", slot.getActivity().name())
          .addValue("startTime", slot.getStartTime())
          .addValue("endTime", slot.getEndTime())
          .addValue("noOfSeats", slot.getNoOfSeats())
          .addValue("updatedAt", java.sql.Timestamp.valueOf(slot.getUpdatedAt() != null ? slot.getUpdatedAt() : java.time.LocalDateTime.now()));

      jdbcTemplate.update(sql, params);
      logger.info("Slot updated with id: {}", slot.getId());
    }
    return slot;
  }

  public Optional<Slot> findById(Long id) {
    String sql = "SELECT * FROM slot WHERE id = :id";
    MapSqlParameterSource params = new MapSqlParameterSource().addValue("id", id);
    try {
      Slot slot = jdbcTemplate.queryForObject(sql, params, new SlotRowMapper());
      return Optional.of(slot);
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  public List<Slot> getSlotsByDate(Long centreId, LocalDate date) {
    String sql = "SELECT * FROM slot WHERE fitness_centre_id = :centreId AND slot_date = :date";
    MapSqlParameterSource params = new MapSqlParameterSource()
        .addValue("centreId", centreId)
        .addValue("date", java.sql.Date.valueOf(date));
    return jdbcTemplate.query(sql, params, new SlotRowMapper());
  }

  public List<Slot> getSlotsByCenter(Long centreId) {
    String sql = "SELECT * FROM slot WHERE fitness_centre_id = :centreId";
    MapSqlParameterSource params = new MapSqlParameterSource().addValue("centreId", centreId);
    return jdbcTemplate.query(sql, params, new SlotRowMapper());
  }

  public Optional<Slot> findByIdWithPessimisticLock(Long id) {
    // Simulate pessimistic lock with "FOR UPDATE"
    String sql = "SELECT * FROM slot WHERE id = :id FOR UPDATE";
    MapSqlParameterSource params = new MapSqlParameterSource().addValue("id", id);
    try {
      Slot slot = jdbcTemplate.queryForObject(sql, params, new SlotRowMapper());
      return Optional.of(slot);
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  public List<Slot> findAll() {
    String sql = "SELECT * FROM slot";
    return jdbcTemplate.query(sql, new SlotRowMapper());
  }

  public void delete(Long id) {
    String sql = "DELETE FROM slot WHERE id = :id";
    MapSqlParameterSource params = new MapSqlParameterSource().addValue("id", id);
    jdbcTemplate.update(sql, params);
    logger.info("Slot deleted with id: {}", id);
  }

  public void deleteById(Long id) {
    delete(id);
  }

  public void deleteAll() {
    String sql = "DELETE FROM slot";
    jdbcTemplate.update(sql, new MapSqlParameterSource());
    logger.info("All slots deleted");
  }

  public long count() {
    String sql = "SELECT COUNT(*) FROM slot";
    Integer count = jdbcTemplate.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
    return count != null ? count : 0;
  }

  public boolean existsById(Long id) {
    String sql = "SELECT EXISTS(SELECT 1 FROM slot WHERE id = :id)";
    MapSqlParameterSource params = new MapSqlParameterSource().addValue("id", id);
    Boolean exists = jdbcTemplate.queryForObject(sql, params, Boolean.class);
    return exists != null && exists;
  }
}
