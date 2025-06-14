package ru.yandex.practicum.filmorate.films.domain.port;


import ru.yandex.practicum.filmorate.common.exception.DuplicateResourceException;
import ru.yandex.practicum.filmorate.common.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.films.domain.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 Repository interface for managing User entity persistence operations. */
public interface UserRepository {
  /**
   Saves a new user.
   @param user User to save
   @return Saved user
   @throws DuplicateResourceException if user with same ID already exists
   */
  User save(User user);

  /**
   Updates an existing user.
   @param user User to update
   @return Updated user
   @throws ResourceNotFoundException if user not found
   */
  User update(User user);

  /**
   Finds user by ID.
   @param id User ID to find
   @return Optional containing user if found, empty otherwise
   */
  Optional<User> findById(UUID id);

  /**
   Retrieves all users.
   @return List of all users
   */
  List<User> findAll();
}