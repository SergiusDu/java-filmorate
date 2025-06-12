package ru.yandex.practicum.filmorate.application.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.application.port.in.FilmUseCase;
import ru.yandex.practicum.filmorate.domain.model.Film;
import ru.yandex.practicum.filmorate.domain.port.FilmRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService implements FilmUseCase {
  FilmRepository filmRepository;

  @Override
  public Film addFilm(Film film) {
    return filmRepository.save(film);
  }

  @Override
  public Film updateFilm(Film film) {
    return filmRepository.update(film);
  }

  @Override
  public List<Film> getAllFilms() {
    return filmRepository.findAll();
  }
}
