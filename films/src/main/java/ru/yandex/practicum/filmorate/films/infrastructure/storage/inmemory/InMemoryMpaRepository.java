package ru.yandex.practicum.filmorate.films.infrastructure.storage.inmemory;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.common.infrastructure.storage.inmemory.AbstractInMemoryRepository;
import ru.yandex.practicum.filmorate.films.domain.factory.MpaFactory;
import ru.yandex.practicum.filmorate.films.domain.model.value.Mpa;
import ru.yandex.practicum.filmorate.films.domain.port.CreateMpaCommand;
import ru.yandex.practicum.filmorate.films.domain.port.UpdateMpaCommand;

@Repository
public class InMemoryMpaRepository extends AbstractInMemoryRepository<Mpa, CreateMpaCommand, UpdateMpaCommand> {
  public InMemoryMpaRepository(MpaFactory factory) {
    super(factory::create,
          factory::update,
          UpdateMpaCommand::id,
          Mpa::id);
  }
}
