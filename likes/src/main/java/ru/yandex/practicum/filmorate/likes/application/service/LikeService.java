package ru.yandex.practicum.filmorate.likes.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.common.exception.ValidationException;
import ru.yandex.practicum.filmorate.likes.application.port.in.LikeUseCase;
import ru.yandex.practicum.filmorate.likes.domain.port.LikeRepository;

import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class LikeService implements LikeUseCase {
  private final LikeRepository likeRepository;

  @Override
  public boolean addLike(long filmId, long userId) {
    return likeRepository.addLike(filmId,
                                  userId);
  }


  @Override
  public boolean removeLike(long filmId, long userId) {
    return likeRepository.removeLike(filmId,
                                     userId);
  }

  @Override
  public Set<Long> getPopularFilmIds(int count) {
    if (count < 0)
      throw new ValidationException("Count parameter cannot be negative");
    return likeRepository.getPopularFilmIds(count);
  }

  @Override
  public Set<Long> findUsersWhoLikedFilm(long filmId) {
    return likeRepository.findUsersWhoLikedFilm(filmId);
  }

  @Override
  public Set<Long> findLikedFilms(long userId) {
    return likeRepository.findLikedFilms(userId);
  }

  @Override
  public Map<Long, Integer> getLikeCountsForFilms(Set<Long> filmIds) {
    return likeRepository.getLikeCountsForFilms(filmIds);
  }
}
