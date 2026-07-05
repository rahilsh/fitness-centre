package com.rsh.fitness_centre.repository;

import com.rsh.fitness_centre.entity.Booking;
import com.rsh.fitness_centre.entity.BookingStatus;
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
public class BookingJdbcRepository implements BookingRepository {

  private static final Logger logger = LoggerFactory.getLogger(BookingJdbcRepository.class);
  private final NamedParameterJdbcTemplate jdbcTemplate;
  private final UserJdbcRepository userJdbcRepository;
  private final SlotJdbcRepository slotJdbcRepository;

  @Autowired
  public BookingJdbcRepository(NamedParameterJdbcTemplate jdbcTemplate, 
      UserJdbcRepository userJdbcRepository,
      SlotJdbcRepository slotJdbcRepository) {
    this.jdbcTemplate = jdbcTemplate;
    this.userJdbcRepository = userJdbcRepository;
    this.slotJdbcRepository = slotJdbcRepository;
  }

  private class BookingRowMapper implements RowMapper<Booking> {
    @Override
    public Booking mapRow(ResultSet rs, int rowNum) throws SQLException {
      Booking booking = new Booking();
      booking.setId(rs.getLong("id"));
      
      Long userId = rs.getLong("user_id");
      userJdbcRepository.findById(userId).ifPresent(booking::setUser);
      
      Long slotId = rs.getLong("slot_id");
      slotJdbcRepository.findById(slotId).ifPresent(booking::setSlot);
      
      booking.setBookedAt(rs.getTimestamp("booked_at") != null ? rs.getTimestamp("booked_at").toLocalDateTime() : null);
      booking.setUpdatedAt(rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null);
      booking.setStatus(BookingStatus.valueOf(rs.getString("status")));
      return booking;
    }
  }

  public Booking save(Booking booking) {
    if (booking.getId() == null) {
      // Insert
      String sql = "INSERT INTO booking (user_id, slot_id, booked_at, updated_at, status) " +
          "VALUES (:userId, :slotId, :bookedAt, :updatedAt, :status)";
      
      MapSqlParameterSource params = new MapSqlParameterSource()
          .addValue("userId", booking.getUser().getId())
          .addValue("slotId", booking.getSlot().getId())
          .addValue("bookedAt", java.sql.Timestamp.valueOf(booking.getBookedAt() != null ? booking.getBookedAt() : java.time.LocalDateTime.now()))
          .addValue("updatedAt", java.sql.Timestamp.valueOf(booking.getUpdatedAt() != null ? booking.getUpdatedAt() : java.time.LocalDateTime.now()))
          .addValue("status", booking.getStatus().name());

      KeyHolder keyHolder = new GeneratedKeyHolder();
      jdbcTemplate.update(sql, params, keyHolder, new String[]{"id"});
      booking.setId(keyHolder.getKey().longValue());
      logger.info("Booking inserted with id: {}", booking.getId());
    } else {
      // Update
      String sql = "UPDATE booking SET user_id = :userId, slot_id = :slotId, booked_at = :bookedAt, updated_at = :updatedAt, status = :status WHERE id = :id";
      
      MapSqlParameterSource params = new MapSqlParameterSource()
          .addValue("id", booking.getId())
          .addValue("userId", booking.getUser().getId())
          .addValue("slotId", booking.getSlot().getId())
          .addValue("bookedAt", java.sql.Timestamp.valueOf(booking.getBookedAt() != null ? booking.getBookedAt() : java.time.LocalDateTime.now()))
          .addValue("updatedAt", java.sql.Timestamp.valueOf(booking.getUpdatedAt() != null ? booking.getUpdatedAt() : java.time.LocalDateTime.now()))
          .addValue("status", booking.getStatus().name());

      jdbcTemplate.update(sql, params);
      logger.info("Booking updated with id: {}", booking.getId());
    }
    return booking;
  }

  public Optional<Booking> findById(Long id) {
    String sql = "SELECT * FROM booking WHERE id = :id";
    MapSqlParameterSource params = new MapSqlParameterSource().addValue("id", id);
    try {
      Booking booking = jdbcTemplate.queryForObject(sql, params, new BookingRowMapper());
      return Optional.of(booking);
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  public List<Booking> getBookingsByUser(Long userId) {
    String sql = "SELECT * FROM booking WHERE user_id = :userId";
    MapSqlParameterSource params = new MapSqlParameterSource().addValue("userId", userId);
    return jdbcTemplate.query(sql, params, new BookingRowMapper());
  }

  public List<Booking> getBookingsByCentre(Long centreId) {
    String sql = "SELECT b.* FROM booking b " +
        "JOIN slot s ON b.slot_id = s.id " +
        "WHERE s.fitness_centre_id = :centreId";
    MapSqlParameterSource params = new MapSqlParameterSource().addValue("centreId", centreId);
    return jdbcTemplate.query(sql, params, new BookingRowMapper());
  }

  public List<Booking> findAll() {
    String sql = "SELECT * FROM booking";
    return jdbcTemplate.query(sql, new BookingRowMapper());
  }

  public void delete(Long id) {
    String sql = "DELETE FROM booking WHERE id = :id";
    MapSqlParameterSource params = new MapSqlParameterSource().addValue("id", id);
    jdbcTemplate.update(sql, params);
    logger.info("Booking deleted with id: {}", id);
  }

  public void deleteById(Long id) {
    delete(id);
  }

  public void deleteAll() {
    String sql = "DELETE FROM booking";
    jdbcTemplate.update(sql, new MapSqlParameterSource());
    logger.info("All bookings deleted");
  }

  public long count() {
    String sql = "SELECT COUNT(*) FROM booking";
    Integer count = jdbcTemplate.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
    return count != null ? count : 0;
  }

  public boolean existsById(Long id) {
    String sql = "SELECT EXISTS(SELECT 1 FROM booking WHERE id = :id)";
    MapSqlParameterSource params = new MapSqlParameterSource().addValue("id", id);
    Boolean exists = jdbcTemplate.queryForObject(sql, params, Boolean.class);
    return exists != null && exists;
  }
}
