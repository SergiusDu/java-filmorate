package ru.yandex.practicum.filmorate.films.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record CreateFilmRequest(@NotBlank(message = "Name cannot be empty")
                                String name,

                                @NotNull(message = "Description cannot be null")
                                String description,

                                @NotNull(message = "Release date cannot be null")
                                @Past(message = "Release date must be in the past")
                                LocalDate releaseDate,

                                @Positive(message = "Duration must be positive")
                                long duration) {}
