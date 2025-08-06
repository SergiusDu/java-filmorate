package ru.yandex.practicum.filmorate.films.domain.factory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.films.domain.model.Film;
import ru.yandex.practicum.filmorate.films.domain.port.CreateFilmCommand;
import ru.yandex.practicum.filmorate.films.domain.port.UpdateFilmCommand;

import java.time.Duration;

@Component
public class FilmFactory {

  public Film create(long id, CreateFilmCommand command) {
    return Film.builder()
               .id(id)
               .name(command.name())
               .description(command.description())
               .releaseDate(command.releaseDate())
               .duration(Duration.ofMinutes(command.duration()))
               .genres(command.genres())
               .isDeleted(false)
               .mpa(null)
               .build();
  }

  public Film update(UpdateFilmCommand command) {
    return Film.builder()
               .id(command.id())
               .name(command.name())
               .description(command.description())
               .releaseDate(command.releaseDate())
               .duration(Duration.ofMinutes(command.duration()))
               .genres(command.genres())
               .mpa(null)
               .build();
  }
}
