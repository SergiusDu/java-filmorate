package ru.yandex.practicum.filmorate.infrastructure.web.dto;

import ru.yandex.practicum.filmorate.directors.domain.model.Director;
import ru.yandex.practicum.filmorate.films.domain.model.Film;

import java.util.List;

public record FilmWithDirectors(Film film,
                                List<Director> directors) {
}
