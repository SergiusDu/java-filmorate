package ru.yandex.practicum.filmorate.films.domain.port;


import ru.yandex.practicum.filmorate.common.exception.DuplicateResourceException;
import ru.yandex.practicum.filmorate.common.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.films.domain.model.Film;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 Repository interface for managing Film entity persistence operations. */
public interface FilmRepository {
  /**
   Saves a new film to the repository.
   @param film The film entity to be saved
   @return The saved film entity with generated ID
   @throws DuplicateResourceException if a film with the same ID already exists in the repository
   */
  Film save(Film film);

  /**
   Updates an existing film.
   @param film Film to update
   @return Updated film
   @throws ResourceNotFoundException if film not found
   */
  Film update(Film film);

  /**
   Retrieves all films.
   @return List of all films
   */
  List<Film> findAll();

  /**
   Finds film by ID.
   @param id Film ID to find
   @return Optional containing film if found, empty otherwise
   */
  Optional<Film> findById(UUID id);
}