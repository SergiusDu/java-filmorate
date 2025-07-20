package ru.yandex.practicum.filmorate.films.domain.factory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.films.domain.model.value.Mpa;
import ru.yandex.practicum.filmorate.films.domain.port.CreateMpaCommand;
import ru.yandex.practicum.filmorate.films.domain.port.UpdateMpaCommand;

@Component
public class MpaFactory {
  public Mpa create(long id, CreateMpaCommand command) {
    return new Mpa(id,
                   command.name());
  }

  public Mpa update(UpdateMpaCommand command) {
    return new Mpa(command.id(),
                   command.name());
  }
}
