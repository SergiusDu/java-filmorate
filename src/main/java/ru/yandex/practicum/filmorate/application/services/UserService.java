package ru.yandex.practicum.filmorate.application.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.application.port.in.UserUseCase;
import ru.yandex.practicum.filmorate.domain.model.User;
import ru.yandex.practicum.filmorate.domain.port.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserUseCase {
  UserRepository userRepository;

  @Override
  public User addUser(User user) {
    return userRepository.save(user);
  }

  @Override
  public User updateUser(User user) {
    return userRepository.update(user);
  }

  @Override
  public List<User> getAllUsers() {
    return userRepository.findAll();
  }
}
