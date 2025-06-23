package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.films.application.port.in.FilmUseCase;
import ru.yandex.practicum.filmorate.films.domain.model.Film;
import ru.yandex.practicum.filmorate.films.domain.port.CreateFilmCommand;
import ru.yandex.practicum.filmorate.films.domain.port.UpdateFilmCommand;
import ru.yandex.practicum.filmorate.infrastructure.web.mapper.FilmMapper;
import ru.yandex.practicum.folmorate.likes.application.port.in.LikeUseCase;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmCompositionService {
  FilmUseCase filmUseCase;
  LikeUseCase likeService;
  FilmMapper filmMapper;

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
    return likeService.addLike(filmId,
                               userId);
  }

  public boolean removeLike(long filmId, long userId) {
    return likeService.removeLike(filmId,
                                  userId);
  }

  public List<Film> getPopularFilms(int count) {
    return filmUseCase.getFilmsByIds(likeService.getPopularFilmIds(count));
  }
}
