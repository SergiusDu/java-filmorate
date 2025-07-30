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

    boolean addLikeToReview(long reviewId, long userId);

    boolean addDislikeToReview(long reviewId, long userId);

    boolean removeLikeFromReview(long reviewId, long userId);

    boolean removeDislikeFromReview(long reviewId, long userId);
}
