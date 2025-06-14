package ru.yandex.practicum.filmorate.films.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import ru.yandex.practicum.filmorate.common.exception.InvalidFilmDataException;

import java.time.Duration;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmTest {

  private UUID validId;
  private String validName;
  private String validDescription;
  private LocalDate validReleaseDate;
  private Duration validDuration;

  @BeforeEach
  void setUp() {
    validId = UUID.randomUUID();
    validName = "Inception";
    validDescription = "A mind-bending thriller.";
    validReleaseDate = LocalDate.of(2010,
                                    7,
                                    16);
    validDuration = Duration.ofMinutes(148);
  }

  @Test
  void shouldCreateFilm_whenAllDataIsValid() {
    Film film = new Film(validId,
                         validName,
                         validDescription,
                         validReleaseDate,
                         validDuration);

    assertThat(film.id()).isEqualTo(validId);
    assertThat(film.name()).isEqualTo(validName);
    assertThat(film.description()).isEqualTo(validDescription);
    assertThat(film.releaseDate()).isEqualTo(validReleaseDate);
    assertThat(film.duration()).isEqualTo(validDuration);
  }

  @Test
  void shouldThrowException_whenIdIsNull() {
    var exception = assertThrows(InvalidFilmDataException.class,
                                 () -> new Film(null,
                                                validName,
                                                validDescription,
                                                validReleaseDate,
                                                validDuration));
    assertThat(exception.getMessage()).isEqualTo("Film id must not be null");
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"  ", "\t", "\n"})
  void shouldThrowException_whenNameIsBlank(String invalidName) {
    var exception = assertThrows(InvalidFilmDataException.class,
                                 () -> new Film(validId,
                                                invalidName,
                                                validDescription,
                                                validReleaseDate,
                                                validDuration));
    assertThat(exception.getMessage()).isEqualTo("Film name must not be blank");
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"  ", "\t", "\n"})
  void shouldThrowException_whenDescriptionIsBlank(String invalidDescription) {
    var exception = assertThrows(InvalidFilmDataException.class,
                                 () -> new Film(validId,
                                                validName,
                                                invalidDescription,
                                                validReleaseDate,
                                                validDuration));
    assertThat(exception.getMessage()).isEqualTo("Film description must not be blank");
  }

  @Test
  void shouldThrowException_whenReleaseDateIsNull() {
    var exception = assertThrows(InvalidFilmDataException.class,
                                 () -> new Film(validId,
                                                validName,
                                                validDescription,
                                                null,
                                                validDuration));
    assertThat(exception.getMessage()).isEqualTo("Film release date must not be null");
  }

  @Test
  void shouldThrowException_whenDurationIsNull() {
    var exception = assertThrows(InvalidFilmDataException.class,
                                 () -> new Film(validId,
                                                validName,
                                                validDescription,
                                                validReleaseDate,
                                                null));
    assertThat(exception.getMessage()).isEqualTo("Film duration must not be null");
  }

  @Test
  void shouldThrowException_whenDurationIsZero() {
    var exception = assertThrows(InvalidFilmDataException.class,
                                 () -> new Film(validId,
                                                validName,
                                                validDescription,
                                                validReleaseDate,
                                                Duration.ZERO));
    assertThat(exception.getMessage()).isEqualTo("Film duration must be positive");
  }

  @Test
  void shouldThrowException_whenDurationIsNegative() {
    var exception = assertThrows(InvalidFilmDataException.class,
                                 () -> new Film(validId,
                                                validName,
                                                validDescription,
                                                validReleaseDate,
                                                Duration.ofMinutes(-1)));
    assertThat(exception.getMessage()).isEqualTo("Film duration must be positive");
  }
}