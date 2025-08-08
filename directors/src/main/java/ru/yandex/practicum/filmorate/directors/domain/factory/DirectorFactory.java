package ru.yandex.practicum.filmorate.directors.domain.factory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.directors.domain.model.Director;
import ru.yandex.practicum.filmorate.directors.domain.port.CreateDirectorCommand;
import ru.yandex.practicum.filmorate.directors.domain.port.UpdateDirectorCommand;

@Component
public class DirectorFactory {
  public Director create(long id, CreateDirectorCommand command) {
    return new Director(id, command.name());
  }

  public Director update(UpdateDirectorCommand command) {
    return new Director(command.id(), command.name());
  }
}
