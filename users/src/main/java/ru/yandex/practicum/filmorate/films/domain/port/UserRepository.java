package ru.yandex.practicum.filmorate.films.domain.port;


import ru.yandex.practicum.filmorate.films.domain.model.User;

import java.util.List;
import java.util.Optional;

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
  Optional<User> findById(int id);

  /**
   Retrieves all users.
   @return List of all users
   */
  List<User> findAll();
}