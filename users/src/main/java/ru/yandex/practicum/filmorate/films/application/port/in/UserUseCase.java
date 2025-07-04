package ru.yandex.practicum.filmorate.films.application.port.in;


import ru.yandex.practicum.filmorate.films.domain.model.User;
import ru.yandex.practicum.filmorate.films.domain.port.CreateUserCommand;
import ru.yandex.practicum.filmorate.films.domain.port.UpdateUserCommand;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserUseCase {
  User addUser(CreateUserCommand user);

  User updateUser(UpdateUserCommand user);

  List<User> getAllUsers();

  Optional<User> findUserById(long userId);

  List<User> findUsersByIds(Set<Long> ids);
}
