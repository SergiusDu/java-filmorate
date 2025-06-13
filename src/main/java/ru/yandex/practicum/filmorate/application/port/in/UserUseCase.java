package ru.yandex.practicum.filmorate.application.port.in;

import ru.yandex.practicum.filmorate.domain.model.User;
import ru.yandex.practicum.filmorate.infrastructure.web.dto.CreateUserRequest;
import ru.yandex.practicum.filmorate.infrastructure.web.dto.UpdateUserRequest;

import java.util.List;

public interface UserUseCase {
  User addUser(CreateUserRequest user);

  User updateUser(UpdateUserRequest user);

  List<User> getAllUsers();
}
