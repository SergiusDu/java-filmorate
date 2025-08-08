package ru.yandex.practicum.filmorate.directors.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.common.enums.SortBy;
import ru.yandex.practicum.filmorate.common.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.directors.application.port.in.DirectorUseCase;
import ru.yandex.practicum.filmorate.directors.domain.model.Director;
import ru.yandex.practicum.filmorate.directors.domain.port.CreateDirectorCommand;
import ru.yandex.practicum.filmorate.directors.domain.port.DirectorRepository;
import ru.yandex.practicum.filmorate.directors.domain.port.UpdateDirectorCommand;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DirectorService
    implements DirectorUseCase {
  private final DirectorRepository directorRepository;

  @Override
  public Director createDirector(CreateDirectorCommand command) {
    return directorRepository.save(command);
  }

  @Override
  public List<Director> findAll() {
    return directorRepository.findAll();
  }

  @Override
  public Director findDirectorById(long id) {
    return directorRepository
               .findById(id)
               .orElseThrow(() -> new ResourceNotFoundException("Director with ID " + id + " not found"));
  }

  @Override
  public List<Director> findDirectorsByIds(Set<Long> ids) {
    return directorRepository.findByIds(ids);
  }

  @Override
  public Director updateDirector(UpdateDirectorCommand command) {
    if (!directorRepository.existsById(command.id())) {
      throw new ResourceNotFoundException("Director with ID " + command.id() + " not found");
    }
    return directorRepository.update(command);
  }

  @Override
  public void deleteDirectorById(long id) {
    if (!directorRepository.deleteById(id)) {
      throw new ResourceNotFoundException("Director with ID " + id + " not found");
    }
  }

  @Override
  public List<Long> getFilmIdsByDirector(long directorId, SortBy sortBy) {
    if (!directorRepository.existsById(directorId)) {
      throw new ResourceNotFoundException("Director with ID " + directorId + " not found");
    }
    return directorRepository.findFilmIdsByDirectorId(directorId, sortBy);
  }

  @Override
  public void updateFilmDirectors(long filmId, Set<Long> directorIds) {
    if (directorIds != null && !directorIds.isEmpty()) {
      List<Director> foundDirectors = directorRepository.findByIds(directorIds);
      if (foundDirectors.size() != directorIds.size()) {
        throw new ResourceNotFoundException("One or more directors not found.");
      }
    }
    directorRepository.updateFilmDirectors(filmId, directorIds);
  }

  @Override
  public Map<Long, List<Director>> getDirectorsForFilmIds(Set<Long> filmIds) {
    return directorRepository.findDirectorsForFilmIds(filmIds);
  }
}
