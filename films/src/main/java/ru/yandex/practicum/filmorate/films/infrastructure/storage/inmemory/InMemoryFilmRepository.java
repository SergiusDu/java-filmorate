package ru.yandex.practicum.filmorate.films.infrastructure.storage.inmemory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.common.exception.DuplicateResourceException;
import ru.yandex.practicum.filmorate.common.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.films.domain.model.Film;
import ru.yandex.practicum.filmorate.films.domain.port.CreateFilmCommand;
import ru.yandex.practicum.filmorate.films.domain.port.FilmRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Repository
public class InMemoryFilmRepository implements FilmRepository {
  private final Map<Integer, Film> films = new ConcurrentHashMap<>();
  private final AtomicInteger idCounter = new AtomicInteger(0);

  @Override
  public Film save(CreateFilmCommand command) {
    Film film = new Film(generateId(),
                         command.name(),
                         command.description(),
                         command.releaseDate(),
                         command.duration());
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
  public Optional<Film> findById(int id) {
    return Optional.ofNullable(films.get(id));
  }

  private int generateId() {
    return idCounter.addAndGet(1);
  }
}
