package ru.yandex.practicum.filmorate.infrastructure.web.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record UpdateFilmRequest(@NotNull
                                UUID id,
                                @NotNull
                                String name,

                                @NotNull
                                String description,

                                @NotNull
                                LocalDate releaseDate,

                                @NotNull
                                long duration) {}
