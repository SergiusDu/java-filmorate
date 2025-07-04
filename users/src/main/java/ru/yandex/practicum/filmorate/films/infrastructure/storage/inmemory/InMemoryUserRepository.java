package ru.yandex.practicum.filmorate.films.infrastructure.storage.inmemory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.common.infrastructure.storage.inmemory.AbstractInMemoryRepository;
import ru.yandex.practicum.filmorate.films.domain.factory.UserFactory;
import ru.yandex.practicum.filmorate.films.domain.model.User;
import ru.yandex.practicum.filmorate.films.domain.port.CreateUserCommand;
import ru.yandex.practicum.filmorate.films.domain.port.UpdateUserCommand;
import ru.yandex.practicum.filmorate.films.domain.port.UserRepository;

@Slf4j
@Repository
public class InMemoryUserRepository extends AbstractInMemoryRepository<User, CreateUserCommand, UpdateUserCommand> implements UserRepository {
  public InMemoryUserRepository(UserFactory factory) {
    super(factory::create,
          factory::update,
          UpdateUserCommand::id,
          User::id);
  }
}
