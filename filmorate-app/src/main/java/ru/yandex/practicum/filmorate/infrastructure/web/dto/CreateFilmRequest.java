package ru.yandex.practicum.filmorate.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import ru.yandex.practicum.filmorate.common.validation.ValidReleaseDate;
import ru.yandex.practicum.filmorate.films.domain.model.value.Genre;
import ru.yandex.practicum.filmorate.films.domain.model.value.Mpa;

import java.time.LocalDate;
import java.util.Set;

public record CreateFilmRequest(@NotBlank(message = "Name cannot be empty")
                                String name,

                                @NotNull(message = "Description cannot be null")
                                String description,

                                @NotNull(message = "Release date cannot be null")
                                @ValidReleaseDate(message = "Release date must be between the earliest allowed date " +
                                                            "and today")
                                LocalDate releaseDate,

                                @Positive(message = "Duration must be positive")
                                long duration,

                                @NotEmpty(message = "Genres cannot be empty")
                                Set<Genre> genres,

                                @NotNull(message = "Rating cannot be null")
                                Mpa mpa) {}
