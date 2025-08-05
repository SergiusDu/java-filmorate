package ru.yandex.practicum.filmorate.infrastructure.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import ru.yandex.practicum.filmorate.common.validation.ValidReleaseDate;
import ru.yandex.practicum.filmorate.films.domain.model.value.Genre;

import java.time.LocalDate;
import java.util.Set;

public record UpdateFilmRequest(@NotNull(message = "Film ID cannot be null")
                                Long id,
                                @NotBlank(message = "Name cannot be null")
                                String name,

                                @NotBlank(message = "Description cannot be null")
                                String description,

                                @NotNull(message = "Release date cannot be null")
                                @ValidReleaseDate(message = "Release date must be between the earliest allowed date " +
                                                            "and today")
                                LocalDate releaseDate,

                                @Positive(message = "Duration must be positive")
                                long duration,

                                Set<Genre> genres,

                                @Valid
                                MpaIdDto mpa,

                                Set<DirectorIdDto> directors) {
}