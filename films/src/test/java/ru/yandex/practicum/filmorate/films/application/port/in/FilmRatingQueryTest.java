package ru.yandex.practicum.filmorate.films.application.port.in;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmRatingQueryTest {

    @Test
    void shouldCreateValidQueryWithDefaults() {
        FilmRatingQuery query = FilmRatingQuery.of(10, null, null, null, null);

        assertThat(query.limit()).isEqualTo(10);
        assertThat(query.genreId()).isEmpty();
        assertThat(query.year()).isEmpty();
        assertThat(query.directorId()).isEmpty();
        assertThat(query.sortBy()).isEqualTo(FilmRatingQuery.SortBy.LIKES);
    }

    @Test
    void shouldThrowIfLimitIsZero() {
        assertThrows(IllegalArgumentException.class,
                () -> FilmRatingQuery.of(0, null, null, null, null));
    }

    @Test
    void shouldThrowIfYearIsTooEarly() {
        assertThrows(IllegalArgumentException.class,
                () -> FilmRatingQuery.of(10, null, 1800, null, null));
    }

    @Test
    void shouldThrowIfYearIsInFuture() {
        int nextYear = java.time.Year.now().getValue() + 1;
        assertThrows(IllegalArgumentException.class,
                () -> FilmRatingQuery.of(10, null, nextYear, null, null));
    }

    @Test
    void shouldUseProvidedSortByIfNotNull() {
        var query = FilmRatingQuery.of(10, null, null, null, FilmRatingQuery.SortBy.AVERAGE_RATING);
        assertThat(query.sortBy()).isEqualTo(FilmRatingQuery.SortBy.AVERAGE_RATING);
    }

    @Test
    void shouldCreateDefaultQuery() {
        var query = FilmRatingQuery.ofDefault();
        assertThat(query.limit()).isEqualTo(10);
        assertThat(query.sortBy()).isEqualTo(FilmRatingQuery.SortBy.LIKES);
    }
}