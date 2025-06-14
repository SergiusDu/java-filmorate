package ru.yandex.practicum.filmorate.films.domain.model.value;


import ru.yandex.practicum.filmorate.common.exception.InvalidUserDataException;
import ru.yandex.practicum.filmorate.common.validation.ValidationUtils;

public record Login(String login) {
  public Login {
    ValidationUtils.ensureLoginFormat(login,
                                      msg -> new InvalidUserDataException("Invalid login format: " + msg));
  }
}
