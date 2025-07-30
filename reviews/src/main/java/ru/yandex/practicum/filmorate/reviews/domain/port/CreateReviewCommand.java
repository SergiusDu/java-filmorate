package ru.yandex.practicum.filmorate.reviews.domain.port;
import lombok.Builder;

@Builder
public record CreateReviewCommand(String content, boolean isPositive, long filmId, long userId, long l) {}

