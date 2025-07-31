package ru.yandex.practicum.filmorate.likes.application.port.in;

import java.util.Map;
import java.util.Set;

/**
 Use case interface for managing film likes */
public interface LikeUseCase {
  /**
   Adds a like from a user to a film
   @param filmId ID of the film to like
   @param userId ID of the user giving the like
   @return true if like was successfully added, false otherwise
   */
  boolean addLike(long filmId, long userId);

  /**
   Removes a user's like from a film
   @param filmId ID of the film to remove like from
   @param userId ID of the user whose like to remove
   @return true if like was successfully removed, false otherwise
   */
  boolean removeLike(long filmId, long userId);

  /**
   Gets IDs of most popular films based on number of likes
   @param count number of film IDs to return
   @return {@link Set} of film IDs sorted by popularity (most likes first)
   @throws ru.yandex.practicum.filmorate.common.exception.ValidationException if count is negative
   */
  Set<Long> getPopularFilmIds(int count);

  /**
   Finds all users who liked a specific film
   @param filmId ID of the film to check
   @return set of user IDs who liked the film
   */
  Set<Long> findUsersWhoLikedFilm(long filmId);

  /**
   * Retrieves all film IDs that were liked by the specified user.
   *
   * @param userId the ID of the user
   * @return a set of film IDs liked by the user
   */
  Set<Long> findLikedFilms(long userId);

  Map<Long, Integer> getLikeCountsForFilms(Set<Long> filmIds);
}