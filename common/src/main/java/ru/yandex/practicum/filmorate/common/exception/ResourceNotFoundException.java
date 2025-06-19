package ru.yandex.practicum.filmorate.common.exception;

/**
 Exception thrown when a requested resource cannot be found. Extends DomainException class. */
public class ResourceNotFoundException extends DomainException {
  /**
   Constructs a new resource not found exception with the specified detail message.
   @param message the detail message
   */
  public ResourceNotFoundException(String message) {
    super(message);
  }

  /**
   Constructs a new resource not found exception with no detail message.
   */
  public ResourceNotFoundException() {
  }

  /**
   Constructs a new resource not found exception with the specified detail message, cause, suppression enabled or
   disabled, and writable stack trace enabled or disabled.
   @param message the detail message
   @param cause the cause of the exception
   @param enableSuppression whether suppression is enabled or disabled
   @param writableStackTrace whether the stack trace should be writable
   */
  public ResourceNotFoundException(String message,
                                   Throwable cause,
                                   boolean enableSuppression,
                                   boolean writableStackTrace) {
    super(message,
          cause,
          enableSuppression,
          writableStackTrace);
  }

  /**
   Constructs a new resource not found exception with the specified cause.
   @param cause the cause of the exception
   */
  public ResourceNotFoundException(Throwable cause) {
    super(cause);
  }

  /**
   Constructs a new resource not found exception with the specified detail message and cause.
   @param message the detail message
   @param cause the cause of the exception
   */
  public ResourceNotFoundException(String message, Throwable cause) {
    super(message,
          cause);
  }
}