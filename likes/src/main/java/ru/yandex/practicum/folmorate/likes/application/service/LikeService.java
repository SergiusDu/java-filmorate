package ru.yandex.practicum.folmorate.likes.application.service;

import domain.port.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.common.exception.ValidationException;
import ru.yandex.practicum.folmorate.likes.application.port.in.LikeUseCase;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class LikeService implements LikeUseCase {
  LikeRepository likeRepository;

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
  public List<Long> getPopularFilmIds(int count) {
    if (count < 0)
      throw new ValidationException("Count parameter cannot be negative");
    return likeRepository.getPopularFilmIds(count);
  }

  @Override
  public Set<Long> findUsersWhoLikedFilm(long filmId) {
    return likeRepository.findUsersWhoLikedFilm(filmId);
  }
}
