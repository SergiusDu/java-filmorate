package ru.yandex.practicum.filmorate.reviews.application.port.in;

import ru.yandex.practicum.filmorate.reviews.domain.model.Review;
import ru.yandex.practicum.filmorate.reviews.domain.port.CreateReviewCommand;
import ru.yandex.practicum.filmorate.reviews.domain.port.UpdateReviewCommand;

import java.util.List;
import java.util.Optional;

public interface ReviewUseCase {
    Review addReview(CreateReviewCommand command);

    Review updateReview(UpdateReviewCommand command);

    boolean removeReview(long reviewId);

    Optional<Review> getReviewById(long reviewId);

    List<Review> getReviewsByFilmId(long filmId);

    List<Review> getAllReviews();

    void changeUseful(long reviewId, int delta);

    Optional<Long> checkReviewForFilmExists(CreateReviewCommand command);
}
