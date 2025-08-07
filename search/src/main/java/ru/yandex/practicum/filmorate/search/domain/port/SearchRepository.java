package ru.yandex.practicum.filmorate.search.domain.port;

import ru.yandex.practicum.filmorate.search.domain.model.SearchableFilm;

import java.util.List;
import java.util.Set;

public interface SearchRepository {
  void save(SearchableFilm film);

  List<Long> search(String query, Set<String> by);
}