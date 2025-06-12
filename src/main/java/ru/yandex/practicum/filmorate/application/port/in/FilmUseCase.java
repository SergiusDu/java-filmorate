package ru.yandex.practicum.filmorate.application.port.in;

import ru.yandex.practicum.filmorate.domain.model.Film;

import java.util.List;

public interface FilmUseCase {
  Film addFilm(Film film);

  Film updateFilm(Film film);

  List<Film> getAllFilms();
}
