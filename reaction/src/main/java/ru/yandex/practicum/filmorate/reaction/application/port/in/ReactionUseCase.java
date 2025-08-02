package ru.yandex.practicum.filmorate.reaction.application.port.in;

import ru.yandex.practicum.filmorate.reaction.domain.model.Reaction;
import ru.yandex.practicum.filmorate.reaction.domain.model.ReactionType;
import java.util.Optional;

public interface ReactionUseCase {

    void addReaction(long reviewId, long userId, ReactionType reactionType);

    boolean removeReaction(long reviewId, long userId);

    Optional<Reaction> findReaction(long reviewId, long userId);
}