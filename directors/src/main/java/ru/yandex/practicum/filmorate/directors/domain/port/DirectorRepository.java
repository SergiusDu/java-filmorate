package ru.yandex.practicum.filmorate.directors.domain.port;

import ru.yandex.practicum.filmorate.common.enums.SortBy;
import ru.yandex.practicum.filmorate.directors.domain.model.Director;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 Repository interface for managing {@link Director} entities in the database. Provides methods for creating, reading,
 updating, and deleting directors. */
public interface DirectorRepository {
  /**
   Creates a new director in the database.
   @param command The {@link CreateDirectorCommand} containing the director data to be saved
   @return The newly created {@link Director} entity with generated ID
   */
  Director save(CreateDirectorCommand command);

  /**
   Updates an existing director in the database.
   @param command The {@link UpdateDirectorCommand} containing the updated director data
   @return The updated {@link Director} entity
   @throws IllegalStateException if the director with the specified ID is not found after update
   */
  Director update(UpdateDirectorCommand command);

  /**
   Deletes a director from the database by its ID.
   @param id The ID of the director to delete
   @return true if the director was successfully deleted, false otherwise
   */
  boolean deleteById(long id);

  /**
   Retrieves all directors from the database.
   @return A list of all directors
   */
  List<Director> findAll();

  /**
   Finds a director by its ID.
   @param id The ID of the director to find
   @return An {@link Optional} containing the found {@link Director} or empty if not found
   */
  Optional<Director> findById(long id);

  /**
   Finds multiple directors by their IDs.
   @param ids A set of director IDs to find
   @return A list of {@link Director} entities matching the provided IDs
   @throws IllegalArgumentException if the number of requested IDs exceeds the maximum limit
   */
  List<Director> findByIds(Set<Long> ids);

  /**
   Checks if a director with the specified ID exists in the database.
   @param id The ID of the director to check
   @return true if the director exists, false otherwise
   */
  boolean existsById(long id);

  /**
   Retrieves a list of film IDs associated with a specific director, sorted according to the provided criteria.
   @param directorId The ID of the director whose films to find
   @param sortBy The sorting criteria (YEAR for sorting by release date, LIKES for sorting by number of likes)
   @return A list of film IDs directed by the specified director, sorted according to the provided criteria
   */
  List<Long> findFilmIdsByDirectorId(long directorId, SortBy sortBy);

  /**
   Creates associations between a film and multiple directors in the database.
   @param filmId The ID of the film to link directors to
   @param directorIds A set of director IDs to be linked to the film
   */
  void linkFilmToDirectors(long filmId, Set<Long> directorIds);

  /**
   Updates the directors associated with a specific film by removing all existing associations and creating new ones
   based on the provided director IDs.
   @param filmId The ID of the film whose directors should be updated
   @param directorIds A set of director IDs to be associated with the film
   */
  void updateFilmDirectors(long filmId, Set<Long> directorIds);

  /**
   Retrieves all directors associated with the specified film IDs.
   @param filmIds A set of film IDs to find directors for
   @return A map where keys are film IDs and values are lists of directors associated with each film
   */
  Map<Long, List<Director>> findDirectorsForFilmIds(Set<Long> filmIds);
}
