package ru.yandex.practicum.filmorate.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.directors.domain.model.Director;
import ru.yandex.practicum.filmorate.films.domain.model.Film;
import ru.yandex.practicum.filmorate.films.domain.model.value.Genre;
import ru.yandex.practicum.filmorate.films.domain.model.value.Mpa;
import ru.yandex.practicum.filmorate.films.domain.port.CreateFilmCommand;
import ru.yandex.practicum.filmorate.films.domain.port.UpdateFilmCommand;
import ru.yandex.practicum.filmorate.infrastructure.web.dto.*;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class FilmMapper {

  public CreateFilmCommand toCommand(CreateFilmRequest request) {
    Set<Long> directorIds = (request.directors() == null)
                            ? Collections.emptySet()
                            : request.directors()
                                     .stream()
                                     .map(DirectorIdDto::id)
                                     .collect(Collectors.toSet());
    Mpa mpaCreate = (request.mpa() == null)
                    ? null
                    : new Mpa(request.mpa()
                                     .id(), null);
    return new CreateFilmCommand(request.name(),
                                 request.description(),
                                 request.releaseDate(),
                                 request.duration(),
                                 request.genres(),
                                 mpaCreate,
                                 directorIds);
  }

  public UpdateFilmCommand toCommand(UpdateFilmRequest request) {
    Set<Long> directorIds = (request.directors() == null)
                            ? Collections.emptySet()
                            : request.directors()
                                     .stream()
                                     .map(DirectorIdDto::id)
                                     .collect(Collectors.toSet());
    Mpa mpaUpdate = (request.mpa() == null)
                    ? null
                    : new Mpa(request.mpa()
                                     .id(), null);
    return new UpdateFilmCommand(request.id(),
                                 request.name(),
                                 request.description(),
                                 request.releaseDate(),
                                 request.duration(),
                                 request.genres(),
                                 mpaUpdate,
                                 directorIds);
  }

  public FilmResponse toResponse(Film film) {
    return toResponse(new FilmWithDirectors(film, null));
  }

  public FilmResponse toResponse(FilmWithDirectors dto) {
    final Film film = dto.film();
    final Set<Genre> genres = film.genres();
    final Set<Genre> sortedGenres = (genres == null)
                                    ? null
                                    : genres.stream()
                                            .sorted(Comparator.comparing(Genre::id))
                                            .collect(Collectors.toCollection(LinkedHashSet::new));

    return new FilmResponse(film.id(),
                            film.name(),
                            film.description(),
                            film.releaseDate(),
                            film.duration()
                                .toMinutes(),
                            sortedGenres,
                            film.mpa(),
                            toDirectorResponses(dto.directors()));
  }

  private Set<DirectorResponse> toDirectorResponses(List<Director> directors) {
    if (directors == null) {
      return Collections.emptySet();
    }
    return directors.stream()
                    .map(d -> new DirectorResponse(d.id(), d.name()))
                    .collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(DirectorResponse::id))));
  }
}