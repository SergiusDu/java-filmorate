package ru.yandex.practicum.filmorate.films.infrastructure.storage.inmemory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.common.exception.DuplicateResourceException;
import ru.yandex.practicum.filmorate.common.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.films.domain.model.User;
import ru.yandex.practicum.filmorate.films.domain.port.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Repository
public class InMemoryUserRepository implements UserRepository {
  final Map<UUID, User> users = new ConcurrentHashMap<>();

  @Override
  public User save(User user) {
    if (users.containsKey(user.id())) {
      String errorMessage = String.format("User already exists with id: %s",
                                          user.id());
      log.warn(errorMessage);
      throw new DuplicateResourceException(errorMessage);
    }
    users.put(user.id(),
              user);
    return user;
  }

  @Override
  public User update(User user) {
    if (!users.containsKey(user.id())) {
      String errorMessage = String.format("User not found with id: %s ",
                                          user.id());
      log.warn(errorMessage);
      throw new ResourceNotFoundException(errorMessage);
    }
    users.put(user.id(),
              user);
    return user;
  }

  @Override
  public Optional<User> findById(UUID id) {
    return Optional.ofNullable(users.get(id));
  }

  @Override
  public List<User> findAll() {
    return users.values()
                .stream()
                .toList();
  }
}
