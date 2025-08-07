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
    validReleaseDate = LocalDate.of(2010, 7, 16);
    validDuration = Duration.ofMinutes(148);
    validGenres = Set.of(new Genre(4L, "Thriller"));
    validRating = new Mpa(3L, "PG-13");
  }

  @Test
  @DisplayName("Should create a film successfully with valid data")
  void shouldCreateFilm_whenAllDataIsValid() {
    Film film = Film.builder()
            .id(validId)
            .name(validName)
            .description(validDescription)
            .releaseDate(validReleaseDate)
            .duration(validDuration)
            .genres(validGenres)
            .mpa(validRating)
            .build();

    assertThat(film.id()).isEqualTo(validId);
    assertThat(film.name()).isEqualTo(validName);
    assertThat(film.description()).isEqualTo(validDescription);
    assertThat(film.releaseDate()).isEqualTo(validReleaseDate);
    assertThat(film.duration()).isEqualTo(validDuration);
    assertThat(film.genres()).isEqualTo(validGenres);
    assertThat(film.mpa()).isEqualTo(validRating);
  }

  @Nested
  @DisplayName("Validation Tests")
  class ValidationTests {

    @Test
    void shouldThrow_whenIdIsNull() {
      var ex = assertThrows(InvalidFilmDataException.class, () ->
              Film.builder()
                      .id(null)
                      .name(validName)
                      .description(validDescription)
                      .releaseDate(validReleaseDate)
                      .duration(validDuration)
                      .genres(validGenres)
                      .mpa(validRating)
                      .build()
      );
      assertThat(ex.getMessage()).isEqualTo("Film id must not be null");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "  ", "\n"})
    void shouldThrow_whenNameIsBlank(String name) {
      var ex = assertThrows(InvalidFilmDataException.class, () ->
              Film.builder()
                      .id(validId)
                      .name(name)
                      .description(validDescription)
                      .releaseDate(validReleaseDate)
                      .duration(validDuration)
                      .genres(validGenres)
                      .mpa(validRating)
                      .build()
      );
      assertThat(ex.getMessage()).isEqualTo("Film name must not be blank");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "  ", "\n"})
    void shouldThrow_whenDescriptionIsBlank(String description) {
      var ex = assertThrows(InvalidFilmDataException.class, () ->
              Film.builder()
                      .id(validId)
                      .name(validName)
                      .description(description)
                      .releaseDate(validReleaseDate)
                      .duration(validDuration)
                      .genres(validGenres)
                      .mpa(validRating)
                      .build()
      );
      assertThat(ex.getMessage()).isEqualTo("Film description must not be blank");
    }

    @Test
    void shouldThrow_whenReleaseDateIsNull() {
      var ex = assertThrows(InvalidFilmDataException.class, () ->
              Film.builder()
                      .id(validId)
                      .name(validName)
                      .description(validDescription)
                      .releaseDate(null)
                      .duration(validDuration)
                      .genres(validGenres)
                      .mpa(validRating)
                      .build()
      );
      assertThat(ex.getMessage()).isEqualTo("Film release date must not be null");
    }

    @Test
    void shouldThrow_whenDurationIsNull() {
      var ex = assertThrows(InvalidFilmDataException.class, () ->
              Film.builder()
                      .id(validId)
                      .name(validName)
                      .description(validDescription)
                      .releaseDate(validReleaseDate)
                      .duration(null)
                      .genres(validGenres)
                      .mpa(validRating)
                      .build()
      );
      assertThat(ex.getMessage()).isEqualTo("Film duration must not be null");
    }

    @Test
    void shouldThrow_whenDurationIsNegative() {
      var ex = assertThrows(InvalidFilmDataException.class, () ->
              Film.builder()
                      .id(validId)
                      .name(validName)
                      .description(validDescription)
                      .releaseDate(validReleaseDate)
                      .duration(Duration.ofMinutes(-10))
                      .genres(validGenres)
                      .mpa(validRating)
                      .build()
      );
      assertThat(ex.getMessage()).isEqualTo("Film duration must be positive");
    }
  }
}