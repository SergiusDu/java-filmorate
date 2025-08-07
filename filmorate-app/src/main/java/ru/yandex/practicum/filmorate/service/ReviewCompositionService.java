package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.common.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.events.domain.model.value.Operation;
import ru.yandex.practicum.filmorate.events.domain.service.DomainEventPublisher;
import ru.yandex.practicum.filmorate.reaction.application.port.in.ReactionUseCase;
import ru.yandex.practicum.filmorate.reaction.domain.model.Reaction;
import ru.yandex.practicum.filmorate.reaction.domain.model.ReactionType;
import ru.yandex.practicum.filmorate.reviews.application.port.in.ReviewUseCase;
import ru.yandex.practicum.filmorate.reviews.domain.model.Review;
import ru.yandex.practicum.filmorate.reviews.domain.port.CreateReviewCommand;
import ru.yandex.practicum.filmorate.reviews.domain.port.UpdateReviewCommand;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewCompositionService {
    private final ReviewUseCase reviewUseCase;
    private final ReactionUseCase reactionUseCase;
    private final FilmCompositionService filmService;
    private final UserCompositionService userService;
    private final DomainEventPublisher domainEventPublisher;

    public Review addReview(CreateReviewCommand command) {
        filmService.validateFilmExists(command.filmId());
        userService.validateUserExists(command.userId());

        if (reviewUseCase.checkReviewForFilmExists(command)
                .isPresent()) {
            long reviewId = reviewUseCase.checkReviewForFilmExists(command)
                    .get();
            reviewUseCase.removeReview(reviewId);
        }
        Review review = reviewUseCase.addReview(command);
        domainEventPublisher.publishReviewEvent(command.userId(), Operation.ADD, review.reviewId());
        return review;
    }

    public Review updateReview(UpdateReviewCommand command) {
        Optional<Review> reviewOld = reviewUseCase.getReviewById(command.reviewId());
        if (reviewOld.isPresent()) {
            command = new UpdateReviewCommand(command.reviewId(), command.content(), command.isPositive(), command.useful(),
                    reviewOld.get().userId(), reviewOld.get().filmId());
        }
        Review review = reviewUseCase.updateReview(command);
        domainEventPublisher.publishReviewEvent(command.userId(), Operation.UPDATE, command.reviewId());
        return review;
    }

    public boolean removeReview(long reviewId) {
        Review review = reviewUseCase.getReviewById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review with id " + reviewId + " was not found"));
        boolean removed = reviewUseCase.removeReview(reviewId);
        if (removed) {
            domainEventPublisher.publishReviewEvent(review.userId(), Operation.REMOVE, reviewId);
        }
        return removed;
    }

    public Review getReviewById(long reviewId) {
        return reviewUseCase.getReviewById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Review with id " + reviewId + " was not found"));
    }

    public List<Review> getReviewsByFilmId(long filmId) {
        filmService.validateFilmExists(filmId);
        return reviewUseCase.getReviewsByFilmId(filmId)
                .stream()
                .sorted(Comparator.comparingInt(Review::useful).reversed())
                .toList();
    }

    public List<Review> getAllReviews() {
        return reviewUseCase.getAllReviews()
                .stream()
                .sorted(Comparator.comparingInt(Review::useful).reversed())
                .toList();
    }

    @Transactional
    public void addLikeToReview(long reviewId, long userId) {
        handleReaction(reviewId, userId, ReactionType.LIKE);
    }

    private void handleReaction(long reviewId, long userId, ReactionType newReactionType) {
        Optional<Reaction> reactionOpt = reactionUseCase.findReaction(reviewId, userId);
        int delta = 0;

        if (reactionOpt.isPresent()) {
            Reaction existingReaction = reactionOpt.get();
            if (existingReaction.type() == newReactionType) {
                throw new DuplicateKeyException("Reaction can be added only once");
            }

            reactionUseCase.removeReaction(reviewId, userId);

            delta += (existingReaction.type() == ReactionType.LIKE) ? -1 : 1;
        }

        reactionUseCase.addReaction(reviewId, userId, newReactionType);
        delta += (newReactionType == ReactionType.LIKE) ? 1 : -1;

        if (delta != 0) {
            reviewUseCase.changeUseful(reviewId, delta);
        }
    }

    @Transactional
    public void addDislikeToReview(long reviewId, long userId) {
        handleReaction(reviewId, userId, ReactionType.DISLIKE);
    }

    @Transactional
    public void removeLikeFromReview(long reviewId, long userId) {
        removeReaction(reviewId, userId, ReactionType.LIKE);
    }

    private void removeReaction(long reviewId, long userId, ReactionType reactionToRemove) {
        Optional<Reaction> existingReactionOpt = reactionUseCase.findReaction(reviewId, userId);

        if (existingReactionOpt.isPresent() && existingReactionOpt.get()
                .type() == reactionToRemove) {
            if (reactionUseCase.removeReaction(reviewId, userId)) {
                int delta = (reactionToRemove == ReactionType.LIKE) ? -1 : 1;
                reviewUseCase.changeUseful(reviewId, delta);
            }
        }
    }

    @Transactional
    public void removeDislikeFromReview(long reviewId, long userId) {
        removeReaction(reviewId, userId, ReactionType.DISLIKE);
    }
}
