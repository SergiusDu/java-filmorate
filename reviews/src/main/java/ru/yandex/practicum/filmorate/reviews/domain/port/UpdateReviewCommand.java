package ru.yandex.practicum.filmorate.reviews.domain.port;

import lombok.Builder;

@Builder
public record UpdateReviewCommand(long reviewId, String content, Boolean isPositive, Integer useful, long userId, long filmId) {}
