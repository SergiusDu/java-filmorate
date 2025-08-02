package ru.yandex.practicum.filmorate.users.domain.model.value;


import ru.yandex.practicum.filmorate.common.exception.InvalidUserDataException;
import ru.yandex.practicum.filmorate.common.validation.ValidationUtils;

/**
 Value object representing a valid email address. Validates email format during construction using ValidationUtils. */
public record Email(String email) {
  /**
   Constructs an Email object after validating the format.
   @param email The email address string to validate
   @throws InvalidUserDataException if the email is null, blank or does not match a valid email format
   */
  public Email {
    email = ValidationUtils.ensureEmailFormat(email,
                                              InvalidUserDataException::new);
  }
}