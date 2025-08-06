package ru.yandex.practicum.filmorate.likes.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.common.exception.ValidationException;
import ru.yandex.practicum.filmorate.likes.application.port.in.LikeUseCase;
import ru.yandex.practicum.filmorate.likes.domain.port.LikeRepository;

import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class LikeService implements LikeUseCase {

  private final LikeRepository likeRepository;

  /**
   * Adds a like from a user to a film.
   *
   * @param filmId ID of the film
   * @param userId ID of the user
   * @return true if added, false otherwise
   */
  @Override
  public boolean addLike(long filmId, long userId) {
    return likeRepository.addLike(filmId, userId);
  }

  /**
   * Removes a like from a user to a film.
   *
   * @param filmId ID of the film
   * @param userId ID of the user
   * @return true if removed, false otherwise
   */
  @Override
  public boolean removeLike(long filmId, long userId) {
    return likeRepository.removeLike(filmId, userId);
  }

  /**
   * Returns top-N film IDs by number of likes.
   *
   * @param count number of top films to return
   * @return set of film IDs sorted by popularity (descending)
   */
  @Override
  public Set<Long> getPopularFilmIds(int count) {
    if (count <= 0) {
      throw new ValidationException("Count must be positive");
    }
    return likeRepository.getPopularFilmIds(count);
  }

  /**
   * Returns IDs of users who liked the given film.
   *
   * @param filmId ID of the film
   * @return set of user IDs
   */
  @Override
  public Set<Long> findUsersWhoLikedFilm(long filmId) {
    return likeRepository.findUsersWhoLikedFilm(filmId);
  }

  /**
   * Returns IDs of films liked by the given user.
   *
   * @param userId ID of the user
   * @return set of film IDs
   */
  @Override
  public Set<Long> findLikedFilms(long userId) {
    return likeRepository.findLikedFilms(userId);
  }

  /**
   * Returns mapping of filmId to number of likes.
   *
   * @param filmIds set of film IDs
   * @return map of filmId → like count
   */
  @Override
  public Map<Long, Integer> getLikeCountsForFilms(Set<Long> filmIds) {
    return likeRepository.getLikeCountsForFilms(filmIds);
  }

  /**
   * Returns full mapping of all users and their liked films.
   * Used in collaborative filtering.
   *
   * @return map of userId → set of liked film IDs
   */
  @Override
  public Map<Long, Set<Long>> findAllUserFilmLikes() {
    return likeRepository.findAllUserFilmLikes();
  }

}