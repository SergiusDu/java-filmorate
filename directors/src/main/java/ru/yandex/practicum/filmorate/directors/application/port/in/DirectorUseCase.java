package ru.yandex.practicum.filmorate.directors.application.port.in;

import ru.yandex.practicum.filmorate.common.enums.SortBy;
import ru.yandex.practicum.filmorate.common.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.common.exception.ValidationException;
import ru.yandex.practicum.filmorate.directors.domain.model.Director;
import ru.yandex.practicum.filmorate.directors.domain.port.CreateDirectorCommand;
import ru.yandex.practicum.filmorate.directors.domain.port.UpdateDirectorCommand;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 Use case interface for managing Director entities in the application. Provides methods for creating, reading, updating,
 and deleting directors. This interface serves as the primary entry point for director-related operations in the
 application's hexagonal architecture, acting as an inbound port.
 <p>
 The interface follows the Command-Query Separation principle, with commands for modifying data and queries for
 retrieving data. It is designed to be implemented by service classes that coordinate the application's business logic
 for director management. */
public interface DirectorUseCase {
  /**
   Creates a new director in the system.
   @param command The command containing the director information to be created (name)
   @return The newly created Director entity with assigned ID
   @throws ValidationException if the director name is blank
   */
  Director createDirector(CreateDirectorCommand command);

  /**
   Retrieves all directors from the system.
   @return A list of all Director entities, or an empty list if no directors exist
   */
  List<Director> findAll();

  /**
   Finds a director by their unique identifier.
   @param id The unique identifier of the director to find
   @return The Director entity with the specified ID
   @throws ResourceNotFoundException if no director with the given ID exists
   */
  Director findDirectorById(long id);

  /**
   Finds multiple directors by their unique identifiers.
   @param ids A set of unique identifiers of directors to find
   @return A list of Director entities matching the provided IDs, or an empty list if none are found
   */
  List<Director> findDirectorsByIds(Set<Long> ids);

  /**
   Updates an existing director in the system.
   @param command The command containing the director ID and updated information (name)
   @return The updated Director entity
   @throws ResourceNotFoundException if no director with the given ID exists
   @throws ValidationException if the updated director name is blank
   */
  Director updateDirector(UpdateDirectorCommand command);

  /**
   Deletes a director from the system by their unique identifier.
   @param id The unique identifier of the director to delete
   @throws ResourceNotFoundException if no director with the given ID exists
   */
  void deleteDirectorById(long id);

  /**
   Retrieves a list of film IDs associated with a specific director, sorted according to the provided criteria.
   @param directorId The unique identifier of the director whose films are to be retrieved
   @param sortBy The sorting criteria to apply (e.g., by year or likes)
   @return A list of film IDs directed by the specified director, sorted according to the provided criteria
   @throws ResourceNotFoundException if no director with the given ID exists
   */
  List<Long> getFilmIdsByDirector(long directorId, SortBy sortBy);

  /**
   Updates the association between a film and its directors.
   @param filmId The unique identifier of the film to update
   @param directorIds A set of director IDs to associate with the film
   @throws ResourceNotFoundException if one or more directors with the given IDs do not exist
   */
  void updateFilmDirectors(long filmId, Set<Long> directorIds);

  /**
   Retrieves directors associated with multiple films.
   @param filmIds A set of film IDs for which to retrieve associated directors
   @return A map where keys are film IDs and values are lists of directors associated with each film
   */
  Map<Long, List<Director>> getDirectorsForFilmIds(Set<Long> filmIds);
}
