package ru.yandex.practicum.filmorate.films.infrastructure.web.dto;

import java.time.LocalDate;

public record FilmResponse(Long id,
                           String name,
                           String description,
                           LocalDate releaseDate,
                           long duration) {}
