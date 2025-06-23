package ru.yandex.practicum.filmorate.films.application.port.in;

import ru.yandex.practicum.filmorate.films.domain.model.Film;
import ru.yandex.practicum.filmorate.films.domain.port.CreateFilmCommand;
import ru.yandex.practicum.filmorate.films.domain.port.UpdateFilmCommand;

import java.util.List;
import java.util.Set;

public interface FilmUseCase {
  Film addFilm(CreateFilmCommand command);

  Film updateFilm(UpdateFilmCommand command);

  List<Film> getAllFilms();

  List<Film> getFilmsByIds(Set<Long> ids);
}
