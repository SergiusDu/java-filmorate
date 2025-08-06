package ru.yandex.practicum.filmorate.users.domain.port;


import ru.yandex.practicum.filmorate.users.domain.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 Repository interface for managing User entity persistence operations. */
public interface UserRepository {

  User save(CreateUserCommand createCommand);

  User update(UpdateUserCommand updateCommand);

  /**
   Finds user by ID.
   @param id User ID to find
   @return Optional containing user if found, empty otherwise
   */
  Optional<User> findById(long id);

  /**
   Retrieves all films.
   @return List of all films
   */
  List<User> findAll();

  List<User> findByIds(Set<Long> ids);

  boolean deleteById(long userId);
}