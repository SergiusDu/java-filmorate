package ru.yandex.practicum.filmorate.reaction.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.reaction.application.port.in.ReactionUseCase;
import ru.yandex.practicum.filmorate.reaction.domain.port.ReactionRepository;


@Service
@RequiredArgsConstructor
public class ReactionService implements ReactionUseCase {
    private final ReactionRepository reactionRepository;
    @Override
    public boolean addLike(long reviewId, long userId) {
        return reactionRepository.addLike(reviewId, userId);
    }

    @Override
    public boolean removeLike(long reviewId, long userId) {
        return reactionRepository.removeLike(reviewId, userId);
    }

    @Override
    public boolean addDislike(long reviewId, long userId) {
        return reactionRepository.addDislike(reviewId, userId);
    }

    @Override
    public boolean removeDislike(long reviewId, long userId) {
        return reactionRepository.removeDislike(reviewId, userId);
    }
}
