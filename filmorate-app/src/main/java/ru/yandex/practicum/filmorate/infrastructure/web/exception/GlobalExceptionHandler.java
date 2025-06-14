package ru.yandex.practicum.filmorate.infrastructure.web.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
  public ResponseEntity<Map<String, String>> handleResourceNotFoundException(ResourceNotFoundException e) {
    log.error("Resource not found error: {}",
              e.getMessage(),
              e);
    return new ResponseEntity<>(Map.of("error",
                                       e.getMessage()),
                                HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(DuplicateResourceException.class)
  public ResponseEntity<Map<String, String>> handleDuplicateResourceException(DuplicateResourceException ex) {
    log.error("Duplicate resource error: {}",
              ex.getMessage(),
              ex);
    return new ResponseEntity<>(Map.of("error",
                                       ex.getMessage()),
                                HttpStatus.CONFLICT);
  }

  @ExceptionHandler(ValidationException.class)
  public ResponseEntity<Map<String, String>> handleValidationException(ValidationException ex) {
    log.error("Validation error: {}",
              ex.getMessage(),
              ex);
    return new ResponseEntity<>(Map.of("error",
                                       ex.getMessage()),
                                HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
    log.error("Method argument validation error: {}",
              ex.getMessage(),
              ex);
    Map<String, String> errors = ex.getBindingResult()
                                   .getFieldErrors()
                                   .stream()
                                   .collect(Collectors.toMap(fieldError -> fieldError.getField(),
                                                             fieldError -> fieldError.getDefaultMessage() != null
                                                                           ? fieldError.getDefaultMessage()
                                                                           : "Invalid value"));
    return new ResponseEntity<>(Map.of("errors",
                                       errors),
                                HttpStatus.BAD_REQUEST);
  }
}