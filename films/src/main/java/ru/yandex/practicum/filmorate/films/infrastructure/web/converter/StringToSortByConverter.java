package ru.yandex.practicum.filmorate.films.infrastructure.web.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.common.enums.SortBy;

@Component
public class StringToSortByConverter
    implements Converter<String, SortBy> {
  @Override
  public SortBy convert(String source) {
    if (source.isBlank()) {
      return null;
    }
    try {
      return SortBy.valueOf(source.toUpperCase());
    } catch (IllegalArgumentException e) {
      return null;
    }
  }
}