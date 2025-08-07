package ru.yandex.practicum.filmorate.common.exception;

/**
 * Exception thrown when event data validation fails.
 * This is a business logic exception that indicates invalid event parameters.
 */
public class InvalidEventDataException extends RuntimeException {

    /**
     * Constructs a new InvalidEventDataException with the specified detail message.
     *
     * @param message the detail message explaining the validation failure
     */
    public InvalidEventDataException(String message) {
        super(message);
    }

    /**
     * Constructs a new InvalidEventDataException with the specified detail message and cause.
     *
     * @param message the detail message explaining the validation failure
     * @param cause   the cause of the exception
     */
    public InvalidEventDataException(String message, Throwable cause) {
        super(message, cause);
    }
}