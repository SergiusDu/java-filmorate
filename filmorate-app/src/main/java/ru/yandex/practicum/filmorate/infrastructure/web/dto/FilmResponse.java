package ru.yandex.practicum.filmorate.infrastructure.web.dto;

import lombok.Builder;
import ru.yandex.practicum.filmorate.films.domain.model.value.Genre;
import ru.yandex.practicum.filmorate.films.domain.model.value.Mpa;

import java.time.LocalDate;
import java.util.Set;

@Builder
public record FilmResponse(Long id,
                           String name,
                           String description,
                           LocalDate releaseDate,
                           long duration,
                           Set<Genre> genres,
                           Mpa mpa,
                           Set<DirectorResponse> directors) {
}
