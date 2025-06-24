package ru.yandex.practicum.filmorate.films.domain.factory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.films.domain.model.User;
import ru.yandex.practicum.filmorate.films.domain.model.value.Email;
import ru.yandex.practicum.filmorate.films.domain.model.value.Login;
import ru.yandex.practicum.filmorate.films.domain.port.CreateUserCommand;
import ru.yandex.practicum.filmorate.films.domain.port.UpdateUserCommand;

@Component
public final class UserFactory {
  public User create(long id, CreateUserCommand command) {
    return new User(id,
                    new Email(command.email()),
                    new Login(command.login()),
                    command.name(),
                    command.birthday());
  }

  public User update(UpdateUserCommand command) {
    return new User(command.id(),
                    new Email(command.email()),
                    new Login(command.login()),
                    command.name(),
                    command.birthday());
  }

}
