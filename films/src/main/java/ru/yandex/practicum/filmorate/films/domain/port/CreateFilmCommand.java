package ru.yandex.practicum.filmorate.films.domain.port;

import ru.yandex.practicum.filmorate.films.domain.model.value.Genre;
import ru.yandex.practicum.filmorate.films.domain.model.value.Mpa;

import java.time.LocalDate;
import java.util.Set;

public record CreateFilmCommand(String name,
                                String description,
                                LocalDate releaseDate,
                                Long duration,
                                Set<Genre> genres,
                                Mpa mpa) {}
