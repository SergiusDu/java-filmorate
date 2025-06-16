package ru.yandex.practicum.filmorate.films.infrastructure.web.dto;

import java.time.LocalDate;

public record FilmResponse(Integer id,
                           String name,
                           String description,
                           LocalDate releaseDate,
                           long duration) {}
