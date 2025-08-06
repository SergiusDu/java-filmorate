package ru.yandex.practicum.filmorate.films.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.common.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.films.application.port.in.FilmUseCase;
import ru.yandex.practicum.filmorate.films.domain.model.Film;
import ru.yandex.practicum.filmorate.films.domain.model.value.Genre;
import ru.yandex.practicum.filmorate.films.domain.model.value.Mpa;
import ru.yandex.practicum.filmorate.films.domain.port.*;
import ru.yandex.practicum.filmorate.films.domain.service.FilmValidationService;

import java.util.List;
import java.util.Optional;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class FilmService
    implements FilmUseCase {
  private final FilmRepository filmRepository;
  private final GenreRepository genreRepository;
  private final MpaRepository mpaRepository;

  private final FilmValidationService filmValidationService;

  @Override
  public Film addFilm(CreateFilmCommand command) {
    validateFilmDependencies(command.genres(), command.mpa());
    filmValidationService.validate(command);
    return filmRepository.save(command);
  }

  @Override
  public Film updateFilm(UpdateFilmCommand command) {
    validateFilmDependencies(command.genres(), command.mpa());
    filmValidationService.validate(command);
    return filmRepository.update(command);
  }

  @Override
  public Optional<Film> findFilmById(long filmId) {
    return filmRepository.findById(filmId);
  }

  @Override
  public List<Film> getAllFilms() {
    return filmRepository.findAll();
  }

  @Override
  public List<Film> getFilmsByIds(List<Long> ids) {
    return filmRepository.getByIds(ids);
  }

  @Override
  public List<Genre> getGeners() {
    return genreRepository.findAll();
  }

  @Override
  public Optional<Genre> getGenreById(long id) {
    return genreRepository.findById(id);
  }

  @Override
  public List<Mpa> getMpas() {
    return mpaRepository.findAll();
  }

  @Override
  public Optional<Mpa> getMpaById(long id) {
    return mpaRepository.findById(id);
  }

  private void validateFilmDependencies(Set<Genre> genres, Long mpaId) {
    if (mpaId != null && mpaRepository
                             .findById(mpaId)
                             .isEmpty()) {
      throw new ResourceNotFoundException("Mpa with id " + mpaId + " not found");
    }

    if (genres != null) {
      genres.forEach(genre -> {
        if (genreRepository
                .findById(genre.id())
                .isEmpty()) {
          throw new ResourceNotFoundException("Genre with id " + genre.id() + " not found");
        }
      });
    }
  }
}
