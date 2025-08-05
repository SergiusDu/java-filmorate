package ru.yandex.practicum.filmorate.films.application.port.in;

import jakarta.validation.constraints.Positive;
import java.time.Year;
import java.util.Optional;

/**
 * Query object for retrieving most popular films,
 * with optional filtering by genre, year, director
 * and customizable sorting strategy.
 */
public record FilmRatingQuery(
        @Positive int limit,
        Optional<Long> genreId,
        Optional<Integer> year,
        Optional<Long> directorId,
        SortBy sortBy
) {

    /**
     * Sorting strategies for popular films.
     */
    public enum SortBy {
        /** Sort by number of likes (used in legacy mode and current brief) */
        LIKES,

        /** Sort by average rating (planned in future refactor) */
        AVERAGE_RATING,

        /** Sort by number of ratings submitted */
        NUMBER_OF_RATINGS,

        /** Sort by release date (descending) */
        RELEASE_DATE
    }

    /**
     * Factory method for safely constructing a query with all optional values.
     *
     * @param limit      number of films to return (must be positive)
     * @param genreId    optional genre ID
     * @param year       optional release year
     * @param directorId optional director ID
     * @param sortBy     sorting method, defaults to LIKES if null
     * @return FilmRatingQuery
     */
    public static FilmRatingQuery of(
            int limit,
            Long genreId,
            Integer year,
            Long directorId,
            SortBy sortBy
    ) {
        if (limit <= 0) {
            throw new IllegalArgumentException("Limit must be positive.");
        }

        if (year != null && (year < 1895 || year > Year.now().getValue())) {
            throw new IllegalArgumentException("Year must be between 1895 and current year.");
        }

        return new FilmRatingQuery(
                limit,
                Optional.ofNullable(genreId),
                Optional.ofNullable(year),
                Optional.ofNullable(directorId),
                sortBy != null ? sortBy : SortBy.LIKES
        );
    }

    /**
     * Provides a default query: top 10 films sorted by LIKES.
     * Future versions may switch to AVERAGE_RATING here.
     */
    public static FilmRatingQuery ofDefault() {
        return of(10, null, null, null, null);
    }
}