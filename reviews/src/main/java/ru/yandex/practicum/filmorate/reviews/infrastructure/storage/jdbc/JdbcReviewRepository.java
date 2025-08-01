package ru.yandex.practicum.filmorate.reviews.infrastructure.storage.jdbc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.common.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.reviews.domain.factory.ReviewFactory;
import ru.yandex.practicum.filmorate.reviews.domain.model.Review;
import ru.yandex.practicum.filmorate.reviews.domain.port.CreateReviewCommand;
import ru.yandex.practicum.filmorate.reviews.domain.port.ReviewRepository;
import ru.yandex.practicum.filmorate.reviews.domain.port.UpdateReviewCommand;

import java.util.*;


@Slf4j
@Repository
@RequiredArgsConstructor
@Profile("db")
public class JdbcReviewRepository implements ReviewRepository {
    private final JdbcTemplate jdbcTemplate;
    private final ReviewFactory reviewFactory;


    @Override
    public Review addReview(CreateReviewCommand command) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("reviews")
                .usingGeneratedKeyColumns("review_id");

        Map<String, Object> params = Map.of("content", command.content(), "is_positive", command.isPositive(),
                "film_id", command.filmId(), "user_id", command.userId());

        long reviewId = simpleJdbcInsert.executeAndReturnKey(params)
                .longValue();

        return reviewFactory.create(reviewId, command);
    }

    @Override
    public Review updateReview(UpdateReviewCommand command) {
        String sql = "UPDATE reviews SET content = ?, is_positive = ?, useful = ?, user_id = ?, film_id = ? " +
                "WHERE review_id = ?";
        int rowsAffected = jdbcTemplate.update(sql, command.content(), command.isPositive(), command.useful(),
                command.userId(), command.filmId(), command.reviewId());

        if (rowsAffected == 0) {
            throw new ResourceNotFoundException("Review with id " + command.reviewId() + " not found.");
        }

        return reviewFactory.update(command);
    }

    @Override
    public boolean removeReview(long reviewId) {
        String removeReviewQuery = "DELETE FROM reviews WHERE review_id = ?";
        return jdbcTemplate.update(removeReviewQuery, reviewId) > 0;
    }

    @Override
    public Optional<Review> getReviewById(long reviewId) {
        List<Review> reviews = mapRowsToReviews(jdbcTemplate.queryForList("SELECT * FROM reviews WHERE review_id = ?", reviewId));
        return reviews.stream().findFirst();
    }

    @Override
    public List<Review> getReviewsByFilmId(long filmId) {
        return mapRowsToReviews(jdbcTemplate.queryForList("SELECT * FROM reviews WHERE film_id = ?", filmId));
    }

    @Override
    public List<Review> getAllReviews() {
        return mapRowsToReviews(jdbcTemplate.queryForList("SELECT * FROM reviews"));
    }

    private List<Review> mapRowsToReviews(List<Map<String, Object>> rows) {
        if (rows.isEmpty()) {
            return List.of();
        }

        return rows.stream()
                .map(row -> {
                    long reviewId = (Long) row.get("review_id");
                    return new Review(reviewId,
                            (String) row.get("content"),
                            (Boolean) row.get("is_positive"),
                            (Integer) row.get("useful"),
                            (Long) row.get("user_id"),
                            (Long) row.get("film_id"));
                }).toList();
    }

    @Override
    public void changeUseful(long reviewId, int delta) {
        Optional<Review> reviewOpt = getReviewById(reviewId);

        Integer newUseful = 0;

        if (reviewOpt.isPresent()) {
            Review review = reviewOpt.get();

            newUseful = review.useful() + delta;

            updateReview(new UpdateReviewCommand(reviewId, review.content(), review.isPositive(),
                    newUseful, review.filmId(), review.userId()));
        }
       // String sql = "UPDATE reviews SET useful = useful + ? WHERE review_id = ?";
       // jdbcTemplate.update(sql, delta, reviewId);
    }
}
