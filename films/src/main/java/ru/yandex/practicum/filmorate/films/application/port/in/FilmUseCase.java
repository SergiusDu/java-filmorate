package ru.yandex.practicum.filmorate.films.application.port.in;

import ru.yandex.practicum.filmorate.films.domain.model.Film;
import ru.yandex.practicum.filmorate.films.domain.model.value.Genre;
import ru.yandex.practicum.filmorate.films.domain.model.value.Mpa;
import ru.yandex.practicum.filmorate.films.domain.port.CreateFilmCommand;
import ru.yandex.practicum.filmorate.films.domain.port.UpdateFilmCommand;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface FilmUseCase {
    Film addFilm(CreateFilmCommand command);

    Film updateFilm(UpdateFilmCommand command);

    Optional<Film> findFilmById(long filmId);

    List<Film> getAllFilms();

    Optional<Film> getFilmById(long id);

    List<Film> getFilmsByIds(Set<Long> ids);

    List<Genre> getGeners();

    Optional<Genre> getGenreById(long id);

    List<Mpa> getMpas();

    Optional<Mpa> getMpaById(long id);

    List<Film> findFilmsByGenreIdAndYear(long genreId, int year, int count);
}