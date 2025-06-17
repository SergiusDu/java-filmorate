package ru.yandex.practicum.filmorate.films.domain.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.common.config.AppValidationProperties;
import ru.yandex.practicum.filmorate.common.exception.InvalidFilmDataException;
import ru.yandex.practicum.filmorate.films.domain.port.CreateFilmCommand;
import ru.yandex.practicum.filmorate.films.domain.port.UpdateFilmCommand;

import java.time.LocalDate;

@Slf4j
@Service
public class FilmValidationService {
  private final int maxDescriptionLength;
  private final LocalDate earliestReleaseDate;

  public FilmValidationService(AppValidationProperties properties) {
    this.maxDescriptionLength = properties.film()
                                          .description()
                                          .maxLength();
    this.earliestReleaseDate = properties.film()
                                         .releaseDate()
                                         .earliest();
  }

  public void validate(CreateFilmCommand command) {
    validateDescription(command.description());
    validateReleaseDate(command.releaseDate());
  }

  private void validateDescription(String description) {
    if (description.length() > maxDescriptionLength) {
      String errorMessage = String.format("Description length cannot exceed %d characters.",
                                          maxDescriptionLength);
      log.warn(errorMessage);
      throw new InvalidFilmDataException(errorMessage);
    }
  }

  private void validateReleaseDate(LocalDate releaseDate) {
    if (releaseDate.isBefore(earliestReleaseDate)) {
      String errorMessage = String.format("Release date cannot be earlier than %s.",
                                          earliestReleaseDate);
      log.warn(errorMessage);
      throw new InvalidFilmDataException(errorMessage);
    }
  }

  public void validate(UpdateFilmCommand command) {
    validateDescription(command.description());
    validateReleaseDate(command.releaseDate());
  }
}
