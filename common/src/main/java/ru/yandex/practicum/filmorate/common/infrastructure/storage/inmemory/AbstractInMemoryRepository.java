package ru.yandex.practicum.filmorate.common.infrastructure.storage.inmemory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.common.exception.ResourceNotFoundException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;
import java.util.function.Function;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractInMemoryRepository<T, C, U> {
  protected final ConcurrentHashMap<Long, T> storage = new ConcurrentHashMap<>();
  private final AtomicLong idGenerator = new AtomicLong(0);

  private final BiFunction<Long, C, T> createBuilder;
  private final Function<U, T> updateBuilder;
  private final Function<U, Long> updateIdExtractor;
  private final Function<T, Long> entityIdExtractor;

  public final T save(C createCommand) {
    T value = createBuilder.apply(generateNextId(),
                                  createCommand);
    storage.put(entityIdExtractor.apply(value),
                value);
    return value;
  }

  public final Long generateNextId() {
    return idGenerator.incrementAndGet();
  }

  public final List<T> findAll() {
    return new ArrayList<>(storage.values());
  }

  public final T update(U updateCommand) {
    Long id = updateIdExtractor.apply(updateCommand);
    if (!storage.containsKey(id)) {
      String errorMessage = "Entity with id " + id + " not found";
      log.warn(errorMessage);
      throw new ResourceNotFoundException(errorMessage);
    }
    T value = updateBuilder.apply(updateCommand);
    storage.put(id,
                value);
    return value;
  }

  public final Optional<T> findById(long id) {
    return Optional.ofNullable(storage.get(id));
  }

  public final List<T> findByIds(Set<Long> ids) {
    if (ids == null || ids.isEmpty())
      return List.of();
    return ids.stream()
              .map(storage::get)
              .filter(Objects::nonNull)
              .toList();
  }

  public List<T> getByIds(Set<Long> ids) {
    if (ids == null)
      return List.of();
    return ids.stream()
              .toList()
              .stream()
              .map(storage::get)
              .toList();
  }
}
