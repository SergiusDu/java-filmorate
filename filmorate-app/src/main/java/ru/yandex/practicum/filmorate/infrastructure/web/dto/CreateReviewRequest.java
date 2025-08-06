package ru.yandex.practicum.filmorate.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateReviewRequest(@NotBlank(message = "Content cannot be empty")
                                  String content,

                                  @NotNull(message = "Is positive field cannot be null")
                                  Boolean isPositive,

                                  @NotNull(message = "User id cannot be null")
                                  Long userId,

                                  @NotNull(message = "Film id cannot be null")
                                  Long filmId) {
}

