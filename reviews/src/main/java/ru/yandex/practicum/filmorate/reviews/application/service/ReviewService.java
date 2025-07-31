package ru.yandex.practicum.filmorate.reviews.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.reviews.application.port.in.ReviewUseCase;
import ru.yandex.practicum.filmorate.reviews.domain.model.Review;
import ru.yandex.practicum.filmorate.reviews.domain.port.CreateReviewCommand;
import ru.yandex.practicum.filmorate.reviews.domain.port.ReviewRepository;
import ru.yandex.practicum.filmorate.reviews.domain.port.UpdateReviewCommand;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService implements ReviewUseCase {

    private final ReviewRepository reviewRepository;

    @Override
    public Review addReview(CreateReviewCommand command) {
        return reviewRepository.addReview(command);
    }

    @Override
    public Review updateReview(UpdateReviewCommand command) {
        return reviewRepository.updateReview(command);
    }

    @Override
    public boolean removeReview(long reviewId) {
        return reviewRepository.removeReview(reviewId);
    }

    @Override
    public Optional<Review> getReviewById(long reviewId) {
        return reviewRepository.getReviewById(reviewId);
    }

    @Override
    public List<Review> getReviewsByFilmId(long filmId) {
        return reviewRepository.getReviewsByFilmId(filmId);
    }

    @Override
    public List<Review> getAllReviews() {
        return reviewRepository.getAllReviews();
    }

    @Override
    public boolean addLikeToReview(long reviewId, long userId) {
        return reviewRepository.addLikeToReview(reviewId, userId);
    }

    @Override
    public boolean addDislikeToReview(long reviewId, long userId) {
        return reviewRepository.addDislikeToReview(reviewId, userId);
    }

    @Override
    public boolean removeLikeFromReview(long reviewId, long userId) {
        return reviewRepository.removeLikeFromReview(reviewId, userId);
    }

    @Override
    public boolean removeDislikeFromReview(long reviewId, long userId) {
        return reviewRepository.removeDislikeFromReview(reviewId, userId);
    }
}
