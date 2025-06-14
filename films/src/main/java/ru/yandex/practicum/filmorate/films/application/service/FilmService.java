package ru.yandex.practicum.filmorate.films.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.films.application.port.in.FilmUseCase;
import ru.yandex.practicum.filmorate.films.domain.model.Film;
import ru.yandex.practicum.filmorate.films.domain.port.FilmRepository;
import ru.yandex.practicum.filmorate.films.domain.service.FilmValidationService;
import ru.yandex.practicum.filmorate.films.infrastructure.web.dto.CreateFilmRequest;
import ru.yandex.practicum.filmorate.films.infrastructure.web.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.films.infrastructure.web.mapper.FilmMapper;

import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class FilmService implements FilmUseCase {
  final FilmRepository filmRepository;
  private final FilmValidationService filmValidationService;

  @Override
  public Film addFilm(CreateFilmRequest request) {
    Film film = FilmMapper.toDomain(request,
                                    UUID.randomUUID());
    filmValidationService.validate(film);
    return filmRepository.save(film);
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
