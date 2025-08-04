package ru.yandex.practicum.filmorate.likes.domain.port;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 Repository interface for managing film likes */
public interface LikeRepository {
  /**
   Add a like to a film from a user
   @param filmId ID of the film to like
   @param userId ID of the user giving the like
   @return true if like was added successfully, false otherwise
   */
  boolean addLike(long filmId, long userId);

  /**
   Remove a like from a film
   @param filmId ID of the film to remove like from
   @param userId ID of the user whose like to remove
   @return true if like was removed successfully, false otherwise
   */
  boolean removeLike(long filmId, long userId);

  /**
   Get IDs of most popular films based on number of likes
   @param count number of film IDs to return
   @return {@link Set} of film IDs sorted by popularity (most likes first)
   */
  LinkedHashSet<Long> getPopularFilmIds(int count);

  /**
   Find all users who liked a specific film
   @param filmId ID of the film to check
   @return set of user IDs who liked the film
   */
  Set<Long> findUsersWhoLikedFilm(long filmId);

  /**
   Check if a film exists in the repository
   @param filmId ID of the film to check for
   @return true if film exists in repository, false otherwise
   */
  boolean contains(long filmId);

  /**
   * Returns a set of film IDs liked by the given user.
   * @param userId ID of the user
   * @return set of liked film IDs
   */
  Set<Long> findLikedFilms(long userId);

  /**
   * Returns a map of filmId -> number of likes for each film from the given set.
   *
   * @param filmIds set of film IDs
   * @return map where key = film ID, value = total number of likes
   */
  Map<Long, Integer> getLikeCountsForFilms(Set<Long> filmIds);

  /**
   * Returns a full map of all users and their liked films.
   * Used in collaborative filtering.
   *
   * @return map of user IDs to sets of liked film IDs
   */
  Map<Long, Set<Long>> findAllUserFilmLikes();
}