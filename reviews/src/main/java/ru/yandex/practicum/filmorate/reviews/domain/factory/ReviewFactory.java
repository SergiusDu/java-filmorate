package ru.yandex.practicum.filmorate.reviews.domain.factory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.reviews.domain.model.Review;
import ru.yandex.practicum.filmorate.reviews.domain.port.CreateReviewCommand;
import ru.yandex.practicum.filmorate.reviews.domain.port.UpdateReviewCommand;

@Component
public class ReviewFactory {
    public Review create(long id, CreateReviewCommand command) {
        return Review.builder()
                .reviewId(id)
                .content(command.content())
                .isPositive(command.isPositive())
                .filmId(command.filmId())
                .userId(command.userId())
                .build();
    }

    public Review update(UpdateReviewCommand command) {
        return Review.builder()
                .reviewId(command.reviewId())
                .content(command.content())
                .isPositive(command.isPositive())
                .useful(command.useful())
                .filmId(command.filmId())
                .userId(command.userId())
                .build();
    }
}
