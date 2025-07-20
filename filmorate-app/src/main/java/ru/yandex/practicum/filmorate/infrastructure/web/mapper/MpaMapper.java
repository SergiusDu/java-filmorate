package ru.yandex.practicum.filmorate.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.films.domain.model.value.Mpa;
import ru.yandex.practicum.filmorate.infrastructure.web.dto.MpaResponse;

@Component
public class MpaMapper {
  public MpaResponse toResponse(Mpa mpa) {
    return new MpaResponse(mpa.id(),
                           mpa.name());
  }
}
