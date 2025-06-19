package ru.yandex.practicum.filmorate.films.infrastructure.storage.inmemory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.common.infrastructure.storage.inmemory.AbstractInMemoryRepository;
import ru.yandex.practicum.filmorate.films.domain.model.Film;
import ru.yandex.practicum.filmorate.films.domain.port.CreateFilmCommand;
import ru.yandex.practicum.filmorate.films.domain.port.FilmRepository;
import ru.yandex.practicum.filmorate.films.domain.port.UpdateFilmCommand;
import ru.yandex.practicum.filmorate.films.infrastructure.web.mapper.FilmMapper;

@Slf4j
@Repository
public class InMemoryFilmRepository extends AbstractInMemoryRepository<Film, CreateFilmCommand, UpdateFilmCommand> implements FilmRepository {

  public InMemoryFilmRepository(FilmMapper mapper) {
    super(mapper::fromCommand,
          mapper::fromCommand,
          UpdateFilmCommand::id,
          Film::id);
  }
}
