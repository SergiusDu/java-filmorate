package ru.yandex.practicum.filmorate.users.domain.model;


import lombok.Builder;
import ru.yandex.practicum.filmorate.common.exception.InvalidUserDataException;
import ru.yandex.practicum.filmorate.common.validation.ValidationUtils;
import ru.yandex.practicum.filmorate.users.domain.model.value.Email;
import ru.yandex.practicum.filmorate.users.domain.model.value.Login;

import java.time.LocalDate;

/**
 Represents a user entity containing core user information. All fields are validated during construction. This class
 uses
 Lombok's @Builder annotation to automatically generate a builder pattern implementation.
 @param id Unique identifier for the user
 @param email Email address of the user (validated format)
 @param login Login name of the user (validated format)
 @param name Display name of the user
 @param birthday Birthdate of the user */
@Builder
public record User(long id,
                   Email email,
                   Login login,
                   String name,
                   LocalDate birthday) {
  /**
   Validates all fields during record construction. This constructor is called automatically when the builder creates a
   new instance.
   @throws InvalidUserDataException if any required field is null or invalid
   */
  public User {
    ValidationUtils.notNull(email, msg -> new InvalidUserDataException("User email must not be null"));
    ValidationUtils.notNull(login, msg -> new InvalidUserDataException("User login must not be null"));
    ValidationUtils.notBlank(name, msg -> new InvalidUserDataException("User name must not be empty"));
    ValidationUtils.notNull(birthday, msg -> new InvalidUserDataException("User birthday must not be null"));
  }
}