package ru.yandex.practicum.filmorate.common.enums;

import ru.yandex.practicum.filmorate.common.exception.ValidationException;

public enum SortBy {
  YEAR, LIKES;

  public static SortBy fromString(String value) {
    try {
      return SortBy.valueOf(value.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new ValidationException("Invalid sort parameter: " + value);
    }
  }
}
