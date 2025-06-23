package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.common.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.common.exception.ValidationException;
import ru.yandex.practicum.filmorate.films.application.port.in.FilmUseCase;
import ru.yandex.practicum.filmorate.films.application.port.in.UserUseCase;
import ru.yandex.practicum.filmorate.films.domain.model.Film;
import ru.yandex.practicum.filmorate.films.domain.port.CreateFilmCommand;
import ru.yandex.practicum.filmorate.films.domain.port.UpdateFilmCommand;
import ru.yandex.practicum.filmorate.likes.application.port.in.LikeUseCase;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmCompositionService {
  private final FilmUseCase filmUseCase;
  private final LikeUseCase likeService;
  private final UserUseCase userUseCase;

  public List<Film> getAllFilms() {
    return filmUseCase.getAllFilms()
                      .stream()
                      .toList();
  }

  public Film createFilm(CreateFilmCommand command) {
    return filmUseCase.addFilm(command);
  }

  public Film updateFilm(UpdateFilmCommand command) {
    return filmUseCase.updateFilm(command);
  }

  public boolean addLike(long filmId, long userId) {
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

  public boolean removeLike(long filmId, long userId) {
    validateFilmId(filmId);
    validateUserId(userId);
    return likeService.removeLike(filmId,
                                  userId);
  }

  public List<Film> getPopularFilms(int count) {
    if (count < 0)
      throw new ValidationException("Count parameter cannot be negative");
    return filmUseCase.getFilmsByIds(likeService.getPopularFilmIds(count));
  }
}
