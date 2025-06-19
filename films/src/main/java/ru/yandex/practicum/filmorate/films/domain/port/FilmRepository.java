package ru.yandex.practicum.filmorate.films.domain.port;


import ru.yandex.practicum.filmorate.common.exception.DuplicateResourceException;
import ru.yandex.practicum.filmorate.common.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.films.domain.model.Film;

import java.util.List;
import java.util.Optional;

/**
 Repository interface for managing Film entity persistence operations. */
public interface FilmRepository {
  /**
   Saves a new film to the repository based on the provided command.
   @param createFilmCommand Command containing film data to create
   @return The created film entity with generated ID
   @throws DuplicateResourceException if a film with the same ID already exists in the repository
   */
  Film save(CreateFilmCommand createFilmCommand);

  /**
   Updates an existing film based on the provided update command.
   @param updateCommand Command containing updated film data
   @return The updated film entity
   @throws ResourceNotFoundException if film with specified ID does not exist in the repository
   */
  Film update(UpdateFilmCommand updateCommand);

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
  Optional<Film> findById(int id);
}