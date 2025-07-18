package ru.yandex.practicum.filmorate.films.infrastructure.storage.inmemory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.common.infrastructure.storage.inmemory.AbstractInMemoryRepository;
import ru.yandex.practicum.filmorate.films.domain.factory.FilmFactory;
import ru.yandex.practicum.filmorate.films.domain.model.Film;
import ru.yandex.practicum.filmorate.films.domain.port.CreateFilmCommand;
import ru.yandex.practicum.filmorate.films.domain.port.FilmRepository;
import ru.yandex.practicum.filmorate.films.domain.port.UpdateFilmCommand;

@Repository
@Slf4j
@Profile("in-memory")
public class InMemoryFilmRepository extends AbstractInMemoryRepository<Film, CreateFilmCommand, UpdateFilmCommand> implements FilmRepository {

  public InMemoryFilmRepository(FilmFactory factory) {
    super(factory::create,
          factory::update,
          UpdateFilmCommand::id,
          Film::id);
  }
}
