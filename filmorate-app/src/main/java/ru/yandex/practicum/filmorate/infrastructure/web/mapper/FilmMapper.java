package ru.yandex.practicum.filmorate.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.films.domain.model.Film;
import ru.yandex.practicum.filmorate.films.domain.port.CreateFilmCommand;
import ru.yandex.practicum.filmorate.films.domain.port.UpdateFilmCommand;
import ru.yandex.practicum.filmorate.infrastructure.web.dto.CreateFilmRequest;
import ru.yandex.practicum.filmorate.infrastructure.web.dto.FilmResponse;
import ru.yandex.practicum.filmorate.infrastructure.web.dto.UpdateFilmRequest;

import java.time.Duration;

@Component
public class FilmMapper {
  public CreateFilmCommand toCommand(CreateFilmRequest request) {
    return new CreateFilmCommand(request.name(),
                                 request.description(),
                                 request.releaseDate(),
                                 request.duration(),
                                 request.genres(),
                                 request.mpa());
  }

  public UpdateFilmCommand toCommand(UpdateFilmRequest request) {
    return new UpdateFilmCommand(request.id(),
                                 request.name(),
                                 request.description(),
                                 request.releaseDate(),
                                 request.duration(),
                                 request.genres(),
                                 request.mpa());
  }

  public Film fromCommand(Long id, CreateFilmCommand command) {
    return new Film(id,
                    command.name(),
                    command.description(),
                    command.releaseDate(),
                    Duration.ofSeconds(command.duration()),
                    command.genres(),
                    command.mpa());
  }

  public Film fromCommand(UpdateFilmCommand command) {
    return new Film(command.id(),
                    command.name(),
                    command.description(),
                    command.releaseDate(),
                    Duration.ofSeconds(command.duration()),
                    command.genres(),
                    command.mpa());
  }

  public FilmResponse toResponse(Film film) {
    return new FilmResponse(film.id(),
                            film.name(),
                            film.description(),
                            film.releaseDate(),
                            film.duration()
                                .toMinutes(),
                            film.genres(),
                            film.mpa());
  }

  public Film toDomain(CreateFilmRequest request, long id) {
    return new Film(id,
                    request.name(),
                    request.description(),
                    request.releaseDate(),
                    Duration.ofSeconds(request.duration()),
                    request.genres(),
                    request.mpa());
  }

  public Film toDomain(UpdateFilmRequest request) {
    return new Film(request.id(),
                    request.name(),
                    request.description(),
                    request.releaseDate(),
                    Duration.ofSeconds(request.duration()),
                    request.genres(),
                    request.mpa());
  }
}
