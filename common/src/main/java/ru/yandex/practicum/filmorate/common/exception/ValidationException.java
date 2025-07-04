package ru.yandex.practicum.filmorate.common.exception;

/**
 Base class for service-related exceptions in the application. Extends RuntimeException to allow unchecked exception
 handling. */
public class ValidationException extends RuntimeException {
  /**
   Constructs a new service exception with the specified detail message.
   @param message the detail message
   */
  public ValidationException(String message) {
    super(message);
  }

  /**
   Constructs a new service exception with the specified detail message and cause.
   @param message the detail message
   @param cause the cause of the exception
   */
  public ValidationException(String message, Throwable cause) {
    super(message,
          cause);
  }

  /**
   Constructs a new service exception with the specified cause.
   @param cause the cause of the exception
   */
  public ValidationException(Throwable cause) {
    super(cause);
  }

  /**
   Constructs a new service exception with the specified detail message, cause, suppression enabled or disabled, and
   writable stack trace enabled or disabled.
   @param message the detail message
   @param cause the cause of the exception
   @param enableSuppression whether suppression is enabled or disabled
   @param writableStackTrace whether the stack trace should be writable
   */
  public ValidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message,
          cause,
          enableSuppression,
          writableStackTrace);
  }
}