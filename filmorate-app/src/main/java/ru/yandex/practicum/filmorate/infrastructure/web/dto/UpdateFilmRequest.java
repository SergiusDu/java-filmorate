package ru.yandex.practicum.filmorate.infrastructure.web.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import ru.yandex.practicum.filmorate.common.validation.ValidReleaseDate;
import ru.yandex.practicum.filmorate.films.domain.model.value.Genre;
import ru.yandex.practicum.filmorate.films.domain.model.value.Mpa;

import java.time.LocalDate;
import java.util.Set;

public record UpdateFilmRequest(@NotNull(message = "Film ID cannot be null")
                                Long id,
                                @NotNull(message = "Name cannot be null")
                                String name,

                                @NotNull(message = "Description cannot be null")
                                String description,

                                @NotNull(message = "Release date cannot be null")
                                @ValidReleaseDate(message = "Release date must be between the earliest allowed date " +
                                                            "and today")
                                LocalDate releaseDate,

                                @NotNull(message = "Duration cannot be null")
                                long duration,

                                @NotEmpty(message = "Genres cannot be empty")
                                Set<Genre> genres,

                                @NotNull(message = "Rating cannot be null")
                                Mpa mpa) {}