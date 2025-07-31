package ru.yandex.practicum.filmorate.reviews.domain.model;

import lombok.Builder;
import ru.yandex.practicum.filmorate.common.exception.InvalidReviewDataException;
import ru.yandex.practicum.filmorate.common.validation.ValidationUtils;

@Builder
public record Review(Long reviewId,
                   String content,
                   boolean isPositive,
                   Integer useful,
                   long userId,
                   long filmId) {
    /**
     Validates some Review fields. If invalid
     @throws InvalidReviewDataException
     If useful is NULL (like in the case of entity creation), it is set to default value 0
     */
    public Review {
        ValidationUtils.notNull(reviewId, msg -> new InvalidReviewDataException("Review id must not be null"));
        ValidationUtils.notBlank(content, msg -> new InvalidReviewDataException("Review content must not be blank"));
        ValidationUtils.notNull(isPositive, msg -> new InvalidReviewDataException("Review isPositive field must not null"));
        if (useful == null) useful = 0;
    }
}