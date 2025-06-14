package ru.yandex.practicum.filmorate.common.exception;

/**
 Exception thrown when user data service fails. Extends ValidationException class. */
public class InvalidUserDataException extends ValidationException {
  /**
   Constructs a new invalid user data exception with the specified detail message.
   @param message the detail message
   */
  public InvalidUserDataException(String message) {
    super(message);
  }

  /**
   Constructs a new invalid user data exception with the specified detail message and cause.
   @param message the detail message
   @param cause the cause of the exception
   */
  public InvalidUserDataException(String message, Throwable cause) {
    super(message,
          cause);
  }

  /**
   Constructs a new invalid user data exception with the specified cause.
   @param cause the cause of the exception
   */
  public InvalidUserDataException(Throwable cause) {
    super(cause);
  }

  /**
   Constructs a new invalid user data exception with the specified detail message, cause, suppression enabled or
   disabled, and writable stack trace enabled or disabled.
   @param message the detail message
   @param cause the cause of the exception
   @param enableSuppression whether suppression is enabled or disabled
   @param writableStackTrace whether the stack trace should be writable
   */
  public InvalidUserDataException(String message,
                                  Throwable cause,
                                  boolean enableSuppression,
                                  boolean writableStackTrace) {
    super(message,
          cause,
          enableSuppression,
          writableStackTrace);
  }
}