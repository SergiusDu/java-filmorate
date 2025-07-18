package ru.yandex.practicum.filmorate.films.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.yandex.practicum.filmorate.common.exception.InvalidFilmDataException;
import ru.yandex.practicum.filmorate.films.domain.model.value.Genre;
import ru.yandex.practicum.filmorate.films.domain.model.value.Mpa;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Film Domain Model")
class FilmTest {

  private Long validId;
  private String validName;
  private String validDescription;
  private LocalDate validReleaseDate;
  private Duration validDuration;
  private Set<Genre> validGenres;
  private Mpa validRating;

  @BeforeEach
  void setUp() {
    validId = 1L;
    validName = "Inception";
    validDescription = "A mind-bending thriller.";
    validReleaseDate = LocalDate.of(2010,
                                    7,
                                    16);
    validDuration = Duration.ofMinutes(148);
    validGenres = Set.of(new Genre(4L,
                                   "Thriller"));
    validRating = new Mpa(3L,
                          "PG-13");
  }

  @Test
  @DisplayName("Should create a film successfully with valid data")
  void shouldCreateFilm_whenAllDataIsValid() {
    Film film = new Film(validId,
                         validName,
                         validDescription,
                         validReleaseDate,
                         validDuration,
                         validGenres,
                         validRating);

    assertThat(film.id()).isEqualTo(validId);
    assertThat(film.name()).isEqualTo(validName);
    assertThat(film.description()).isEqualTo(validDescription);
    assertThat(film.releaseDate()).isEqualTo(validReleaseDate);
    assertThat(film.duration()).isEqualTo(validDuration);
    assertThat(film.genres()).isEqualTo(validGenres);
    assertThat(film.rating()).isEqualTo(validRating);
  }

  @Nested
  @DisplayName("ID Validation")
  class IdValidation {
    @Test
    @DisplayName("Should throw exception when id is null")
    void shouldThrowException_whenIdIsNull() {
      var exception = assertThrows(InvalidFilmDataException.class,
                                   () -> new Film(null,
                                                  validName,
                                                  validDescription,
                                                  validReleaseDate,
                                                  validDuration,
                                                  validGenres,
                                                  validRating));
      assertThat(exception.getMessage()).isEqualTo("Film id must not be null");
    }
  }

  @Nested
  @DisplayName("Name Validation")
  class NameValidation {
    @ParameterizedTest
    @ValueSource(strings = {"  ",
                            "\t",
                            "\n"
    })
    @DisplayName("Should throw exception when name is blank")
    void shouldThrowException_whenNameIsBlank(String invalidName) {
      var exception = assertThrows(InvalidFilmDataException.class,
                                   () -> new Film(validId,
                                                  invalidName,
                                                  validDescription,
                                                  validReleaseDate,
                                                  validDuration,
                                                  validGenres,
                                                  validRating));
      assertThat(exception.getMessage()).isEqualTo("Film name must not be blank");
    }
  }

  @Nested
  @DisplayName("Description Validation")
  class DescriptionValidation {
    @ParameterizedTest
    @ValueSource(strings = {"  ",
                            "\t",
                            "\n"
    })
    @DisplayName("Should throw exception when description is blank")
    void shouldThrowException_whenDescriptionIsBlank(String invalidDescription) {
      var exception = assertThrows(InvalidFilmDataException.class,
                                   () -> new Film(validId,
                                                  validName,
                                                  invalidDescription,
                                                  validReleaseDate,
                                                  validDuration,
                                                  validGenres,
                                                  validRating));
      assertThat(exception.getMessage()).isEqualTo("Film description must not be blank");
    }
  }

  @Nested
  @DisplayName("Release Date Validation")
  class ReleaseDateValidation {
    @Test
    @DisplayName("Should throw exception when release date is null")
    void shouldThrowException_whenReleaseDateIsNull() {
      var exception = assertThrows(InvalidFilmDataException.class,
                                   () -> new Film(validId,
                                                  validName,
                                                  validDescription,
                                                  null,
                                                  validDuration,
                                                  validGenres,
                                                  validRating));
      assertThat(exception.getMessage()).isEqualTo("Film release date must not be null");
    }
  }

  @Nested
  @DisplayName("Duration Validation")
  class DurationValidation {
    @Test
    @DisplayName("Should throw exception when duration is null")
    void shouldThrowException_whenDurationIsNull() {
      var exception = assertThrows(InvalidFilmDataException.class,
                                   () -> new Film(validId,
                                                  validName,
                                                  validDescription,
                                                  validReleaseDate,
                                                  null,
                                                  validGenres,
                                                  validRating));
      assertThat(exception.getMessage()).isEqualTo("Film duration must not be null");
    }

    @Test
    @DisplayName("Should throw exception when duration is zero")
    void shouldThrowException_whenDurationIsZero() {
      var exception = assertThrows(InvalidFilmDataException.class,
                                   () -> new Film(validId,
                                                  validName,
                                                  validDescription,
                                                  validReleaseDate,
                                                  Duration.ZERO,
                                                  validGenres,
                                                  validRating));
      assertThat(exception.getMessage()).isEqualTo("Film duration must be positive");
    }

    @Test
    @DisplayName("Should throw exception when duration is negative")
    void shouldThrowException_whenDurationIsNegative() {
      var exception = assertThrows(InvalidFilmDataException.class,
                                   () -> new Film(validId,
                                                  validName,
                                                  validDescription,
                                                  validReleaseDate,
                                                  Duration.ofMinutes(-1),
                                                  validGenres,
                                                  validRating));
      assertThat(exception.getMessage()).isEqualTo("Film duration must be positive");
    }
  }

  @Nested
  @DisplayName("Genres Validation")
  class GenresValidation {
    @Test
    @DisplayName("Should throw exception when genres set is empty")
    void shouldThrowException_whenGenresAreEmpty() {
      var exception = assertThrows(InvalidFilmDataException.class,
                                   () -> new Film(validId,
                                                  validName,
                                                  validDescription,
                                                  validReleaseDate,
                                                  validDuration,
                                                  Collections.emptySet(),
                                                  validRating));
      assertThat(exception.getMessage()).isEqualTo("Film genres must not be empty");
    }
  }

  @Nested
  @DisplayName("Rating Validation")
  class RatingValidation {
    @Test
    @DisplayName("Should throw exception when mpa is null")
    void shouldThrowException_whenRatingIsNull() {
      var exception = assertThrows(InvalidFilmDataException.class,
                                   () -> new Film(validId,
                                                  validName,
                                                  validDescription,
                                                  validReleaseDate,
                                                  validDuration,
                                                  validGenres,
                                                  null));
      assertThat(exception.getMessage()).isEqualTo("Film mpa must not be null");
    }
  }
}