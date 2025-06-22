package ru.yandex.practicum.filmorate.films.application.port.in;


import ru.yandex.practicum.filmorate.films.domain.model.User;
import ru.yandex.practicum.filmorate.films.domain.port.CreateUserCommand;
import ru.yandex.practicum.filmorate.films.domain.port.UpdateUserCommand;

import java.util.List;
import java.util.Set;

public interface UserUseCase {
  User addUser(CreateUserCommand user);

  User updateUser(UpdateUserCommand user);

  List<User> getAllUsers();

  List<User> findUsersByIds(Set<Long> ids);
}
