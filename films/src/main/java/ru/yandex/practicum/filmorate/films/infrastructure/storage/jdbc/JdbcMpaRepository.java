package ru.yandex.practicum.filmorate.films.infrastructure.storage.jdbc;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.common.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.films.domain.model.value.Mpa;
import ru.yandex.practicum.filmorate.films.domain.port.CreateMpaCommand;
import ru.yandex.practicum.filmorate.films.domain.port.MpaRepository;
import ru.yandex.practicum.filmorate.films.domain.port.UpdateMpaCommand;

import java.util.List;
import java.util.Optional;

@Repository
@Profile("db")
@RequiredArgsConstructor
public class JdbcMpaRepository implements MpaRepository {
  private static final RowMapper<Mpa> MAP_ROW_MAPPER = ((rs, rowNum) -> new Mpa(rs.getLong("mpa_id"),
                                                                                rs.getString("name")));

  private final JdbcTemplate jdbcTemplate;

  @Override
  public Mpa save(CreateMpaCommand command) {
    String sql = "INSERT INTO mpa_ratings (name) VALUES (?);";
    return jdbcTemplate.queryForObject(sql,
                                       MAP_ROW_MAPPER,
                                       command.name());
  }

  @Override
  public Mpa update(UpdateMpaCommand command) {
    String sql = "UPDATE mpa_ratings SET name = ? WHERE mpa_id = ?;";
    List<Mpa> results = jdbcTemplate.query(sql,
                                           MAP_ROW_MAPPER,
                                           command.name(),
                                           command.id());
    if (results.isEmpty()) {
      throw new ResourceNotFoundException("MPA mpa not found: " + command.id());
    }
    return results.get(0);
  }

  public List<Mpa> findAll() {
    String sql = "SELECT * FROM mpa_ratings ORDER BY mpa_id";
    return jdbcTemplate.query(sql,
                              MAP_ROW_MAPPER);
  }

  @Override
  public Optional<Mpa> findById(long id) {
    String sql = "SELECT * FROM mpa_ratings WHERE mpa_id = ?";
    List<Mpa> results = jdbcTemplate.query(sql,
                                           MAP_ROW_MAPPER,
                                           id);
    return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
  }
}
