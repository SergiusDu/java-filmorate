package ru.yandex.practicum.filmorate.search.application.port.in;

import java.util.List;
import java.util.Set;

public interface SearchUseCase {
  List<Long> searchFilms(String query, Set<String> by);
}
