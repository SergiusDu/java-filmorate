package ru.yandex.practicum.filmorate.films.infrastructure.storage.jdbc;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.common.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.films.domain.model.value.Genre;
import ru.yandex.practicum.filmorate.films.domain.port.CreateGenreCommand;
import ru.yandex.practicum.filmorate.films.domain.port.GenreRepository;
import ru.yandex.practicum.filmorate.films.domain.port.UpdateGenreCommand;

import java.util.List;
import java.util.Optional;

@Repository
@Profile("db")
@RequiredArgsConstructor
public class JdbcGenreRepository implements GenreRepository {

  private static final RowMapper<Genre> GENRE_ROW_MAPPER = (rs, rowNum) -> new Genre(rs.getLong("genre_id"),
                                                                                     rs.getString("name"));
  private final JdbcTemplate jdbcTemplate;

  @Override
  public Genre save(CreateGenreCommand command) {
    String sql = "INSERT INTO genres (name) VALUES (?);";
    return jdbcTemplate.queryForObject(sql,
                                       GENRE_ROW_MAPPER,
                                       command.name());
  }

  @Override
  public Genre update(UpdateGenreCommand command) {
    String sql = "UPDATE genres SET name = ? WHERE genre_id = ?;";
    List<Genre> results = jdbcTemplate.query(sql,
                                             GENRE_ROW_MAPPER,
                                             command.name(),
                                             command.id());
    if (results.isEmpty()) {
      throw new ResourceNotFoundException("Genre not found: " + command.id());
    }
    return results.get(0);
  }

  @Override
  public List<Genre> findAll() {
    String sql = "SELECT * FROM genres ORDER BY genre_id";
    return jdbcTemplate.query(sql,
                              GENRE_ROW_MAPPER);
  }

  @Override
  public Optional<Genre> findById(long id) {
    String sql = "SELECT * FROM genres WHERE genre_id = ?";
    List<Genre> results = jdbcTemplate.query(sql,
                                             GENRE_ROW_MAPPER,
                                             id);
    return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
  }
}