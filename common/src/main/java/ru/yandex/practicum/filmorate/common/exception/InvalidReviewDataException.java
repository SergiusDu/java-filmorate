package ru.yandex.practicum.filmorate.common.exception;

public class InvalidReviewDataException extends RuntimeException {
    public InvalidReviewDataException(String message) {
        super(message);
    }
}
