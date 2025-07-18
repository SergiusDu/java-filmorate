package ru.yandex.practicum.filmorate.films.domain.model;

import ru.yandex.practicum.filmorate.common.exception.InvalidFilmDataException;
import ru.yandex.practicum.filmorate.common.validation.ValidationUtils;
import ru.yandex.practicum.filmorate.films.domain.model.value.Genre;
import ru.yandex.practicum.filmorate.films.domain.model.value.Mpa;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Set;

/**
 A record representing a film with validated core information.
 @param id The unique identifier for the film. Must not be null.
 @param name The title of the film. Must not be blank.
 @param description The plot summary or description of the film. Must not be blank.
 @param releaseDate The original release date of the film. Must not be null.
 @param duration The total runtime length of the film. Must not be null and must be positive.
 @param genres The set of genres classifying the film (e.g. Comedy, Drama, etc.). Optional.
 @param rating The MPAA mpa classification of the film (G, PG, PG-13, etc.). Must not be null. */
public record Film(Long id,
                   String name,
                   String description,
                   LocalDate releaseDate,
                   Duration duration,
                   Set<Genre> genres,
                   Mpa rating) {
  /**
   Validates all fields during record construction.
   @throws InvalidFilmDataException if any required field is null or blank
   */
  public Film {
    ValidationUtils.notNull(id,
                            msg -> new InvalidFilmDataException("Film id must not be null"));
    ValidationUtils.notBlank(name,
                             msg -> new InvalidFilmDataException("Film name must not be blank"));
    ValidationUtils.notBlank(description,
                             msg -> new InvalidFilmDataException("Film description must not be blank"));
    ValidationUtils.notNull(releaseDate,
                            msg -> new InvalidFilmDataException("Film release date must not be null"));
    ValidationUtils.notNull(duration,
                            msg -> new InvalidFilmDataException("Film duration must not be null"));
    ValidationUtils.notEmpty(genres,
                             msg -> new InvalidFilmDataException("Film genres must not be empty"));
    ValidationUtils.positive(duration,
                             msg -> new InvalidFilmDataException("Film duration must be positive"));
    ValidationUtils.notNull(rating,
                            msg -> new InvalidFilmDataException("Film mpa must not be null"));
  }
}