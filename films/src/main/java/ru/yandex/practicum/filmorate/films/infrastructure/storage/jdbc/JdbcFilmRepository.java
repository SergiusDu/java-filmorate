package ru.yandex.practicum.filmorate.films.infrastructure.storage.jdbc;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.common.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.films.domain.factory.FilmFactory;
import ru.yandex.practicum.filmorate.films.domain.model.Film;
import ru.yandex.practicum.filmorate.films.domain.model.value.Genre;
import ru.yandex.practicum.filmorate.films.domain.model.value.Mpa;
import ru.yandex.practicum.filmorate.films.domain.port.CreateFilmCommand;
import ru.yandex.practicum.filmorate.films.domain.port.FilmRepository;
import ru.yandex.practicum.filmorate.films.domain.port.UpdateFilmCommand;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Profile("db")
@RequiredArgsConstructor
public class JdbcFilmRepository
    implements FilmRepository {

  private static final String BASE_FILM_QUERY =
      "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, m.mpa_id, m.name as mpa_name " +
      "FROM films AS f JOIN mpa_ratings AS m ON f.mpa_id = m.mpa_id";

  private final JdbcTemplate jdbcTemplate;
  private final FilmFactory filmFactory;

  @Override
  public Film save(CreateFilmCommand command) {
    SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("films")
                                            .usingGeneratedKeyColumns("film_id");

    Map<String, Object> params = Map.of("name",
                                        command.name(),
                                        "description",
                                        command.description(),
                                        "release_date",
                                        command.releaseDate(),
                                        "duration",
                                        command.duration(),
                                        "mpa_id",
                                        command.mpa());

    long filmId = simpleJdbcInsert.executeAndReturnKey(params)
                                  .longValue();
    updateFilmGenres(filmId, command.genres());

    return findById(filmId).orElseThrow(() -> new IllegalStateException(
        "Film with id " + filmId + " not found after save"));
  }

  @Override
  public Film update(UpdateFilmCommand command) {
    String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? " +
                 "WHERE film_id = ?";
    int rowsAffected = jdbcTemplate.update(sql,
                                           command.name(),
                                           command.description(),
                                           command.releaseDate(),
                                           command.duration(),
                                           command.mpa(),
                                           command.id());

    if (rowsAffected == 0) {
      throw new ResourceNotFoundException("Film with id " + command.id() + " not found.");
    }

    updateFilmGenres(command.id(), command.genres());
    return findById(command.id()).orElseThrow(() -> new IllegalStateException(
        "Film with id " + command.id() + " not found after update"));
  }

  @Override
  public List<Film> findAll() {
    return mapRowsToFilms(jdbcTemplate.queryForList(BASE_FILM_QUERY));
  }

  @Override
  public Optional<Film> findById(long id) {
    String sql = BASE_FILM_QUERY + " WHERE f.film_id = ?";
    List<Film> films = mapRowsToFilms(jdbcTemplate.queryForList(sql, id));
    return films.stream()
                .findFirst();
  }

  @Override
  public List<Film> getByIds(List<Long> ids) {
    if (ids == null || ids.isEmpty()) {
      return List.of();
    }
    String inSql = String.join(",", Collections.nCopies(ids.size(), "?"));
    String sql = String.format(BASE_FILM_QUERY + " WHERE f.film_id IN (%s)", inSql);
    List<Film> unsortedFilms = mapRowsToFilms(jdbcTemplate.queryForList(sql, ids.toArray()));

    Map<Long, Film> filmMap = unsortedFilms.stream()
                                           .collect(Collectors.toMap(Film::id, film -> film));

    return ids.stream()
              .map(filmMap::get)
              .filter(Objects::nonNull)
              .toList();
  }

  @Override
  public List<Long> findFilmIdsByFilters(Long genreId, Integer year) {
    StringBuilder sqlBuilder = new StringBuilder("SELECT f.film_id FROM films f ");
    List<Object> params = new ArrayList<>();

    if (genreId != null) {
      sqlBuilder.append("JOIN film_genres fg ON f.film_id = fg.film_id WHERE fg.genre_id = ? ");
      params.add(genreId);
    }

    if (year != null) {
      sqlBuilder.append(genreId == null ? "WHERE " : "AND ");
      sqlBuilder.append("EXTRACT(YEAR FROM f.release_date) = ?");
      params.add(year);
    }

    return jdbcTemplate.queryForList(sqlBuilder.toString(), Long.class, params.toArray());
  }

  private void updateFilmGenres(long filmId, Set<Genre> genres) {
    jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", filmId);
    if (genres != null && !genres.isEmpty()) {
      List<Object[]> batchArgs = genres.stream()
                                       .map(genre -> new Object[] {filmId,
                                                                   genre.id()
                                       })
                                       .toList();
      jdbcTemplate.batchUpdate("INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)", batchArgs);
    }
  }

  private List<Film> mapRowsToFilms(List<Map<String, Object>> rows) {
    if (rows.isEmpty()) {
      return List.of();
    }

    Set<Long> filmIds = rows.stream()
                            .map(row -> (Long) row.get("film_id"))
                            .collect(Collectors.toSet());

    Map<Long, Set<Genre>> genresByFilmId = getGenresForFilmIds(filmIds);

    return rows.stream()
               .map(row -> {
                 long filmId = (Long) row.get("film_id");
                 Set<Genre> genres = genresByFilmId.getOrDefault(filmId, Set.of());

                 return Film.builder()
                            .id(filmId)
                            .name((String) row.get("name"))
                            .description((String) row.get("description"))
                            .releaseDate(((java.sql.Date) row.get("release_date")).toLocalDate())
                            .duration(Duration.ofMinutes((Integer) row.get("duration")))
                            .genres(genres)
                            .mpa(new Mpa((Long) row.get("mpa_id"), (String) row.get("mpa_name")))
                            .build();
               })
               .toList();
  }

  private Map<Long, Set<Genre>> getGenresForFilmIds(Set<Long> filmIds) {
    if (filmIds.isEmpty()) {
      return Map.of();
    }
    Map<Long, Set<Genre>> genresByFilmId = new HashMap<>();
    String inSql = String.join(",", Collections.nCopies(filmIds.size(), "?"));
    String sql = "SELECT fg.film_id, g.genre_id, g.name " +
                 "FROM film_genres AS fg JOIN genres AS g ON fg.genre_id = g.genre_id " + "WHERE fg.film_id IN (" +
                 inSql + ") ORDER BY g.genre_id ASC";

    jdbcTemplate.query(sql, rs -> {
      long filmId = rs.getLong("film_id");
      Genre genre = new Genre(rs.getLong("genre_id"), rs.getString("name"));
      genresByFilmId.computeIfAbsent(filmId, k -> new LinkedHashSet<>())
                    .add(genre);
    }, filmIds.toArray());

    return genresByFilmId;
  }

  public List<Film> findFilmsByGenreIdAndYear(Long genreId, Integer year, Integer count) {
    List<Film> filmsList = new ArrayList<>();

    String sqlFirstPart = "SELECT f.* " + "FROM likes l " + "JOIN films f ON l.film_id = f.film_id " +
                          "JOIN film_genres fg ON f.film_id = fg.film_id " +
                          "JOIN genres g ON g.genre_id = fg.genre_id\n";

    String sqlSecondPart = "GROUP BY l.film_id " + "ORDER BY COUNT(l.user_id) DESC LIMIT ?";

    String sql = "";

    if (genreId != null && year != null) {
      sql = sqlFirstPart + "WHERE g.genre_id = ? AND EXTRACT(YEAR FROM f.release_date) = ? \n" + sqlSecondPart;
      filmsList = mapRowsToFilms(jdbcTemplate.queryForList(sql, genreId, year, count));
    } else if (genreId != null) {
      sql = sqlFirstPart + "WHERE g.genre_id = ? \n" + sqlSecondPart;
      filmsList = mapRowsToFilms(jdbcTemplate.queryForList(sql, genreId, count));
    } else if (year != null) {
      sql = sqlFirstPart + "WHERE EXTRACT(YEAR FROM f.release_date) = ? \n" + sqlSecondPart;
      filmsList = mapRowsToFilms(jdbcTemplate.queryForList(sql, year, count));
    }

    return filmsList;
  }
}