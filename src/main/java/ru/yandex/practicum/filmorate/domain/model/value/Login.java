package ru.yandex.practicum.filmorate.domain.model.value;

import ru.yandex.practicum.filmorate.domain.exception.InvalidUserDataException;
import ru.yandex.practicum.filmorate.domain.validation.ValidationUtils;

public record Login(String login) {
  public Login {
    ValidationUtils.ensureLoginFormat(login,
                                      msg -> new InvalidUserDataException("Invalid login format: " + msg));
  }
}
