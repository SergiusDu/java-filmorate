package ru.yandex.practicum.filmorate.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.domain.model.Film;
import ru.yandex.practicum.filmorate.infrastructure.web.dto.CreateFilmRequest;
import ru.yandex.practicum.filmorate.infrastructure.web.dto.FilmResponse;
import ru.yandex.practicum.filmorate.infrastructure.web.dto.UpdateFilmRequest;

import java.time.Duration;
import java.util.UUID;

@Component
public class FilmMapper {
  private FilmMapper() {}

  public static FilmResponse toResponse(Film film) {
    return new FilmResponse(film.id(),
                            film.name(),
                            film.description(),
                            film.releaseDate(),
                            film.duration()
                                .toSeconds());
  }

  public static Film toDomain(CreateFilmRequest request, UUID id) {
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
}
