package ru.yandex.practicum.filmorate.films.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.films.application.port.in.FilmUseCase;
import ru.yandex.practicum.filmorate.films.domain.model.Film;
import ru.yandex.practicum.filmorate.films.domain.port.CreateFilmCommand;
import ru.yandex.practicum.filmorate.films.domain.port.FilmRepository;
import ru.yandex.practicum.filmorate.films.domain.port.UpdateFilmCommand;
import ru.yandex.practicum.filmorate.films.domain.service.FilmValidationService;

import java.util.List;
import java.util.Optional;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class FilmService implements FilmUseCase {
  final FilmRepository filmRepository;
  private final FilmValidationService filmValidationService;

  @Override
  public Film addFilm(CreateFilmCommand command) {
    filmValidationService.validate(command);
    return filmRepository.save(command);
  }

  @Override
  public Film updateFilm(UpdateFilmCommand command) {
    filmValidationService.validate(command);
    return filmRepository.update(command);
  }

  @Override
  public Optional<Film> findFilmById(long filmId) {
    return filmRepository.findById(filmId);
  }

  @Override
  public List<Film> getAllFilms() {
    return filmRepository.findAll();
  }

  @Override
  public List<Film> getFilmsByIds(Set<Long> ids) {
    return filmRepository.getByIds(ids);
  }
}
