package ru.yandex.practicum.filmorate.users.application.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.common.events.UserCreatedEvent;
import ru.yandex.practicum.filmorate.common.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.users.application.port.in.UserUseCase;
import ru.yandex.practicum.filmorate.users.domain.model.User;
import ru.yandex.practicum.filmorate.users.domain.port.CreateUserCommand;
import ru.yandex.practicum.filmorate.users.domain.port.UpdateUserCommand;
import ru.yandex.practicum.filmorate.users.domain.port.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserUseCase {
  private final UserRepository userRepository;
  private final ApplicationEventPublisher eventPublisher;

  @Override
  public User addUser(CreateUserCommand command) {
    User newUser = userRepository.save(command);
    UserCreatedEvent userCreatedEvent = new UserCreatedEvent(this,
                                                             newUser.id());
    logEventPublishing(userCreatedEvent);
    eventPublisher.publishEvent(userCreatedEvent);
    return newUser;
  }

  private <T extends ApplicationEvent> void logEventPublishing(T event) {
    log.debug("Publishing {}: {}",
              event.getClass()
                   .getSimpleName(),
              event);
  }

  @Override
  public User updateUser(UpdateUserCommand command) {
    return userRepository.update(command);
  }

  @Override
  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  @Override
  public Optional<User> findUserById(long userId) {
    return userRepository.findById(userId);
  }

  @Override
  public List<User> findUsersByIds(Set<Long> ids) {
    return userRepository.findByIds(ids);
  }

  @Override
  public void deleteUserById(long userId) {
    if (!userRepository.deleteById(userId)) {
      throw new ResourceNotFoundException("User with id " + userId + " not found");
    }
  }
}
