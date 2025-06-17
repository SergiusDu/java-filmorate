package ru.yandex.practicum.filmorate.common.infrastructure.storage.inmemory;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.common.exception.ResourceNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;

@RequiredArgsConstructor
public abstract class AbstractInMemoryRepository<T, C, U> {
  protected final ConcurrentHashMap<Integer, T> storage = new ConcurrentHashMap<>();
  private final AtomicInteger idGenerator = new AtomicInteger(0);

  private final BiFunction<Integer, C, T> createBuilder;
  private final Function<U, T> updateBuilder;
  private final Function<U, Integer> updateIdExtractor;
  private final Function<T, Integer> entityIdExtractor;

  public final T save(C createCommand) {
    T value = createBuilder.apply(generateNextId(),
                                  createCommand);
    storage.put(entityIdExtractor.apply(value),
                value);
    return value;
  }

  public final Integer generateNextId() {
    return idGenerator.incrementAndGet();
  }

  public final List<T> findAll() {
    return new ArrayList<>(storage.values());
  }

  public final T update(U updateCommand) {
    Integer id = updateIdExtractor.apply(updateCommand);
    if (!storage.contains(id)) {
      throw new ResourceNotFoundException("Entity with id " + id + " not found");
    }
    T value = updateBuilder.apply(updateCommand);
    storage.put(id,
                value);
    return value;
  }

  public final Optional<T> findById(int id) {
    return Optional.ofNullable(storage.get(id));
  }
}
