package ru.yandex.practicum.filmorate.reaction.infrastructure.storage.jdbc;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.yaml.snakeyaml.constructor.DuplicateKeyException;
import ru.yandex.practicum.filmorate.reaction.domain.port.ReactionRepository;

@Repository
@RequiredArgsConstructor
@Profile("db")
public class JdbcReactionRepository implements ReactionRepository {
    private final JdbcTemplate jdbcTemplate;
/*
    @Override
    public boolean addLike(long reviewId, long userId) {
        String sql = "INSERT INTO reactions (reaction, review_id, user_id) VALUES(?, ?, ?)";
        try {
            jdbcTemplate.update(sql, "LIKE", reviewId, userId);
            String updateUseful = "UPDATE reviews SET useful = useful + 1 WHERE review_id = ?";
            jdbcTemplate.update(updateUseful, reviewId);
            return true;
        } catch (DuplicateKeyException | DataAccessException e) {
            return false;
        }
    }

    @Override
    public boolean removeLike(long reviewId, long userId) {
        String sql = "DELETE FROM reactions WHERE review_id = ? AND film_id = ?";
        try {
            String updateUseful = "UPDATE reviews SET useful = useful - 1 WHERE review_id = ?";
            jdbcTemplate.update(updateUseful, reviewId);
        } catch (DataAccessException e) {
            return false;
        }
        return jdbcTemplate.update(sql, userId, reviewId) > 0;
    }

    @Override
    public boolean addDislike(long reviewId, long userId) {
        String sql = "INSERT INTO reactions (reaction, review_id, user_id) VALUES(?, ?, ?)";
        try {
            jdbcTemplate.update(sql, "DISLIKE", reviewId, userId);
            String updateUseful = "UPDATE reviews SET useful = useful - 1 WHERE review_id = ?";
            jdbcTemplate.update(updateUseful, reviewId);
            return true;
        } catch (DuplicateKeyException | DataAccessException e) {
            return false;
        }
    }

    @Override
    public boolean removeDislike(long reviewId, long userId) {
        String sql = "DELETE FROM reactions WHERE review_id = ? AND film_id = ?";
        try {
            String updateUseful = "UPDATE reviews SET useful = useful + 1 WHERE review_id = ?";
            jdbcTemplate.update(updateUseful, reviewId);
        } catch (DataAccessException e) {
            return false;
        }
        return jdbcTemplate.update(sql, userId, reviewId) > 0;
    } */
}
