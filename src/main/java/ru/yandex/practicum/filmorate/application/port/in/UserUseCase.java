package ru.yandex.practicum.filmorate.application.port.in;

import ru.yandex.practicum.filmorate.domain.model.User;

import java.util.List;

public interface UserUseCase {
  User addUser(User user);

  User updateUser(User user);

  List<User> getAllUsers();
}
