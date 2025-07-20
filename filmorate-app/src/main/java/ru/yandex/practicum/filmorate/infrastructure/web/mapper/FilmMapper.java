package ru.yandex.practicum.filmorate.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.films.domain.model.Film;
import ru.yandex.practicum.filmorate.films.domain.model.value.Genre;
import ru.yandex.practicum.filmorate.films.domain.port.CreateFilmCommand;
import ru.yandex.practicum.filmorate.films.domain.port.UpdateFilmCommand;
import ru.yandex.practicum.filmorate.infrastructure.web.dto.CreateFilmRequest;
import ru.yandex.practicum.filmorate.infrastructure.web.dto.FilmResponse;
import ru.yandex.practicum.filmorate.infrastructure.web.dto.UpdateFilmRequest;

import java.time.Duration;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

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


  public FilmResponse toResponse(Film film) {
    final Set<Genre> genres = film.genres();
    final Set<Genre> sortedGenres;

    if (genres == null) {
      sortedGenres = null;
    } else {
      sortedGenres = genres.stream()
                           .sorted(Comparator.comparing(Genre::id))
                           .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    return new FilmResponse(film.id(),
                            film.name(),
                            film.description(),
                            film.releaseDate(),
                            film.duration()
                                .toMinutes(),
                            sortedGenres,
                            film.mpa());
  }

  public Film toDomain(CreateFilmRequest request, long id) {
    return new Film(id,
                    request.name(),
                    request.description(),
                    request.releaseDate(),
                    Duration.ofMinutes(request.duration()),
                    request.genres(),
                    request.mpa());
  }

  public Film toDomain(UpdateFilmRequest request) {
    return new Film(request.id(),
                    request.name(),
                    request.description(),
                    request.releaseDate(),
                    Duration.ofMinutes(request.duration()),
                    request.genres(),
                    request.mpa());
  }
}