package ru.yandex.practicum.filmorate.films.infrastructure.web.dto;

import java.time.LocalDate;
import java.util.UUID;

public record FilmResponse(UUID id,
                           String name,
                           String description,
                           LocalDate releaseDate,
                           long duration) {}
