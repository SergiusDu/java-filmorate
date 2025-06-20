package ru.yandex.practicum.filmorate.films.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.films.domain.model.Film;
import ru.yandex.practicum.filmorate.films.domain.port.CreateFilmCommand;
import ru.yandex.practicum.filmorate.films.domain.port.UpdateFilmCommand;
import ru.yandex.practicum.filmorate.films.infrastructure.web.dto.CreateFilmRequest;
import ru.yandex.practicum.filmorate.films.infrastructure.web.dto.FilmResponse;
import ru.yandex.practicum.filmorate.films.infrastructure.web.dto.UpdateFilmRequest;

import java.time.Duration;

@Component
public class FilmMapper {
  public CreateFilmCommand toCommand(CreateFilmRequest request) {
    return new CreateFilmCommand(request.name(),
                                 request.description(),
                                 request.releaseDate(),
                                 request.duration());
  }

  public UpdateFilmCommand toCommand(UpdateFilmRequest request) {
    return new UpdateFilmCommand(request.id(),
                                 request.name(),
                                 request.description(),
                                 request.releaseDate(),
                                 request.duration());
  }

  public Film fromCommand(Long id, CreateFilmCommand command) {
    return new Film(id,
                    command.name(),
                    command.description(),
                    command.releaseDate(),
                    Duration.ofSeconds(command.duration()));
  }

  public Film fromCommand(UpdateFilmCommand command) {
    return new Film(command.id(),
                    command.name(),
                    command.description(),
                    command.releaseDate(),
                    Duration.ofSeconds(command.duration()));
  }

  public FilmResponse toResponse(Film film) {
    return new FilmResponse(film.id(),
                            film.name(),
                            film.description(),
                            film.releaseDate(),
                            film.duration()
                                .toSeconds());
  }

  public Film toDomain(CreateFilmRequest request, long id) {
    return new Film(id,
                    request.name(),
                    request.description(),
                    request.releaseDate(),
                    Duration.ofSeconds(request.duration()));
  }

  public Film toDomain(UpdateFilmRequest request) {
    return new Film(request.id(),
                    request.name(),
                    request.description(),
                    request.releaseDate(),
                    Duration.ofSeconds(request.duration()));
  }
}
