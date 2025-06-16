package ru.yandex.practicum.filmorate.films.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.films.application.port.in.FilmUseCase;
import ru.yandex.practicum.filmorate.films.domain.model.Film;
import ru.yandex.practicum.filmorate.films.domain.port.CreateFilmCommand;
import ru.yandex.practicum.filmorate.films.domain.port.FilmRepository;
import ru.yandex.practicum.filmorate.films.domain.service.FilmValidationService;
import ru.yandex.practicum.filmorate.films.infrastructure.web.dto.CreateFilmRequest;
import ru.yandex.practicum.filmorate.films.infrastructure.web.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.films.infrastructure.web.mapper.FilmMapper;

import java.util.List;


@Service
@RequiredArgsConstructor
public class FilmService implements FilmUseCase {
  final FilmRepository filmRepository;
  private final FilmValidationService filmValidationService;

  @Override
  public Film addFilm(CreateFilmRequest request) {
    CreateFilmCommand createFilmCommand = FilmMapper.toCreateCommand(request);
    filmValidationService.validate(createFilmCommand);
    return filmRepository.save(createFilmCommand);
  }

  @Override
  public Film updateFilm(UpdateFilmRequest request) {
    Film film = FilmMapper.toDomain(request);
    filmValidationService.validate(film);
    return filmRepository.update(film);
  }

  @Override
  public List<Film> getAllFilms() {
    return filmRepository.findAll();
  }
}
