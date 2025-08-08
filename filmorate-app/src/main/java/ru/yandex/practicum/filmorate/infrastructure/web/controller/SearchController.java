package ru.yandex.practicum.filmorate.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.infrastructure.web.dto.FilmResponse;
import ru.yandex.practicum.filmorate.infrastructure.web.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.service.FilmCompositionService;

import java.util.List;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class SearchController {
  private final FilmCompositionService filmCompositionService;
  private final FilmMapper filmMapper;

  @GetMapping("/search")
  public List<FilmResponse> searchFilms(@RequestParam String query, @RequestParam List<String> by) {
    return filmCompositionService.searchFilms(query, by)
                                 .stream()
                                 .map(filmMapper::toResponse)
                                 .toList();
  }
}