package ru.yandex.practicum.filmorate.films.infrastructure.storage.inmemory;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.common.infrastructure.storage.inmemory.AbstractInMemoryRepository;
import ru.yandex.practicum.filmorate.films.domain.factory.GenreFactory;
import ru.yandex.practicum.filmorate.films.domain.model.value.Genre;
import ru.yandex.practicum.filmorate.films.domain.port.CreateGenreCommand;
import ru.yandex.practicum.filmorate.films.domain.port.GenreRepository;
import ru.yandex.practicum.filmorate.films.domain.port.UpdateGenreCommand;

@Repository
public class InMemoryGenreRepository extends AbstractInMemoryRepository<Genre, CreateGenreCommand,
    UpdateGenreCommand> implements GenreRepository {

  public InMemoryGenreRepository(GenreFactory factory) {
    super(factory::create,
          factory::update,
          UpdateGenreCommand::id,
          Genre::id);
  }
}
