package ru.yandex.practicum.filmorate.films.domain.model;


import ru.yandex.practicum.filmorate.common.exception.InvalidUserDataException;
import ru.yandex.practicum.filmorate.common.validation.ValidationUtils;
import ru.yandex.practicum.filmorate.films.domain.model.value.Email;
import ru.yandex.practicum.filmorate.films.domain.model.value.Login;

import java.time.LocalDate;

/**
 Represents a user entity containing core user information. All fields are validated during construction.
 @param id Unique identifier for the user
 @param email Email address of the user (validated format)
 @param login Login name of the user (validated format)
 @param birthday Birthdate of the user */
public record User(long id,
                   Email email,
                   Login login,
                   String name,
                   LocalDate birthday) {
  /**
   Validates all fields during record construction.
   @throws InvalidUserDataException if any required field is null
   */
  public User {
    ValidationUtils.notNull(email,
                            msg -> new InvalidUserDataException("User email must not be null"));
    ValidationUtils.notNull(login,
                            msg -> new InvalidUserDataException("User login must not be null"));
    ValidationUtils.notBlank(name,
                             msg -> new InvalidUserDataException("User name must not be empty"));
    ValidationUtils.notNull(birthday,
                            msg -> new InvalidUserDataException("User birthday must not be null"));
  }
}