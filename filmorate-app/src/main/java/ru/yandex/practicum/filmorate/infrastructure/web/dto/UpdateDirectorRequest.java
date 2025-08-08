package ru.yandex.practicum.filmorate.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UpdateDirectorRequest(@NotNull(message = "ID cannot be null")
                                    @Positive(message = "ID must be a positive number")
                                    long id,

                                    @NotBlank
                                    String name) {
}
