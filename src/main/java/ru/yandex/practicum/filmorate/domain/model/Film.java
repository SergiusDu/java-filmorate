package ru.yandex.practicum.filmorate.domain.model;

import ru.yandex.practicum.filmorate.domain.exception.InvalidFilmDataException;
import ru.yandex.practicum.filmorate.domain.validation.ValidationUtils;

import java.time.Duration;
import java.time.LocalDate;
import java.util.UUID;

/**
 Represents a film entity containing core information about a movie. All fields are validated to ensure data integrity.
 @param id Unique identifier for the film
 @param name Title of the film (non-blank)
 @param description Plot summary or description of the film (non-blank)
 @param releaseDate Date when the film was originally released
 @param duration Total runtime length of the film */
public record Film
    (UUID id,
     String name,
     String description,
     LocalDate releaseDate,
     Duration duration) {

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

    ValidationUtils.positive(duration,
                             msg -> new InvalidFilmDataException("Film duration must be positive"));
  }
}