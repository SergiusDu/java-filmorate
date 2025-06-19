package ru.yandex.practicum.filmorate.common.exception;

/**
 Base exception class for domain-level exceptions in the application */
public abstract class DomainException extends RuntimeException {
  /**
   Constructs a new domain exception with the specified detail message
   @param message the detail message
   */
  protected DomainException(String message) {
    super(message);
  }

  /**
   Constructs a new domain exception with no detail message
   */
  protected DomainException() {
  }

  /**
   Constructs a new domain exception with the specified detail message, cause, suppression enabled or disabled, and
   writable stack trace enabled or disabled
   @param message the detail message
   @param cause the cause of the exception
   @param enableSuppression whether suppression is enabled or disabled
   @param writableStackTrace whether the stack trace should be writable
   */
  protected DomainException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message,
          cause,
          enableSuppression,
          writableStackTrace);
  }

  /**
   Constructs a new domain exception with the specified cause
   @param cause the cause of the exception
   */
  protected DomainException(Throwable cause) {
    super(cause);
  }

  /**
   Constructs a new domain exception with the specified detail message and cause
   @param message the detail message
   @param cause the cause of the exception
   */
  protected DomainException(String message, Throwable cause) {
    super(message,
          cause);
  }
}