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

  private final FilmCompositionService filmService;
  private final FilmMapper filmMapper;

  @GetMapping
  public List<FilmResponse> getAllFilms() {
    return filmService.getAllFilms()
            .stream()
            .map(filmMapper::toResponse)
            .toList();
  }

  @PostMapping
  public FilmResponse createFilm(@Valid @RequestBody CreateFilmRequest request) {
    return filmMapper.toResponse(filmService.createFilm(filmMapper.toCommand(request)));
  }

  @PutMapping
  public FilmResponse updateFilm(@Valid @RequestBody UpdateFilmRequest request) {
    return filmMapper.toResponse(filmService.updateFilm(filmMapper.toCommand(request)));
  }

  @PutMapping("/{id}/like/{userId}")
  public void likeFilm(@PathVariable long id, @PathVariable long userId) {
    filmService.addLike(id, userId);
  }

  @DeleteMapping("/{id}/like/{userId}")
  public void deleteLike(@PathVariable long id, @PathVariable long userId) {
    filmService.removeLike(id, userId);
  }

  @GetMapping("/popular")
  public List<FilmResponse> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
    return filmService.getPopularFilms(count)
            .stream()
            .map(filmMapper::toResponse)
            .toList();
  }

  @GetMapping("/{id}")
  public FilmResponse getFilmById(@PathVariable long id) {
    return filmMapper.toResponse(filmService.getFilmById(id));
  }

  @GetMapping("/common")
  public List<FilmResponse> getCommonFilms(@RequestParam long userId,
                                           @RequestParam long friendId) {
    return filmService.getCommonFilms(userId, friendId)
            .stream()
            .map(filmMapper::toResponse)
            .toList();
  }
}