package ru.yandex.practicum.filmorate.domain.validation;

import java.util.function.Supplier;

/**
 Utility class providing validation methods for common validation scenarios. Contains static methods for checking null
 values and empty strings. */
public final class ValidationUtils {

  /**
   Private constructor to prevent instantiation of utility class since all methods are static.
   */
  private ValidationUtils() {}

  /**
   Validates that the provided value is not null.
   @param <E> type of RuntimeException to throw if validation fails
   @param value object to check for null
   @param errorSupplier supplier that provides the exception to throw if validation fails
   @throws E the runtime exception supplied by errorSupplier if value is null
   */
  public static <E extends RuntimeException> void requireNotNull(Object value, Supplier<E> errorSupplier) {
    if (value == null)
      throw errorSupplier.get();
  }

  /**
   Validates that the provided string value is not null or blank (empty or whitespace only).
   @param <E> type of RuntimeException to throw if validation fails
   @param value string to validate
   @param errorSupplier supplier that provides the exception to throw if validation fails
   @throws E the runtime exception supplied by errorSupplier if value is null or blank
   */
  public static <E extends RuntimeException> void requireNotBLank(String value, Supplier<E> errorSupplier) {
    if (value == null || value.isBlank())
      throw errorSupplier.get();
  }
}