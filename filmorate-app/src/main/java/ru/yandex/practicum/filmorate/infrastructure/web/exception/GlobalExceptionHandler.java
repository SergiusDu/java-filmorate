package ru.yandex.practicum.filmorate.infrastructure.web.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.common.exception.DuplicateResourceException;
import ru.yandex.practicum.filmorate.common.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.common.exception.ValidationException;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException e) {
    log.error("Resource not found error: {}",
              e.getMessage(),
              e);
    return new ResponseEntity<>(new ErrorResponse(e.getMessage()),
                                HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(DuplicateResourceException.class)
  public ResponseEntity<ErrorResponse> handleDuplicateResourceException(DuplicateResourceException ex) {
    log.error("Duplicate resource error: {}",
              ex.getMessage(),
              ex);
    return new ResponseEntity<>(new ErrorResponse(ex.getMessage()),
                                HttpStatus.CONFLICT);
  }

  @ExceptionHandler(ValidationException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(ValidationException ex) {
    log.error("Validation error: {}",
              ex.getMessage(),
              ex);
    return new ResponseEntity<>(new ErrorResponse(ex.getMessage()),
                                HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ValidationErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
    log.error("Method argument validation error: {}",
              ex.getMessage(),
              ex);
    Map<String, String> errors = ex.getBindingResult()
                                   .getFieldErrors()
                                   .stream()
                                   .collect(Collectors.toMap(FieldError::getField,
                                                             fieldError -> fieldError.getDefaultMessage() != null
                                                                           ? fieldError.getDefaultMessage()
                                                                           : "Invalid name"));
    return new ResponseEntity<>(new ValidationErrorResponse(errors),
                                HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(DuplicateKeyException.class)
  public ResponseEntity<ErrorResponse> handleDuplicateKeyException(DuplicateKeyException ex) {
    log.error("Duplicate key error: {}",
            ex.getMessage(),
            ex);
    return new ResponseEntity<>(new ErrorResponse(ex.getMessage()),
            HttpStatus.CONFLICT);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
    log.error("Violation of constraint: {}",
            ex.getMessage(),
            ex);
    return new ResponseEntity<>(new ErrorResponse(ex.getMessage()),
            HttpStatus.CONFLICT);
  }

  @ExceptionHandler(Throwable.class)
  public ResponseEntity<ErrorResponse> handleUncaughtException(Throwable e) {
    log.error("Uncaught exception: {}",
              e.getMessage(),
              e);
    return new ResponseEntity<>(new ErrorResponse("An unexpected internal server error occurred."),
                                HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
    log.warn("Illegal argument: {}", ex.getMessage());
    ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());
    return ResponseEntity.badRequest().body(errorResponse);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGenericError(Exception ex) {
    log.error("Unexpected error occurred", ex);
    ErrorResponse errorResponse = new ErrorResponse("Internal server error occurred");
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
  }
}