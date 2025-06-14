package ru.yandex.practicum.filmorate.common.validation;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 Utility class providing validation methods for common validation scenarios. Contains static methods for checking null
 values, empty strings, positive durations and email format validation. */
@Slf4j
public final class ValidationUtils {
  /**
   Private constructor to prevent instantiation of utility class since all methods are static.
   */
  private ValidationUtils() {}

  /**
   Generic validation method that tests a value against a predicate and throws an exception if validation fails.
   @param <T> The type of the value to validate
   @param <E> The type of runtime exception to throw
   @param value The value to validate
   @param predicate The validation predicate to test against
   @param exceptionFactory Factory function to create the exception
   @param message Error message to use if validation fails
   @return The validated value if validation succeeds
   @throws E if validation fails
   */
  public static <T, E extends RuntimeException> T validate(T value,
                                                           Predicate<T> predicate,
                                                           Function<String, E> exceptionFactory,
                                                           String message) {
    if (!predicate.test(value)) {
      log.warn("Validation failed: {}",
               message);
      throw exceptionFactory.apply(message);
    }
    return value;
  }

  /**
   Validates that a value is not null.
   @param <T> The type of the value to validate
   @param <E> The type of runtime exception to throw
   @param value The value to check for null
   @param exceptionFactory Factory function to create the exception
   @return The validated non-null value
   @throws E if the value is null
   */
  public static <T, E extends RuntimeException> T notNull(T value, Function<String, E> exceptionFactory) {
    return validate(value,
                    Objects::nonNull,
                    exceptionFactory,
                    "Value must not be null");
  }

  /**
   Validates that a string is not null or blank.
   @param <E> The type of runtime exception to throw
   @param value The string to validate
   @param exceptionFactory Factory function to create the exception
   @return The validated non-blank string
   @throws E if the string is null or blank
   */
  public static <E extends RuntimeException> String notBlank(String value, Function<String, E> exceptionFactory) {
    return validate(value,
                    v -> v != null && !v.isBlank(),
                    exceptionFactory,
                    "String must not be null or blank");
  }

  /**
   Validates that a duration is positive and not null.
   @param <E> The type of runtime exception to throw
   @param value The duration to validate
   @param exceptionFactory Factory function to create the exception
   @return The validated positive duration
   @throws E if the duration is null, zero or negative
   */
  public static <E extends RuntimeException> Duration positive(Duration value, Function<String, E> exceptionFactory) {
    return validate(value,
                    d -> d != null && !d.isZero() && d.isPositive(),
                    exceptionFactory,
                    "Duration must be positive");
  }

  /**
   Validates and normalizes an email address format according to RFC 5322 standards. The validation checks: - Total
   email
   length ≤ 254 characters - Local part: - Length ≤ 64 characters - Contains only letters, numbers and allowed special
   characters (._%+-) - Domain part: - Contains valid characters (letters, numbers, hyphens) - Has at least one dot
   separator - Each label starts/ends with letter/number - Labels ≤ 63 characters
   @param <E> Type of runtime exception to throw on validation failure
   @param value Email string to validate
   @param exceptionFactory Function to create exception with error message
   @return Validated email converted to lowercase
   @throws E if email is invalid: - null or blank - exceeds length limits - missing or multiple @ symbols - invalid
   characters - malformed domain
   */
  public static <E extends RuntimeException> String ensureEmailFormat(String value,
                                                                      Function<String, E> exceptionFactory) {
    final Pattern domainLabelPattern = Pattern.compile("^[A-Za-z0-9]([A-Za-z0-9-]{0,61}[A-Za-z0-9])?$");
    final Pattern localPartPattern = Pattern.compile("^[a-zA-Z0-9]+(?:[._%+-][a-zA-Z0-9]+)*$");

    notBlank(value,
             msg -> exceptionFactory.apply("Email must not be null or blank."));

    if (value.length() > 254) {
      throw exceptionFactory.apply("Email length cannot exceed 254 characters.");
    }

    String[] parts = value.split("@");
    if (parts.length != 2) {
      throw exceptionFactory.apply("Email must contain exactly one '@' symbol.");
    }

    String localPart = parts[0];
    String domainPart = parts[1];

    if (localPart.length() > 64) {
      throw exceptionFactory.apply("Email local part cannot exceed 64 characters.");
    }
    if (!localPartPattern.matcher(localPart)
                         .matches()) {
      throw exceptionFactory.apply("Invalid characters in email local part.");
    }

    if (domainPart.isEmpty()) {
      throw exceptionFactory.apply("Email domain part must not be empty.");
    }

    String[] labels = domainPart.split("\\.");
    if (labels.length < 2) {
      throw exceptionFactory.apply("Email domain must contain at least one dot.");
    }

    for (String label : labels) {
      if (!domainLabelPattern.matcher(label)
                             .matches()) {
        throw exceptionFactory.apply("Invalid domain name format.");
      }
    }

    return value.toLowerCase(Locale.ROOT);
  }

  /**
   Validates and ensures a proper login format by checking that: - Login is not null or blank - Does not contain any
   whitespace characters
   @param <E> The type of runtime exception to throw on validation failure
   @param value The login string to validate
   @param exceptionFactory Factory function to create the exception
   @return The validated login string
   @throws E if login is invalid: - null or blank - contains whitespace
   */
  public static <E extends RuntimeException> String ensureLoginFormat(String value,
                                                                      Function<String, E> exceptionFactory) {
    notBlank(value,
             msg -> exceptionFactory.apply("Login must not be null or blank"));
    return validate(value,
                    v -> v.chars()
                          .noneMatch(Character::isWhitespace),
                    exceptionFactory,
                    "Login must not contain spaces");
  }
}