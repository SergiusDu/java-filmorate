package ru.yandex.practicum.filmorate.films.application.port.in;


import ru.yandex.practicum.filmorate.films.domain.model.User;
import ru.yandex.practicum.filmorate.films.web.dto.CreateUserRequest;
import ru.yandex.practicum.filmorate.films.web.dto.UpdateUserRequest;

import java.util.List;

public interface UserUseCase {
  User addUser(CreateUserRequest user);

  User updateUser(UpdateUserRequest user);

  List<User> getAllUsers();
}
