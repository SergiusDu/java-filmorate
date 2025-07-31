package ru.yandex.practicum.filmorate.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.reviews.domain.model.Review;
import ru.yandex.practicum.filmorate.reviews.domain.port.CreateReviewCommand;
import ru.yandex.practicum.filmorate.reviews.domain.port.UpdateReviewCommand;
import ru.yandex.practicum.filmorate.infrastructure.web.dto.CreateReviewRequest;
import ru.yandex.practicum.filmorate.infrastructure.web.dto.ReviewResponse;
import ru.yandex.practicum.filmorate.infrastructure.web.dto.UpdateReviewRequest;

@Component
public class ReviewMapper {
    public CreateReviewCommand toCommand(CreateReviewRequest request) {
        return new CreateReviewCommand(request.content(), request.isPositive(), request.userId(),
                request.filmId());
    }

    public UpdateReviewCommand toCommand(UpdateReviewRequest request) {
        return UpdateReviewCommand.builder()
                .reviewId(request.reviewId())
                .content(request.content())
                .isPositive(request.isPositive())
                .useful(request.useful())
                .userId(request.userId())
                .filmId(request.filmId())
                .build();
    }

    public ReviewResponse toResponse(Review review) {
        return ReviewResponse.builder()
                .reviewId(review.reviewId())
                .content(review.content())
                .isPositive(review.isPositive())
                .useful(review.useful())
                .userId(review.userId())
                .filmId(review.filmId())
                .build();
    }
}