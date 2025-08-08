package ru.yandex.practicum.filmorate.reaction.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.reaction.application.port.in.ReactionUseCase;
import ru.yandex.practicum.filmorate.reaction.domain.model.Reaction;
import ru.yandex.practicum.filmorate.reaction.domain.model.ReactionType;
import ru.yandex.practicum.filmorate.reaction.domain.port.ReactionRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReactionService implements ReactionUseCase {

    private final ReactionRepository reactionRepository;

    @Override
    public void addReaction(long reviewId, long userId, ReactionType reactionType) {
        reactionRepository.addReaction(reviewId, userId, reactionType);
    }

    @Override
    public boolean removeReaction(long reviewId, long userId) {
        return reactionRepository.removeReaction(reviewId, userId);
    }

    @Override
    public Optional<Reaction> findReaction(long reviewId, long userId) {
        return reactionRepository.findReaction(reviewId, userId);
    }
}
