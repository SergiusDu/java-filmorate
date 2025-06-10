package ru.yandex.practicum.filmorate.domain.validation;

import java.util.function.Supplier;

/**
 Utility class providing validation methods for common validation scenarios. */
public final class ValidationUtils {

  /**
   Private constructor to prevent instantiation of utility class.
   */
  private ValidationUtils() {}

  /**
   Validates that the provided value is not null.
   @param <E> type of RuntimeException to throw
   @param value the value to check for null
   @param errorSupplier supplier of exception to throw if validation fails
   @throws E if the value is null
   */
  public <E extends RuntimeException> void requireNotNull(Object value, Supplier<E> errorSupplier) {
    if (value == null)
      throw errorSupplier.get();
  }

  /**
   Validates that the provided string value is not null or blank.
   @param <E> type of RuntimeException to throw
   @param value the string value to validate
   @param errorSupplier supplier of exception to throw if validation fails
   @throws E if the string is null or blank
   */
  public <E extends RuntimeException> void requireNotBLank(String value, Supplier<E> errorSupplier) {
    if (value == null || value.isBlank())
      throw errorSupplier.get();
  }
}