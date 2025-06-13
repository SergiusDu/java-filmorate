package ru.yandex.practicum.filmorate.application.port.in;

import ru.yandex.practicum.filmorate.domain.model.Film;
import ru.yandex.practicum.filmorate.infrastructure.web.dto.CreateFilmRequest;
import ru.yandex.practicum.filmorate.infrastructure.web.dto.UpdateFilmRequest;

import java.util.List;

public interface FilmUseCase {
  Film addFilm(CreateFilmRequest film);

  Film updateFilm(UpdateFilmRequest film);

  List<Film> getAllFilms();
}
