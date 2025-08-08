package ru.yandex.practicum.filmorate.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateDirectorRequest(@NotBlank(message = "Director name must not be empty")
                                    String name) {
}
