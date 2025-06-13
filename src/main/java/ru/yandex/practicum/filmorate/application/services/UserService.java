package ru.yandex.practicum.filmorate.application.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.application.port.in.UserUseCase;
import ru.yandex.practicum.filmorate.domain.model.User;
import ru.yandex.practicum.filmorate.domain.port.UserRepository;
import ru.yandex.practicum.filmorate.infrastructure.web.dto.CreateUserRequest;
import ru.yandex.practicum.filmorate.infrastructure.web.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.infrastructure.web.mapper.UserMapper;

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
