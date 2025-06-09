package ru.yandex.practicum.filmorate.domain.exception;

/**
 Exception thrown when film data validation fails. Extends ValidationException
 class. */
public class InvalidFilmDataException extends ValidationException {
    /**
     Constructs a new invalid film data exception with the specified detail
     message.
     @param message the detail message
     */
    public InvalidFilmDataException(String message) {
        super(message);
    }

    /**
     Constructs a new invalid film data exception with the specified detail
     message and cause.
     @param message the detail message
     @param cause the cause of the exception
     */
    public InvalidFilmDataException(String message, Throwable cause) {
        super(message,
              cause);
    }

    /**
     Constructs a new invalid film data exception with the specified cause.
     @param cause the cause of the exception
     */
    public InvalidFilmDataException(Throwable cause) {
        super(cause);
    }

    /**
     Constructs a new invalid film data exception with the specified detail
     message, cause, suppression enabled or disabled, and writable stack trace
     enabled or disabled.
     @param message the detail message
     @param cause the cause of the exception
     @param enableSuppression whether suppression is enabled or disabled
     @param writableStackTrace whether the stack trace should be writable
     */
    public InvalidFilmDataException(String message,
                                    Throwable cause,
                                    boolean enableSuppression,
                                    boolean writableStackTrace) {
        super(message,
              cause,
              enableSuppression,
              writableStackTrace);
    }
}