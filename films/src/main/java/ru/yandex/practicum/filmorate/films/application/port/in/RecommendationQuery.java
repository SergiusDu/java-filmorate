package ru.yandex.practicum.filmorate.films.application.port.in;

import java.util.Optional;

/**
 * Query object for retrieving film recommendations using collaborative filtering.
 * Supports optional filtering by genre, release year, and result limit.
 *
 * @param userId  the ID of the user for whom recommendations are requested; must be positive
 * @param limit   optional maximum number of recommendations to return
 * @param genreId optional genre filter
 * @param year    optional release year filter
 */
public record RecommendationQuery(
        long userId,
        Optional<Integer> limit,
        Optional<Long> genreId,
        Optional<Integer> year
) {

    public RecommendationQuery {
        if (userId <= 0) {
            throw new IllegalArgumentException("userId must be positive");
        }
        limit = limit == null ? Optional.empty() : limit;
        genreId = genreId == null ? Optional.empty() : genreId;
        year = year == null ? Optional.empty() : year;
    }

    /**
     * Creates a basic query with no additional filters.
     *
     * @param userId the ID of the user for whom recommendations are requested
     * @return a query object with empty filters
     */
    public static RecommendationQuery of(long userId) {
        return new RecommendationQuery(userId, Optional.empty(), Optional.empty(), Optional.empty());
    }
}