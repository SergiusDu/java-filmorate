package ru.yandex.practicum.filmorate.reviews.domain.port;
import lombok.Builder;

@Builder
public record CreateReviewCommand(String content, Boolean isPositive, long userId, long filmId) {}

