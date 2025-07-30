package ru.yandex.practicum.filmorate.films.domain.factory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.films.domain.model.Film;
import ru.yandex.practicum.filmorate.films.domain.port.CreateFilmCommand;
import ru.yandex.practicum.filmorate.films.domain.port.UpdateFilmCommand;

import java.time.Duration;

// Currently, the create() and update() methods just wrap Film.builder()
// without adding any logic. This makes the class redundant for now.
// However, it may become useful in the future if:
// - we import films from external APIs or files,
// - need to normalize fields (trim strings, adjust dates),
// - want to add logging or centralized validation on creation,
// - or if Film becomes a complex aggregate.
//
// If none of these happen — the factory can be safely removed and builder calls used directly.

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
               .mpa(command.mpa())
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
               .mpa(command.mpa())
               .build();
  }
}
