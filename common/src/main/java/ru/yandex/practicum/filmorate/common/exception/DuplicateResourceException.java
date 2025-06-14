package ru.yandex.practicum.filmorate.common.exception;

/**
 Exception thrown when attempting to create a resource that already exists. Extends ValidationException class. */
public class DuplicateResourceException extends ValidationException {
  /**
   Constructs a new duplicate resource exception with the specified detail message.
   @param message the detail message
   */
  public DuplicateResourceException(String message) {
    super(message);
  }

  /**
   Constructs a new duplicate resource exception with the specified detail message and cause.
   @param message the detail message
   @param cause the cause of the exception
   */
  public DuplicateResourceException(String message, Throwable cause) {
    super(message,
          cause);
  }

  /**
   Constructs a new duplicate resource exception with the specified cause.
   @param cause the cause of the exception
   */
  public DuplicateResourceException(Throwable cause) {
    super(cause);
  }

  /**
   Constructs a new duplicate resource exception with the specified detail message, cause, suppression enabled or
   disabled, and writable stack trace enabled or disabled.
   @param message the detail message
   @param cause the cause of the exception
   @param enableSuppression whether suppression is enabled or disabled
   @param writableStackTrace whether the stack trace should be writable
   */
  public DuplicateResourceException(String message,
                                    Throwable cause,
                                    boolean enableSuppression,
                                    boolean writableStackTrace) {
    super(message,
          cause,
          enableSuppression,
          writableStackTrace);
  }
}