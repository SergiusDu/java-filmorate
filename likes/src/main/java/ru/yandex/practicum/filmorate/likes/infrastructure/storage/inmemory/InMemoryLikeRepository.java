package ru.yandex.practicum.filmorate.likes.infrastructure.storage.inmemory;

import domain.model.FilmLikeCount;
import domain.port.LikeRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

@Repository
public class InMemoryLikeRepository implements LikeRepository {
  private final Map<Long, Set<Long>> likesByFilm = new ConcurrentHashMap<>();
  private final TreeSet<FilmLikeCount> popularFilmsIndex;
  private final ReadWriteLock lock = new ReentrantReadWriteLock();

  public InMemoryLikeRepository() {
    Comparator<FilmLikeCount> comparator = Comparator.comparingInt(FilmLikeCount::likeCount)
                                                     .reversed()
                                                     .thenComparing(FilmLikeCount::filmId);
    this.popularFilmsIndex = new TreeSet<>(comparator);
  }

  @Override
  public boolean addLike(long filmId, long userId) {
    lock.writeLock()
        .lock();
    try {
      Set<Long> usersWhoLiked = likesByFilm.computeIfAbsent(filmId,
                                                            k -> ConcurrentHashMap.newKeySet());
      boolean isAdded = usersWhoLiked.add(userId);
      if (isAdded) {
        updatePopularityIndex(filmId,
                              usersWhoLiked.size() - 1,
                              usersWhoLiked.size());
      }
      return isAdded;
    } finally {
      lock.writeLock()
          .unlock();
    }
  }

  private void updatePopularityIndex(long filmId, int oldLikeCount, int newLikeCount) {
    if (oldLikeCount > 0) {
      popularFilmsIndex.remove(new FilmLikeCount(filmId,
                                                 oldLikeCount));
    }

    if (newLikeCount > 0) {
      popularFilmsIndex.add(new FilmLikeCount(filmId,
                                              newLikeCount));
    }
  }

  @Override
  public boolean removeLike(long filmId, long userId) {
    final AtomicBoolean isRemoved = new AtomicBoolean(false);

    lock.writeLock()
        .lock();
    try {
      likesByFilm.computeIfPresent(filmId,
                                   (key, users) -> {
                                     if (users.remove(userId)) {
                                       isRemoved.set(true);
                                       updatePopularityIndex(filmId,
                                                             users.size() + 1,
                                                             users.size());
                                     }
                                     return users.isEmpty() ? null : users;
                                   });
    } finally {
      lock.writeLock()
          .unlock();
    }
    return isRemoved.get();
  }

  @Override
  public LinkedHashSet<Long> getPopularFilmIds(int count) {
    lock.readLock()
        .lock();
    try {
      return popularFilmsIndex.stream()
                              .limit(count)
                              .map(FilmLikeCount::filmId)
                              .collect(Collectors.toCollection(LinkedHashSet::new));
    } finally {
      lock.readLock()
          .unlock();
    }
  }

  @Override
  public Set<Long> findUsersWhoLikedFilm(long filmId) {
    lock.readLock()
        .lock();
    try {
      return likesByFilm.getOrDefault(filmId,
                                      Set.of());
    } finally {
      lock.readLock()
          .unlock();
    }
  }

  @Override
  public boolean contains(long filmId) {
    return likesByFilm.containsKey(filmId);
  }
}
