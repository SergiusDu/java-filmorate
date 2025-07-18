package ru.yandex.practicum.filmorate.films.domain.port;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.films.domain.model.value.Mpa;

import java.util.List;
import java.util.Optional;

@Component
public interface MpaRepository {
  Mpa save(CreateMpaCommand command);

  Mpa update(UpdateMpaCommand command);

  List<Mpa> findAll();

  Optional<Mpa> findById(long id);
}
