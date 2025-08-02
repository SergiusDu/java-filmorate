package ru.yandex.practicum.filmorate.likes.domain.port;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Repository interface for managing film likes
 */
public interface LikeRepository {
    /**
     * Add a like to a film from a user
     *
     * @param filmId ID of the film to like
     * @param userId ID of the user giving the like
     * @return true if like was added successfully, false otherwise
     */
    boolean addLike(long filmId, long userId);

    /**
     * Remove a like from a film
     *
     * @param filmId ID of the film to remove like from
     * @param userId ID of the user whose like to remove
     * @return true if like was removed successfully, false otherwise
     */
    boolean removeLike(long filmId, long userId);

    /**
     * Get IDs of most popular films based on number of likes
     *
     * @param count number of film IDs to return
     * @return {@link Set} of film IDs sorted by popularity (most likes first)
     */
    LinkedHashSet<Long> getPopularFilmIds(int count);

    /**
     * Find all users who liked a specific film
     *
     * @param filmId ID of the film to check
     * @return set of user IDs who liked the film
     */
    Set<Long> findUsersWhoLikedFilm(long filmId);

    /**
     * Check if a film exists in the repository
     *
     * @param filmId ID of the film to check for
     * @return true if film exists in repository, false otherwise
     */
    boolean contains(long filmId);

    /**
     * Retrieves all film IDs liked by the specified user.
     *
     * @param userId ID of the user
     * @return set of liked film IDs
     */
    Set<Long> findLikedFilms(long userId);

    /**
     * Deletes all likes associated with the specified film.
     *
     * @param filmId ID of the film whose likes should be deleted
     * @return true if at least one like was deleted, false if none existed
     */
    boolean deleteByFilmId(long filmId);

    /**
     * Deletes all likes made by the specified user.
     *
     * @param userId ID of the user whose likes should be deleted
     * @return true if at least one like was deleted, false if none existed
     */
    boolean deleteByUserId(long userId);

    /**
     * Retrieves the total number of likes for each film.
     *
     * @return a map where the key is the film ID and the value is the count of likes
     */
    Map<Long, Long> getLikeCounts();
}