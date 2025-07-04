package ru.yandex.practicum.filmorate.films.domain.port;

import java.time.LocalDate;

public record UpdateFilmCommand(Long id,
                                String name,
                                String description,
                                LocalDate releaseDate,
                                long duration) {}
