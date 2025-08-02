package ru.yandex.practicum.filmorate.reaction.infrastructure.storage.jdbc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.reaction.domain.model.Reaction;
import ru.yandex.practicum.filmorate.reaction.domain.model.ReactionType;
import ru.yandex.practicum.filmorate.reaction.domain.port.ReactionRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
@Profile("db")
public class JdbcReactionRepository implements ReactionRepository {
    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<Reaction> REACTION_ROW_MAPPER = (rs, rowNum) -> new Reaction(
            rs.getLong("review_id"),
            rs.getLong("user_id"),
            ReactionType.valueOf(rs.getString("reaction"))
    );

    @Override
    public void addReaction(long reviewId, long userId, ReactionType reactionType) {
        String sql = "INSERT INTO reactions (review_id, user_id, reaction) VALUES(?, ?, ?)";
        try {
            jdbcTemplate.update(sql, reviewId, userId, reactionType.name());
        } catch (DuplicateKeyException e) {
            log.error("Reaction can not be inserted more than once");
        }
    }

    @Override
    public boolean removeReaction(long reviewId, long userId) {
        String sql = "DELETE FROM reactions WHERE review_id = ? AND user_id = ?";
        return jdbcTemplate.update(sql, reviewId, userId) > 0;
    }

    @Override
    public Optional<Reaction> findReaction(long reviewId, long userId) {
        String sql = "SELECT * FROM reactions WHERE review_id = ? AND user_id = ?";
        List<Reaction> reactions = jdbcTemplate.query(sql, REACTION_ROW_MAPPER, reviewId, userId);
        return reactions.stream().findFirst();
    }
}