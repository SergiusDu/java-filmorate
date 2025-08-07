package ru.yandex.practicum.filmorate.search.infrastructure.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.search.application.port.out.SearchRepository;
import ru.yandex.practicum.filmorate.search.domain.model.SearchableFilm;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class JdbcSearchRepository
    implements SearchRepository {

  private final JdbcTemplate jdbcTemplate;

  @Override
  public void save(SearchableFilm film) {
    String sql = "MERGE INTO search_index (film_id, title, directors_names) KEY(film_id) VALUES (?, ?, ?)";
    jdbcTemplate.update(sql, film.filmId(), film.title(), film.directorNames());
  }

  @Override
  public List<Long> search(String query, Set<String> by) {
    List<String> conditions = new ArrayList<>();
    List<Object> params = new ArrayList<>();
    String searchQuery = "%" + query + "%";

    if (by.contains("TITLE")) {
      conditions.add("title LIKE ?");
      params.add(searchQuery);
    }
    if (by.contains("DIRECTOR")) {
      conditions.add("directors_names LIKE ?");
      params.add(searchQuery);
    }

    if (conditions.isEmpty()) {
      return List.of();
    }

    String whereClause = String.join(" OR ", conditions);
    String sql = "SELECT film_id FROM search_index WHERE " + whereClause;

    return jdbcTemplate.queryForList(sql, Long.class, params.toArray());
  }
}