package ru.yandex.practicum.filmorate.infrastructure.web.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.infrastructure.web.dto.CreateFilmRequest;
import ru.yandex.practicum.filmorate.infrastructure.web.dto.FilmResponse;
import ru.yandex.practicum.filmorate.infrastructure.web.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.infrastructure.web.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.service.FilmCompositionService;

import java.util.List;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
  private final FilmCompositionService filmCompositionService;
  private final FilmMapper mapper;

  @GetMapping
  public List<FilmResponse> getAllFilms() {
    return filmCompositionService.getAllFilms()
                                 .stream()
                                 .map(mapper::toResponse)
                                 .toList();
  }

  @PostMapping
  public FilmResponse createFilm(@Valid @RequestBody CreateFilmRequest request) {
    return mapper.toResponse(filmCompositionService.createFilm(mapper.toCommand(request)));
  }

  @PutMapping
  public FilmResponse updateFilm(@Valid @RequestBody UpdateFilmRequest request) {
    return mapper.toResponse(filmCompositionService.updateFilm(mapper.toCommand(request)));
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
                                 .map(mapper::toResponse)
                                 .toList();
  }
}
