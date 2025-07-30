// удален избыточный javadoc
package ru.yandex.practicum.filmorate.films.domain.port;

import ru.yandex.practicum.filmorate.common.exception.DuplicateResourceException;
import ru.yandex.practicum.filmorate.common.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.films.domain.model.Film;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface FilmRepository {

  Film save(CreateFilmCommand createFilmCommand) throws DuplicateResourceException;

  Film update(UpdateFilmCommand updateCommand) throws ResourceNotFoundException;

  List<Film> findAll();

  Optional<Film> findById(long id);

  List<Film> getByIds(Set<Long> ids);

  // Retrieves films that were liked by both users and sorts them by the total number of likes in descending order.
  List<Film> findCommonFilmsSortedByLikes(long userId, long friendId);
}