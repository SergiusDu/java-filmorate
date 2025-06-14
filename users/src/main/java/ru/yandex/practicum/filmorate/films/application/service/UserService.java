package ru.yandex.practicum.filmorate.films.application.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.films.application.port.in.UserUseCase;
import ru.yandex.practicum.filmorate.films.domain.model.User;
import ru.yandex.practicum.filmorate.films.domain.port.UserRepository;
import ru.yandex.practicum.filmorate.films.web.dto.CreateUserRequest;
import ru.yandex.practicum.filmorate.films.web.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.films.web.mapper.UserMapper;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements UserUseCase {
  final UserRepository userRepository;

  @Override
  public User addUser(CreateUserRequest request) {

    return userRepository.save(UserMapper.toDomain(request,
                                                   UUID.randomUUID()));
  }

  @Override
  public User updateUser(UpdateUserRequest request) {
    return userRepository.update(UserMapper.toDomain(request));
  }

  @Override
  public List<User> getAllUsers() {
    return userRepository.findAll();
  }
}
