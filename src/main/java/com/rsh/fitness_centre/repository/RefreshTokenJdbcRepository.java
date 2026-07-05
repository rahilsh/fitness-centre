package com.rsh.fitness_centre.repository;

import com.rsh.fitness_centre.entity.RefreshToken;
import com.rsh.fitness_centre.entity.User;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
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
public class RefreshTokenJdbcRepository implements RefreshTokenRepository {

  private static final Logger logger = LoggerFactory.getLogger(RefreshTokenJdbcRepository.class);
  private final NamedParameterJdbcTemplate jdbcTemplate;
  private final UserJdbcRepository userJdbcRepository;

  @Autowired
  public RefreshTokenJdbcRepository(NamedParameterJdbcTemplate jdbcTemplate, UserJdbcRepository userJdbcRepository) {
    this.jdbcTemplate = jdbcTemplate;
    this.userJdbcRepository = userJdbcRepository;
  }

  private class RefreshTokenRowMapper implements RowMapper<RefreshToken> {
    @Override
    public RefreshToken mapRow(ResultSet rs, int rowNum) throws SQLException {
      RefreshToken token = new RefreshToken();
      token.setId(rs.getLong("id"));
      
      Long userId = rs.getLong("user_id");
      Optional<User> user = userJdbcRepository.findById(userId);
      user.ifPresent(token::setUser);
      
      token.setToken(rs.getString("token"));
      token.setExpiryDate(rs.getTimestamp("expiry_date").toInstant());
      token.setCreatedAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toInstant() : null);
      token.setRevoked(rs.getBoolean("revoked"));
      return token;
    }
  }

  public RefreshToken save(RefreshToken token) {
    if (token.getId() == null) {
      // Insert
      String sql = "INSERT INTO refresh_token (user_id, token, expiry_date, created_at, revoked) " +
          "VALUES (:userId, :token, :expiryDate, :createdAt, :revoked)";
      
      MapSqlParameterSource params = new MapSqlParameterSource()
          .addValue("userId", token.getUser().getId())
          .addValue("token", token.getToken())
          .addValue("expiryDate", java.sql.Timestamp.from(token.getExpiryDate()))
          .addValue("createdAt", java.sql.Timestamp.from(token.getCreatedAt() != null ? token.getCreatedAt() : Instant.now()))
          .addValue("revoked", token.isRevoked());

      KeyHolder keyHolder = new GeneratedKeyHolder();
      jdbcTemplate.update(sql, params, keyHolder, new String[]{"id"});
      token.setId(keyHolder.getKey().longValue());
      logger.info("RefreshToken inserted with id: {}", token.getId());
    } else {
      // Update
      String sql = "UPDATE refresh_token SET user_id = :userId, token = :token, expiry_date = :expiryDate, revoked = :revoked WHERE id = :id";
      
      MapSqlParameterSource params = new MapSqlParameterSource()
          .addValue("id", token.getId())
          .addValue("userId", token.getUser().getId())
          .addValue("token", token.getToken())
          .addValue("expiryDate", java.sql.Timestamp.from(token.getExpiryDate()))
          .addValue("revoked", token.isRevoked());

      jdbcTemplate.update(sql, params);
      logger.info("RefreshToken updated with id: {}", token.getId());
    }
    return token;
  }

  public Optional<RefreshToken> findById(Long id) {
    String sql = "SELECT * FROM refresh_token WHERE id = :id";
    MapSqlParameterSource params = new MapSqlParameterSource().addValue("id", id);
    try {
      RefreshToken token = jdbcTemplate.queryForObject(sql, params, new RefreshTokenRowMapper());
      return Optional.of(token);
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  public Optional<RefreshToken> findByToken(String token) {
    String sql = "SELECT * FROM refresh_token WHERE token = :token";
    MapSqlParameterSource params = new MapSqlParameterSource().addValue("token", token);
    try {
      RefreshToken refreshToken = jdbcTemplate.queryForObject(sql, params, new RefreshTokenRowMapper());
      return Optional.of(refreshToken);
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  public void deleteByUser(User user) {
    String sql = "DELETE FROM refresh_token WHERE user_id = :userId";
    MapSqlParameterSource params = new MapSqlParameterSource().addValue("userId", user.getId());
    int deleted = jdbcTemplate.update(sql, params);
    logger.info("Deleted {} refresh tokens for user: {}", deleted, user.getId());
  }

  public void deleteByExpiryDateBefore(Instant now) {
    String sql = "DELETE FROM refresh_token WHERE expiry_date < :now";
    MapSqlParameterSource params = new MapSqlParameterSource().addValue("now", java.sql.Timestamp.from(now));
    int deleted = jdbcTemplate.update(sql, params);
    logger.info("Deleted {} expired refresh tokens before: {}", deleted, now);
  }

  public List<RefreshToken> findAll() {
    String sql = "SELECT * FROM refresh_token";
    return jdbcTemplate.query(sql, new RefreshTokenRowMapper());
  }

  public void delete(RefreshToken token) {
    String sql = "DELETE FROM refresh_token WHERE id = :id";
    MapSqlParameterSource params = new MapSqlParameterSource().addValue("id", token.getId());
    jdbcTemplate.update(sql, params);
    logger.info("RefreshToken deleted with id: {}", token.getId());
  }

  public void deleteById(Long id) {
    String sql = "DELETE FROM refresh_token WHERE id = :id";
    MapSqlParameterSource params = new MapSqlParameterSource().addValue("id", id);
    jdbcTemplate.update(sql, params);
    logger.info("RefreshToken deleted with id: {}", id);
  }

  public void deleteAll() {
    String sql = "DELETE FROM refresh_token";
    jdbcTemplate.update(sql, new MapSqlParameterSource());
    logger.info("All refresh tokens deleted");
  }

  public long count() {
    String sql = "SELECT COUNT(*) FROM refresh_token";
    Integer count = jdbcTemplate.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
    return count != null ? count : 0;
  }

  public boolean existsById(Long id) {
    String sql = "SELECT EXISTS(SELECT 1 FROM refresh_token WHERE id = :id)";
    MapSqlParameterSource params = new MapSqlParameterSource().addValue("id", id);
    Boolean exists = jdbcTemplate.queryForObject(sql, params, Boolean.class);
    return exists != null && exists;
  }
}
