package ru.yandex.practicum.filmorate.films.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.films.domain.model.Film;
import ru.yandex.practicum.filmorate.films.domain.port.CreateFilmCommand;
import ru.yandex.practicum.filmorate.films.infrastructure.web.dto.CreateFilmRequest;
import ru.yandex.practicum.filmorate.films.infrastructure.web.dto.FilmResponse;
import ru.yandex.practicum.filmorate.films.infrastructure.web.dto.UpdateFilmRequest;

import java.time.Duration;

@Component
public class FilmMapper {
  private FilmMapper() {
  }

  public static FilmResponse toResponse(Film film) {
    return new FilmResponse(film.id(),
                            film.name(),
                            film.description(),
                            film.releaseDate(),
                            film.duration()
                                .toSeconds());
  }

  public static Film toDomain(CreateFilmRequest request, int id) {
    return new Film(id,
                    request.name(),
                    request.description(),
                    request.releaseDate(),
                    Duration.ofSeconds(request.duration()));
  }

  public static Film toDomain(UpdateFilmRequest request) {
    return new Film(request.id(),
                    request.name(),
                    request.description(),
                    request.releaseDate(),
                    Duration.ofSeconds(request.duration()));
  }

  public static CreateFilmCommand toCreateCommand(CreateFilmRequest request) {
    return new CreateFilmCommand(request.name(),
                                 request.description(),
                                 request.releaseDate(),
                                 Duration.ofSeconds(request.duration()));
  }
}
