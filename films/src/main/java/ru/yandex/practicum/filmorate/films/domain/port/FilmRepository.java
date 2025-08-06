package ru.yandex.practicum.filmorate.films.domain.port;

import ru.yandex.practicum.filmorate.common.exception.DuplicateResourceException;
import ru.yandex.practicum.filmorate.common.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.films.domain.model.Film;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Repository interface for managing Film entity persistence operations.
 */
public interface FilmRepository {

    /**
     * Saves a new film.
     *
     * @param createFilmCommand command containing film data
     * @return the saved film
     * @throws DuplicateResourceException if a film with the same ID already exists
     */
    Film save(CreateFilmCommand createFilmCommand) throws DuplicateResourceException;

    /**
     * Updates an existing film.
     *
     * @param updateCommand command containing updated film data
     * @return the updated film
     * @throws ResourceNotFoundException if the film does not exist
     */
    Film update(UpdateFilmCommand updateCommand) throws ResourceNotFoundException;

    /**
     * Retrieves all films.
     *
     * @return list of all films
     */
    List<Film> findAll();

    /**
     * Finds film by ID.
     *
     * @param id film ID to find
     * @return optional containing film if found, empty otherwise
     */
    Optional<Film> findById(long id);

    /**
     * Retrieves films by their IDs.
     *
     * @param ids set of film IDs to retrieve
     * @return list of films matching the provided IDs
     */
    List<Film> getByIds(Set<Long> ids);

    /**
     * Finds popular films filtered by genre and/or year, with limit.
     *
     * @param genreId optional genre filter
     * @param year    optional year filter
     * @param count   maximum number of films to return
     * @return list of films matching filters, sorted by popularity
     */
    List<Film> findFilmsByGenreIdAndYear(Long genreId, Integer year, Integer count);

    /**
     * Deletes the film with the specified ID.
     *
     * @param id the ID of the film to delete
     * @return {@code true} if a film was successfully deleted, {@code false} if no such film existed
     */
    boolean deleteById(long id);
}
