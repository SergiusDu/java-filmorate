package ru.yandex.practicum.filmorate.films.domain.factory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.films.domain.model.value.Genre;
import ru.yandex.practicum.filmorate.films.domain.port.CreateGenreCommand;
import ru.yandex.practicum.filmorate.films.domain.port.UpdateGenreCommand;

@Component
public class GenreFactory {
  public Genre create(long id, CreateGenreCommand command) {
    return new Genre(id,
                     command.name());
  }

  public Genre update(UpdateGenreCommand command) {
    return new Genre(command.id(),
                     command.name());
  }
}
