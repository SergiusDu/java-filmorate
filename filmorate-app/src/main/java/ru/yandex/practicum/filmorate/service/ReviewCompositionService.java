package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.reviews.domain.model.Review;
import ru.yandex.practicum.filmorate.reviews.domain.port.CreateReviewCommand;
import ru.yandex.practicum.filmorate.reviews.domain.port.UpdateReviewCommand;
import ru.yandex.practicum.filmorate.reviews.application.port.in.ReviewUseCase;

import java.lang.module.ResolutionException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewCompositionService {
    private final ReviewUseCase reviewUseCase;

    public Review addReview(CreateReviewCommand command) {
        return reviewUseCase.addReview(command);
    }

    public Review updateReview(UpdateReviewCommand command) {
        return reviewUseCase.updateReview(command);
    }

    public boolean removeReview(long reviewId) {
        return reviewUseCase.removeReview(reviewId);
    }

    public Review getReviewById(long reviewId) {
        Optional<Review> review = reviewUseCase.getReviewById(reviewId);
        if (review.isPresent()) {
            return review.get();
        } else {
            throw new ResolutionException("Review with id " + reviewId + " was not found");
        }
    }

    public List<Review> getReviewsByFilmId(long filmId) {
        return reviewUseCase.getReviewsByFilmId(filmId).stream()
                .sorted(Comparator.comparingInt(Review::useful).reversed()).toList();
    }

    public List<Review> getAllReviews() {
        return reviewUseCase.getAllReviews().stream()
                .sorted(Comparator.comparingInt(Review::useful).reversed()).toList();
    }

    public boolean addLikeToReview(long reviewId, long userId) {
        /* вариант без модуля reactions
        Review review = getReviewById(reviewId);
        Integer useful = review.useful();
        updateReview(new UpdateReviewCommand(reviewId, review.content(), review.isPositive(), useful + 1, review.filmId(), review.userId()));*/
        return reviewUseCase.addLikeToReview(reviewId, userId);
    }

    public boolean addDislikeToReview(long reviewId, long userId) {
        return reviewUseCase.addDislikeToReview(reviewId, userId);
    }

    public boolean removeLikeFromReview(long reviewId, long userId) {
        return reviewUseCase.removeLikeFromReview(reviewId, userId);
    }

    public boolean removeDislikeFromReview(long reviewId, long userId) {
        return reviewUseCase.removeDislikeFromReview(reviewId, userId);
    }
}
