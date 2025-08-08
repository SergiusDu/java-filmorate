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
        Integer limit,   // nullable
        Long genreId,    // nullable
        Integer year     // nullable
) {
    public RecommendationQuery {
        if (userId <= 0) {
            throw new IllegalArgumentException("userId must be positive");
        }
    }

    public Optional<Integer> limitOpt() {
        return Optional.ofNullable(limit);
    }

    public Optional<Long> genreIdOpt() {
        return Optional.ofNullable(genreId);
    }

    public Optional<Integer> yearOpt() {
        return Optional.ofNullable(year);
    }

    /**
     * Creates a basic query with no additional filters.
     *
     * @param userId the ID of the user for whom recommendations are requested
     * @return a query object with empty filters
     */
    public static RecommendationQuery of(long userId) {
        return new RecommendationQuery(userId, null, null, null);
    }
}