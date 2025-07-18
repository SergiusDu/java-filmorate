package ru.yandex.practicum.filmorate.films.domain.port;

import ru.yandex.practicum.filmorate.films.domain.model.value.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreRepository {
  Genre save(CreateGenreCommand command);

  Genre update(UpdateGenreCommand command);

  List<Genre> findAll();

  Optional<Genre> findById(long id);
}