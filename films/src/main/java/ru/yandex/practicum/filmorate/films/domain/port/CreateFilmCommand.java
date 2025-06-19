package ru.yandex.practicum.filmorate.films.domain.port;

import java.time.LocalDate;

public record CreateFilmCommand(String name,
                                String description,
                                LocalDate releaseDate,
                                Long duration) {}
