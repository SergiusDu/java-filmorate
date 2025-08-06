package ru.yandex.practicum.filmorate.directors.domain.model;

import ru.yandex.practicum.filmorate.common.exception.ValidationException;
import ru.yandex.practicum.filmorate.common.validation.ValidationUtils;

public record Director(long id,
                       String name) {
  public Director {
    ValidationUtils.notBlank(name, msg -> new ValidationException("Director name must not be empty"));
  }
}
