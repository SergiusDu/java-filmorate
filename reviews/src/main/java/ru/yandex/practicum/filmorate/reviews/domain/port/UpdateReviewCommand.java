package ru.yandex.practicum.filmorate.reviews.domain.port;

import lombok.Builder;

@Builder
public record UpdateReviewCommand(long reviewId, String content, boolean isPositive, Integer useful, long filmId, long userId) {}
