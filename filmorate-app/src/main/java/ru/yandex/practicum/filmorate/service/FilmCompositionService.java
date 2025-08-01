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

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FilmCompositionService {
  private final FilmUseCase filmUseCase;
  private final LikeUseCase likeService;
  private final UserUseCase userUseCase;


  public List<Film> getAllFilms() {
    return filmUseCase.getAllFilms();
  }

  public Film createFilm(CreateFilmCommand command) {
    return filmUseCase.addFilm(command);
  }

  public Film updateFilm(UpdateFilmCommand command) {
    return filmUseCase.updateFilm(command);
  }

  public boolean addLike(long filmId,
                         long userId) {
    validateFilmId(filmId);
    validateUserId(userId);
    return likeService.addLike(filmId,
                               userId);
  }

  private void validateFilmId(long filmId) {
    if (filmUseCase.findFilmById(filmId)
                   .isEmpty())
      throw new ResourceNotFoundException("Film with id " + filmId + " not found");
  }

  private void validateUserId(long userId) {
    if (userUseCase.findUserById(userId)
                   .isEmpty())
      throw new ResourceNotFoundException("User with id " + userId + " not found");
  }

  public boolean removeLike(long filmId,
                            long userId) {
    validateFilmId(filmId);
    validateUserId(userId);
    return likeService.removeLike(filmId,
                                  userId);
  }

  public List<Film> getPopularFilms(int count) {
    if (count < 0) {
      throw new ValidationException("Count parameter cannot be negative");
    }
    List<Film> all = filmUseCase.getAllFilms();
    Map<Long, Long> likeCounts = likeService.getLikeCounts();
    return all.stream()
            .sorted(Comparator.comparingLong(
                    f -> - likeCounts.getOrDefault(f.id(), 0L)
            ))
            .limit(count)
            .toList();
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

  public Film getFilmById(long id) {
    return filmUseCase.findFilmById(id)
                      .orElseThrow(() -> new ResourceNotFoundException("Film with id " + id + " not found"));
  }

  public void deleteFilmById(long filmId) {
    validateFilmId(filmId);
    likeService.deleteLikesByFilmId(filmId);
    filmUseCase.deleteFilmById(filmId);
  }

  public List<Film> getCommonFilms(long userId, long friendId) {
    validateUserId(userId);
    validateUserId(friendId);

    Set<Long> userLikes = likeService.findLikedFilms(userId);
    Set<Long> friendLikes = likeService.findLikedFilms(friendId);

    Set<Long> commonFilmIds = new HashSet<>(userLikes);
    commonFilmIds.retainAll(friendLikes);

    if (commonFilmIds.isEmpty()) {
      return List.of();
    }

    return filmUseCase.getFilmsByIds(commonFilmIds).stream()
            .sorted(Comparator.comparingInt(
                    film -> -likeService.findUsersWhoLikedFilm(film.id()).size()
            ))
            .toList();
  }
}
