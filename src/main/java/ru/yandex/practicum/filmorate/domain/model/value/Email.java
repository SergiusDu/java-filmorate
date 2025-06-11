package ru.yandex.practicum.filmorate.domain.model.value;

import jakarta.validation.ValidationException;
import ru.yandex.practicum.filmorate.domain.validation.ValidationUtils;

/**
 Value object representing a valid email address. Validates email format during construction using ValidationUtils. */
public record Email(String email) {
  /**
   Constructs an Email object after validating the format.
   @param email The email address strings to validate
   @throws ValidationException if the email is null, blank or does not match a valid email format
   */
  public Email {
    email = ValidationUtils.ensureEmailFormat(email,
                                              ValidationException::new);
  }
}