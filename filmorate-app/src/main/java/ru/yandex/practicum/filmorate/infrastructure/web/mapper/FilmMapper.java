package ru.yandex.practicum.filmorate.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.films.domain.model.Film;
import ru.yandex.practicum.filmorate.films.domain.model.value.Genre;
import ru.yandex.practicum.filmorate.films.domain.port.CreateFilmCommand;
import ru.yandex.practicum.filmorate.films.domain.port.UpdateFilmCommand;
import ru.yandex.practicum.filmorate.infrastructure.web.dto.CreateFilmRequest;
import ru.yandex.practicum.filmorate.infrastructure.web.dto.FilmResponse;
import ru.yandex.practicum.filmorate.infrastructure.web.dto.UpdateFilmRequest;

import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class FilmMapper {
  public CreateFilmCommand toCommand(CreateFilmRequest request) {
    // Ensure genres is never null - use empty set if not provided
    Set<Genre> genres = request.genres() != null ? request.genres() : new HashSet<>();

    return new CreateFilmCommand(request.name(), request.description(), request.releaseDate(), request.duration(),
                                 genres, request.mpa());
  }

  public UpdateFilmCommand toCommand(UpdateFilmRequest request) {
    // Ensure genres is never null - use empty set if not provided
    Set<Genre> genres = request.genres() != null ? request.genres() : new HashSet<>();

    return UpdateFilmCommand.builder()
                            .id(request.id())
                            .name(request.name())
                            .description(request.description())
                            .releaseDate(request.releaseDate())
                            .duration(request.duration())
                            .genres(genres)
                            .mpa(request.mpa())
                            .build();
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
    return FilmResponse.builder()
                       .id(film.id())
                       .name(film.name())
                       .description(film.description())
                       .releaseDate(film.releaseDate())
                       .duration(film.duration()
                                     .toMinutes())
                       .genres(sortedGenres)
                       .mpa(film.mpa())
                       .build();
  }
}