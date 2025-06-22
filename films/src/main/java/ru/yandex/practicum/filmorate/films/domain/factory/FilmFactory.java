package ru.yandex.practicum.filmorate.films.domain.factory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.films.domain.model.Film;
import ru.yandex.practicum.filmorate.films.domain.port.CreateFilmCommand;
import ru.yandex.practicum.filmorate.films.domain.port.UpdateFilmCommand;

import java.time.Duration;

@Component
public class FilmFactory {
  public Film create(long id, CreateFilmCommand command) {
    return new Film(id,
                    command.name(),
                    command.description(),
                    command.releaseDate(),
                    Duration.ofSeconds(command.duration()));
  }

  public Film update(UpdateFilmCommand command) {
    return new Film(command.id(),
                    command.name(),
                    command.description(),
                    command.releaseDate(),
                    Duration.ofSeconds(command.duration()));
  }
}
