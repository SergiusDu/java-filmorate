package ru.yandex.practicum.filmorate.infrastructure.web.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.infrastructure.web.dto.*;
import ru.yandex.practicum.filmorate.infrastructure.web.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.infrastructure.web.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.infrastructure.web.mapper.MpaMapper;
import ru.yandex.practicum.filmorate.service.FilmCompositionService;

import java.util.List;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
  private final FilmCompositionService filmCompositionService;
  private final FilmMapper filmMapper;
  private final GenreMapper genreMapper;
  private final MpaMapper mpaMapper;

  @GetMapping
  public List<FilmResponse> getAllFilms() {
    return filmCompositionService.getAllFilms()
                                 .stream()
                                 .map(filmMapper::toResponse)
                                 .toList();
  }

  @PostMapping
  public FilmResponse createFilm(@Valid @RequestBody CreateFilmRequest request) {
    return filmMapper.toResponse(filmCompositionService.createFilm(filmMapper.toCommand(request)));
  }

  @PutMapping
  public FilmResponse updateFilm(@Valid @RequestBody UpdateFilmRequest request) {
    return filmMapper.toResponse(filmCompositionService.updateFilm(filmMapper.toCommand(request)));
  }

  @PutMapping("/{id}/like/{userId}")
  public void likeFilm(@PathVariable long id, @PathVariable long userId) {
    filmCompositionService.addLike(id,
                                   userId);
  }

  @DeleteMapping("/{id}/like/{userId}")
  public void deleteLike(@PathVariable long id, @PathVariable long userId) {
    filmCompositionService.removeLike(id,
                                      userId);
  }

  @GetMapping("/popular")
  public List<FilmResponse> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
    return filmCompositionService.getPopularFilms(count)
                                 .stream()
                                 .map(filmMapper::toResponse)
                                 .toList();
  }

  @GetMapping("/genre")
  public List<GenreResponse> getGenres() {
    return filmCompositionService.getGenres()
                                 .stream()
                                 .map(genreMapper::toResponse)
                                 .toList();
  }

  @GetMapping("/genre/{id}")
  public GenreResponse getGenre(@PathVariable long id) {
    return genreMapper.toResponse(filmCompositionService.getGenreById(id));
  }

  @GetMapping("/mpa")
  public List<MpaResponse> getMpas() {
    return filmCompositionService.getMpas()
                                 .stream()
                                 .map(mpaMapper::toResponse)
                                 .toList();
  }

  @GetMapping("/mpa/{id}")
  public MpaResponse getMpaById(@PathVariable long id) {
    return mpaMapper.toResponse(filmCompositionService.getMpaById(id));
  }
}