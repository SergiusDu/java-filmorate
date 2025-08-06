package ru.yandex.practicum.filmorate.films.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.common.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.films.application.port.in.FilmRatingQuery;
import ru.yandex.practicum.filmorate.films.application.port.in.FilmUseCase;
import ru.yandex.practicum.filmorate.films.application.port.in.RecommendationQuery;
import ru.yandex.practicum.filmorate.films.domain.model.Film;
import ru.yandex.practicum.filmorate.films.domain.model.value.Genre;
import ru.yandex.practicum.filmorate.films.domain.model.value.Mpa;
import ru.yandex.practicum.filmorate.films.domain.port.*;
import ru.yandex.practicum.filmorate.films.domain.service.FilmValidationService;
import ru.yandex.practicum.filmorate.likes.application.port.in.LikeUseCase;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService implements FilmUseCase {
    private final FilmRepository filmRepository;
    private final GenreRepository genreRepository;
    private final MpaRepository mpaRepository;
    private final FilmValidationService filmValidationService;
  private final LikeUseCase likeUseCase;

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
    public Optional<Film> getFilmById(long id) {
        return filmRepository.findById(id);
    }

    @Override
    public List<Film> getFilmsByIds(Set<Long> ids) {
        return filmRepository.getByIds(ids);
    }

    @Override
    public List<Genre> getGenres() {
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

  @Override
  public List<Film> findPopularFilms(FilmRatingQuery query) {
    if (query.sortBy() == FilmRatingQuery.SortBy.LIKES) {
      Set<Long> topIds = likeUseCase.getPopularFilmIds(query.limit());
      return getFilmsByIds(topIds).stream()
              .filter(film -> query.genreId().map(id -> film.hasGenre(id)).orElse(true))
              .filter(film -> query.year().map(year -> film.releaseDate().getYear() == year).orElse(true))
              .toList();
    }
    return List.of();
  }

  @Override
  public List<Film> getRecommendations(RecommendationQuery query) {
    throw new UnsupportedOperationException("Delegated to RecommendationService.");
  }

  private void validateFilmDependencies(Set<Genre> genres, Mpa mpa) {
        if (mpa != null && mpaRepository.findById(mpa.id()).isEmpty()) {
            throw new ResourceNotFoundException("Mpa with id " + mpa.id() + " not found");
        }

        if (genres != null) {
            for (Genre genre : genres) {
                if (genreRepository.findById(genre.id()).isEmpty()) {
                    throw new ResourceNotFoundException("Genre with id " + genre.id() + " not found");
                }
            }
        }
    }

  }
