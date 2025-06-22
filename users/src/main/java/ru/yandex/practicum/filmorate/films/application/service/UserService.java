package ru.yandex.practicum.filmorate.films.application.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.common.events.UserCreatedEvent;
import ru.yandex.practicum.filmorate.films.application.port.in.UserUseCase;
import ru.yandex.practicum.filmorate.films.domain.model.User;
import ru.yandex.practicum.filmorate.films.domain.port.CreateUserCommand;
import ru.yandex.practicum.filmorate.films.domain.port.UpdateUserCommand;
import ru.yandex.practicum.filmorate.films.domain.port.UserRepository;
import ru.yandex.practicum.filmorate.friendships.application.port.in.FriendshipsUseCase;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserUseCase {
  private final UserRepository userRepository;
  private final FriendshipsUseCase friendshipService;
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

  private final <T extends ApplicationEvent> void logEventPublishing(T event) {
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
  public Set<User> getFriends(long userId) {
    return friendshipService.getFriends(userId)
                            .stream()
                            .map(userRepository::findById)
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .collect(Collectors.toSet());
  }

  @Override
  public Set<User> getMutualFriends(long userId, long friendId) {
    return friendshipService.getMutualFriends(userId,
                                              friendId)
                            .stream()
                            .map(userRepository::findById)
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .collect(Collectors.toSet());
  }

  @Override
  public List<User> findUsersByIds(Set<Long> ids) {
    return userRepository.findByIds(ids);
  }
}
