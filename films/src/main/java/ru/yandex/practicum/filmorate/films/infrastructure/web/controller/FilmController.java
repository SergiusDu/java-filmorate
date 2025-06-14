package ru.yandex.practicum.filmorate.films.infrastructure.web.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.films.application.port.in.FilmUseCase;
import ru.yandex.practicum.filmorate.films.infrastructure.web.dto.CreateFilmRequest;
import ru.yandex.practicum.filmorate.films.infrastructure.web.dto.FilmResponse;
import ru.yandex.practicum.filmorate.films.infrastructure.web.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.films.infrastructure.web.mapper.FilmMapper;

import java.util.List;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
  private final FilmUseCase filmUseCase;

  @GetMapping
  public List<FilmResponse> getAllFilms() {
    return filmUseCase.getAllFilms()
                      .stream()
                      .map(FilmMapper::toResponse)
                      .toList();
  }

  @PostMapping
  public FilmResponse createFilm(@Valid @RequestBody CreateFilmRequest request) {
    return FilmMapper.toResponse(filmUseCase.addFilm(request));
  }

  @PutMapping
  public FilmResponse updateFilm(@Valid @RequestBody UpdateFilmRequest request) {
    return FilmMapper.toResponse(filmUseCase.updateFilm(request));
  }
}
