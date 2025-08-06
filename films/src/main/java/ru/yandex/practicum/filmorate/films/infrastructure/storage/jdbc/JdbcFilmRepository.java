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
public class JdbcFilmRepository implements FilmRepository {

    private static final String BASE_FILM_QUERY =
            "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, m.mpa_id, m.name as mpa_name " +
            "FROM films AS f JOIN mpa_ratings AS m ON f.mpa_id = m.mpa_id";

    private final JdbcTemplate jdbcTemplate;
    private final FilmFactory filmFactory;

    @Override
    public Film save(CreateFilmCommand command) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");

        Map<String, Object> params = Map.of(
                "name", command.name(),
                "description", command.description(),
                "release_date", command.releaseDate(),
                "duration", command.duration(),
                "mpa_id", command.mpa().id()
        );

        long filmId = insert.executeAndReturnKey(params).longValue();
        updateFilmGenres(filmId, command.genres());
        return filmFactory.create(filmId, command);
    }

    @Override
    public Film update(UpdateFilmCommand command) {
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? " +
                     "WHERE film_id = ?";
        int rows = jdbcTemplate.update(sql,
                command.name(),
                command.description(),
                command.releaseDate(),
                command.duration(),
                command.mpa().id(),
                command.id());
        if (rows == 0) {
            throw new ResourceNotFoundException("Film with id " + command.id() + " not found.");
        }
        updateFilmGenres(command.id(), command.genres());
        return filmFactory.update(command);
    }

    @Override
    public List<Film> findAll() {
        return mapRowsToFilms(jdbcTemplate.queryForList(BASE_FILM_QUERY));
    }

    @Override
    public Optional<Film> findById(long id) {
        String sql = BASE_FILM_QUERY + " WHERE f.film_id = ?";
        List<Film> films = mapRowsToFilms(jdbcTemplate.queryForList(sql, id));
        return films.stream().findFirst();
    }

    @Override
    public List<Film> getByIds(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        String placeholders = String.join(",", Collections.nCopies(ids.size(), "?"));
        String sql = BASE_FILM_QUERY + " WHERE f.film_id IN (" + placeholders + ")";
        List<Film> unsorted = mapRowsToFilms(jdbcTemplate.queryForList(sql, ids.toArray()));
        Map<Long, Film> map = unsorted.stream()
                .collect(Collectors.toMap(Film::id, f -> f));
        return ids.stream()
                  .map(map::get)
                  .filter(Objects::nonNull)
                  .toList();
    }

    private List<Film> mapRowsToFilms(List<Map<String, Object>> rows) {
        if (rows.isEmpty()) return List.of();
        Set<Long> filmIds = rows.stream()
                .map(r -> (Long) r.get("film_id"))
                .collect(Collectors.toSet());
        Map<Long, Set<Genre>> genres = getGenresForFilmIds(filmIds);

        return rows.stream()
                .map(r -> {
                    long id = (Long) r.get("film_id");
                    boolean deleted = r.get("is_deleted") != null && (Boolean) r.get("is_deleted");
                    return Film.builder()
                            .id(id)
                            .name((String) r.get("name"))
                            .description((String) r.get("description"))
                            .releaseDate(((java.sql.Date) r.get("release_date")).toLocalDate())
                            .duration(Duration.ofMinutes((Integer) r.get("duration")))
                            .genres(genres.getOrDefault(id, Set.of()))
                            .mpa(new Mpa((Long) r.get("mpa_id"), (String) r.get("mpa_name")))
                            .isDeleted(deleted)
                            .build();
                })
                .toList();
    }

    private Map<Long, Set<Genre>> getGenresForFilmIds(Set<Long> filmIds) {
        if (filmIds.isEmpty()) return Map.of();
        String placeholders = String.join(",", Collections.nCopies(filmIds.size(), "?"));
        String sql = "SELECT fg.film_id, g.genre_id, g.name " +
                     "FROM film_genres fg JOIN genres g ON fg.genre_id = g.genre_id " +
                     "WHERE fg.film_id IN (" + placeholders + ") ORDER BY g.genre_id";
        Map<Long, Set<Genre>> result = new HashMap<>();
        jdbcTemplate.query(sql, rs -> {
            long filmId = rs.getLong("film_id");
            Genre genre = new Genre(rs.getLong("genre_id"), rs.getString("name"));
            result.computeIfAbsent(filmId, k -> new LinkedHashSet<>()).add(genre);
        }, filmIds.toArray());
        return result;
    }

    private void updateFilmGenres(long filmId, Set<Genre> genres) {
        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", filmId);
        if (genres != null && !genres.isEmpty()) {
            List<Object[]> batch = genres.stream()
                    .map(g -> new Object[]{filmId, g.id()})
                    .toList();
            jdbcTemplate.batchUpdate("INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)", batch);
        }
    }

    @Override
    public List<Film> findFilmsByGenreIdAndYear(Long genreId, Integer year, Integer count) {
        String base = "SELECT f.* FROM likes l " +
                      "JOIN films f ON l.film_id = f.film_id " +
                      "JOIN film_genres fg ON f.film_id = fg.film_id " +
                      "JOIN genres g ON g.genre_id = fg.genre_id ";
        String groupOrder = " GROUP BY l.film_id ORDER BY COUNT(l.user_id) DESC LIMIT ?";
        List<Film> list = new ArrayList<>();

        if (genreId != null && year != null) {
            list = mapRowsToFilms(jdbcTemplate.queryForList(
                base + "WHERE g.genre_id = ? AND EXTRACT(YEAR FROM f.release_date) = ?" + groupOrder,
                genreId, year, count
            ));
        } else if (genreId != null) {
            list = mapRowsToFilms(jdbcTemplate.queryForList(
                base + "WHERE g.genre_id = ?" + groupOrder,
                genreId, count
            ));
        } else if (year != null) {
            list = mapRowsToFilms(jdbcTemplate.queryForList(
                base + "WHERE EXTRACT(YEAR FROM f.release_date) = ?" + groupOrder,
                year, count
            ));
        }
        return list;
    }

    @Override
    public boolean deleteById(long filmId) {
        return jdbcTemplate.update("DELETE FROM films WHERE film_id = ?", filmId) > 0;
    }
}