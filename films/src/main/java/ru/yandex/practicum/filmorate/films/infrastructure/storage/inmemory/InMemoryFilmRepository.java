package ru.yandex.practicum.filmorate.films.infrastructure.storage.inmemory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.common.exception.DuplicateResourceException;
import ru.yandex.practicum.filmorate.common.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.films.domain.model.Film;
import ru.yandex.practicum.filmorate.films.domain.port.FilmRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Repository
public class InMemoryFilmRepository implements FilmRepository {
  private final Map<UUID, Film> films = new ConcurrentHashMap<>();

  @Override
  public Film save(Film film) {
    if (films.containsKey(film.id())) {
      String errorMessage = String.format("Film already exists with id: %s",
                                          film.id());
      log.warn(errorMessage);
      throw new DuplicateResourceException(errorMessage);
    }
    films.put(film.id(),
              film);
    return film;
  }

  @Override
  public Film update(Film film) {
    if (!films.containsKey(film.id())) {
      String errorMessage = String.format("Film with id %s not found",
                                          film.id());
      log.warn(errorMessage);
      throw new ResourceNotFoundException(errorMessage);
    }
    films.put(film.id(),
              film);
    return film;
  }

  @Override
  public List<Film> findAll() {
    return films.values()
                .stream()
                .toList();
  }

  @Override
  public Optional<Film> findById(UUID id) {
    return Optional.ofNullable(films.get(id));
  }
}
