package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.common.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.common.exception.ValidationException;
import ru.yandex.practicum.filmorate.films.application.port.in.FilmUseCase;
import ru.yandex.practicum.filmorate.films.domain.model.Film;
import ru.yandex.practicum.filmorate.films.domain.model.value.Genre;
import ru.yandex.practicum.filmorate.films.domain.model.value.Mpa;
import ru.yandex.practicum.filmorate.films.domain.port.CreateFilmCommand;
import ru.yandex.practicum.filmorate.films.domain.port.UpdateFilmCommand;
import ru.yandex.practicum.filmorate.likes.application.port.in.LikeUseCase;
import ru.yandex.practicum.filmorate.users.application.port.in.UserUseCase;

import java.util.Collections;
import java.util.*;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmCompositionService {
  private final FilmUseCase filmUseCase;
  private final LikeUseCase likeService;
  private final UserUseCase userUseCase;
  private final DirectorUseCase directorUseCase;

  @Transactional
  public FilmWithDirectors createFilm(CreateFilmCommand command) {
    Film film = filmUseCase.addFilm(command);
    directorUseCase.updateFilmDirectors(film.id(), command.directorIds());
    return getFilmWithDirectors(film);
  }

  private FilmWithDirectors getFilmWithDirectors(Film film) {
    Map<Long, List<Director>> directorsMap = directorUseCase.getDirectorsForFilmIds(Set.of(film.id()));
    List<Director> directors = directorsMap.getOrDefault(film.id(), Collections.emptyList());
    return new FilmWithDirectors(film, directors);
  }

  @Transactional
  public FilmWithDirectors updateFilm(UpdateFilmCommand command) {
    Film film = filmUseCase.updateFilm(command);
    directorUseCase.updateFilmDirectors(film.id(), command.directorIds());
    return getFilmWithDirectors(film);
  }

  public List<FilmWithDirectors> getAllFilms() {
    return enrichFilmsWithDirectors(filmUseCase.getAllFilms());
  }

  private List<FilmWithDirectors> enrichFilmsWithDirectors(List<Film> films) {
    if (films.isEmpty()) {
      return Collections.emptyList();
    }
    Set<Long> filmIds = films.stream()
                             .map(Film::id)
                             .collect(Collectors.toSet());
    Map<Long, List<Director>> directorsByFilmId = directorUseCase.getDirectorsForFilmIds(filmIds);
    return films.stream()
                .map(film -> new FilmWithDirectors(film,
                                                   directorsByFilmId.getOrDefault(film.id(), Collections.emptyList())))
                .toList();
  }

  public boolean addLike(long filmId, long userId) {
    validateFilmExists(filmId);
    validateUserExists(userId);
    return likeService.addLike(filmId, userId);
  }

  private void validateFilmExists(long filmId) {
    if (filmUseCase.findFilmById(filmId)
                   .isEmpty())
      throw new ResourceNotFoundException("Film with id " + filmId + " not found");
  }

  private void validateUserExists(long userId) {
    if (userUseCase.findUserById(userId)
                   .isEmpty())
      throw new ResourceNotFoundException("User with id " + userId + " not found");
  }

  public boolean removeLike(long filmId, long userId) {
    validateFilmExists(filmId);
    validateUserExists(userId);
    return likeService.removeLike(filmId, userId);
  }

  public List<FilmWithDirectors> getPopularFilms(int count) {
    if (count < 0)
      throw new ValidationException("Count parameter cannot be negative");

    List<Long> popularFilmIds = likeService.getPopularFilmIds(count)
                                           .stream()
                                           .toList();
    List<Film> films = filmUseCase.getFilmsByIds(popularFilmIds);
    return enrichFilmsWithDirectors(films);
  }

  public List<Genre> getGenres() {
    return filmUseCase.getGeners();
  }

  public Genre getGenreById(long id) {
    return filmUseCase.getGenreById(id)
                      .orElseThrow(() -> new ResourceNotFoundException("Genre with id " + id + " not found"));
  }

  public List<Mpa> getMpas() {
    return filmUseCase.getMpas();
  }

  public Mpa getMpaById(long id) {
    return filmUseCase.getMpaById(id)
                      .orElseThrow(() -> new ResourceNotFoundException("Mpa with id " + id + " not found"));
  }

  public FilmWithDirectors getFilmById(long id) {
    Film film = filmUseCase.findFilmById(id)
                           .orElseThrow(() -> new ResourceNotFoundException("Film with id " + id + " not found"));
    return getFilmWithDirectors(film);
  }

  public List<Film> getCommonFilms(long userId, long friendId) {
    if (userId == friendId) {
      throw new ValidationException("User cannot be compared with themselves.");
    }

    validateUserId(userId);
    validateUserId(friendId);

    Set<Long> userLikes = likeService.findLikedFilms(userId);
    Set<Long> friendLikes = likeService.findLikedFilms(friendId);

    Set<Long> commonFilmIds = new HashSet<>(userLikes);
    commonFilmIds.retainAll(friendLikes);

    if (commonFilmIds.isEmpty()) {
      return List.of();
    }

    List<Film> commonFilms = filmUseCase.getFilmsByIds(commonFilmIds);
    Map<Long, Integer> likeCounts = likeService.getLikeCountsForFilms(commonFilmIds);

    return commonFilms.stream()
            .sorted(Comparator.comparingInt(
                    film -> -likeCounts.getOrDefault(film.id(), 0)
            ))
            .toList();
  }

}

  public List<FilmWithDirectors> getDirectorFilms(long directorId, SortBy sortBy) {
    List<Long> filmIds = directorUseCase.getFilmIdsByDirector(directorId, sortBy);
    if (filmIds.isEmpty()) {
      return Collections.emptyList();
    }
    List<Film> films = filmUseCase.getFilmsByIds(filmIds);
    return enrichFilmsWithDirectors(films);
  }
}