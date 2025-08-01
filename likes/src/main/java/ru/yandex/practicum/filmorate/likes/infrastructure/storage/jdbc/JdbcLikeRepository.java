package ru.yandex.practicum.filmorate.likes.infrastructure.storage.jdbc;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.common.exception.ValidationException;
import ru.yandex.practicum.filmorate.likes.domain.port.LikeRepository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Profile("db")
public class JdbcLikeRepository implements LikeRepository {

  private static final RowMapper<Map.Entry<Long, Long>> LIKE_ROW_MAPPER = (rs, rowNum) -> new AbstractMap.SimpleEntry<>(
      rs.getLong("user_id"), rs.getLong("film_id"));

  private final JdbcTemplate jdbcTemplate;

  @Override
  public boolean addLike(long filmId, long userId) {
    String sql = "INSERT INTO likes (film_id, user_id) VALUES(?, ?)";
    try {
      jdbcTemplate.update(sql, filmId, userId);
      return true;
    } catch (DuplicateKeyException e) {
      return false;
    }
  }

  @Override
  public boolean removeLike(long filmId, long userId) {
    String sql = "DELETE FROM likes WHERE user_id = ? AND film_id = ?";
    return jdbcTemplate.update(sql, userId, filmId) > 0;
  }

  @Override
  public LinkedHashSet<Long> getPopularFilmIds(int count) {
    String sql = """
        SELECT film_id
        FROM likes
        GROUP BY film_id
        ORDER BY COUNT(user_id) DESC
        LIMIT ?
    """;
    List<Long> popularIds = jdbcTemplate.queryForList(sql, Long.class, count);
    return new LinkedHashSet<>(popularIds);
  }

  @Override
  public Set<Long> findUsersWhoLikedFilm(long filmId) {
    String sql = "SELECT user_id FROM likes WHERE film_id = ?";
    List<Long> userIds = jdbcTemplate.queryForList(sql, Long.class, filmId);
    return new HashSet<>(userIds);
  }

  @Override
  public boolean contains(long filmId) {
    String sql = "SELECT COUNT(*) FROM likes WHERE film_id = ?";
    Integer count = jdbcTemplate.queryForObject(sql, Integer.class, filmId);
    return count != null && count > 0;
  }

  @Override
  public Set<Long> findLikedFilms(long userId) {
    String sql = "SELECT film_id FROM likes WHERE user_id = ?";
    List<Long> filmIds = jdbcTemplate.queryForList(sql, Long.class, userId);
    return new HashSet<>(filmIds);
  }

  @Override
  public void deleteByFilmId(long filmId) {
    jdbcTemplate.update("DELETE FROM likes WHERE film_id = ?", filmId);
  }

  @Override
  public void deleteByUserId(long userId) {
    jdbcTemplate.update("DELETE FROM likes WHERE user_id = ?", userId);
  }

  @Override
  public Map<Long, Long> getLikeCounts() {
    String sql = "SELECT film_id, COUNT(*) AS cnt FROM likes GROUP BY film_id";
    return jdbcTemplate.query(sql, rs -> {
      Map<Long, Long> counts = new HashMap<>();
      while (rs.next()) {
        counts.put(rs.getLong("film_id"), rs.getLong("cnt"));
      }
      return counts;
    });
  }
}