package ru.yandex.practicum.filmorate.reviews.domain.port;

import ru.yandex.practicum.filmorate.reviews.domain.model.Review;
import java.util.List;
import java.util.Optional;

public interface ReviewRepository {
    Review addReview(CreateReviewCommand command);

    Review updateReview(UpdateReviewCommand command);

    boolean removeReview(long reviewId);

    Optional<Review> getReviewById(long reviewId);

    List<Review> getReviewsByFilmId(long filmId);

    List<Review> getAllReviews();

    void changeUseful(long reviewId, int delta);
}
