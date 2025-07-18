package ru.yandex.practicum.filmorate.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.infrastructure.web.dto.GenreResponse;
import ru.yandex.practicum.filmorate.infrastructure.web.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.service.FilmCompositionService;

import java.util.List;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {
  private final GenreMapper genreMapper;
  private final FilmCompositionService filmCompositionService;

  @GetMapping
  public List<GenreResponse> getGenres() {
    return filmCompositionService.getGenres()
                                 .stream()
                                 .map(genreMapper::toResponse)
                                 .toList();
  }

  @GetMapping("/{id}")
  public GenreResponse getGenre(@PathVariable long id) {
    return genreMapper.toResponse(filmCompositionService.getGenreById(id));
  }
}
