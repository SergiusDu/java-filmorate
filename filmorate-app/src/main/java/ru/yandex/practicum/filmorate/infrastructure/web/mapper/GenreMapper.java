package ru.yandex.practicum.filmorate.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.films.domain.model.value.Genre;
import ru.yandex.practicum.filmorate.infrastructure.web.dto.GenreResponse;

@Component
public class GenreMapper {
  public GenreResponse toResponse(Genre genre) {
    return new GenreResponse(genre.id(),
                             genre.name());
  }
}
