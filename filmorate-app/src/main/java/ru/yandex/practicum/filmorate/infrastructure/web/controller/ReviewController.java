package ru.yandex.practicum.filmorate.infrastructure.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.infrastructure.web.dto.*;
import ru.yandex.practicum.filmorate.infrastructure.web.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.service.ReviewCompositionService;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewCompositionService reviewCompositionService;
    private final ReviewMapper reviewMapper;

    @PostMapping
    public ReviewResponse createReview(@Valid @RequestBody CreateReviewRequest request) {
        return reviewMapper.toResponse(reviewCompositionService.addReview(reviewMapper.toCommand(request)));
    }

    @PutMapping
    public ReviewResponse upateReview(@Valid @RequestBody UpdateReviewRequest request) {
        return reviewMapper.toResponse(reviewCompositionService.updateReview(reviewMapper.toCommand(request)));
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable long id) {
        reviewCompositionService.removeReview(id);
    }

    @GetMapping("/{reviewId}")
    public ReviewResponse getReviewById(@PathVariable long reviewId) {
        return reviewMapper.toResponse(reviewCompositionService.getReviewById(reviewId));
    }

    @GetMapping
    public List<ReviewResponse> getReviewByFilmId(@RequestParam(required = false) Long filmId,
                                                  @RequestParam(defaultValue = "10") int count) {

        if (filmId != null) {
            return reviewCompositionService.getReviewsByFilmId(filmId)
                    .stream()
                    .map(reviewMapper::toResponse)
                    .limit(count)
                    .toList();
        } else {
            return reviewCompositionService.getAllReviews()
                    .stream()
                    .map(reviewMapper::toResponse)
                    .limit(count)
                    .toList();
        }
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLikeToReview(@PathVariable long id, @PathVariable long userId) {
        reviewCompositionService.addLikeToReview(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLikeFromReview(@PathVariable long id, @PathVariable long userId) {
        reviewCompositionService.removeLikeFromReview(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislikeToReview(@PathVariable long id, @PathVariable long userId) {
        reviewCompositionService.addDislikeToReview(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void removeDislikeFromReview(@PathVariable long id, @PathVariable long userId) {
        reviewCompositionService.removeDislikeFromReview(id, userId);
    }


}
