package ru.yandex.practicum.filmorate.films.domain.model;

import lombok.Builder;
import ru.yandex.practicum.filmorate.common.exception.InvalidFilmDataException;
import ru.yandex.practicum.filmorate.common.validation.ValidationUtils;
import ru.yandex.practicum.filmorate.films.domain.model.value.Genre;
import ru.yandex.practicum.filmorate.films.domain.model.value.Mpa;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Set;

/**
 Represents a film record with validated core information. This record can be constructed using a builder pattern
 through
 {@code @Builder} annotation. All fields are validated during construction to ensure data integrity.
 @param id The unique identifier for the film (non-null)
 @param name The title of the film (non-blank)
 @param description The plot summary or description of the film (non-blank)
 @param releaseDate The original release date of the film (non-null)
 @param duration The total runtime length of the film (non-null, positive)
 @param genres The set of genres classifying the film (optional)
 @param mpa The MPAA rating classification of the film */
@Builder
public record Film(Long id,
                   String name,
                   String description,
                   LocalDate releaseDate,
                   Duration duration,
                   Set<Genre> genres,
                   Mpa mpa) {

  /**
   Validates all fields during record construction.
   @throws InvalidFilmDataException if any required field is null or blank
   */
  public Film {
    ValidationUtils.notNull(id, msg -> new InvalidFilmDataException("Film id must not be null"));
    ValidationUtils.notBlank(name, msg -> new InvalidFilmDataException("Film name must not be blank"));
    ValidationUtils.notBlank(description, msg -> new InvalidFilmDataException("Film description must not be blank"));
    ValidationUtils.notNull(releaseDate, msg -> new InvalidFilmDataException("Film release date must not be null"));
    ValidationUtils.notNull(duration, msg -> new InvalidFilmDataException("Film duration must not be null"));
    ValidationUtils.positive(duration, msg -> new InvalidFilmDataException("Film duration must be positive"));
  }
}