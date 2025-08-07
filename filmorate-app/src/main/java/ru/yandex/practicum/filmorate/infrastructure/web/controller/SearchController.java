package ru.yandex.practicum.filmorate.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.infrastructure.web.dto.FilmResponse;
import ru.yandex.practicum.filmorate.infrastructure.web.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.search.application.port.in.SearchUseCase;
import ru.yandex.practicum.filmorate.service.FilmCompositionService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class SearchController {

  private final SearchUseCase searchUseCase;
  private final FilmCompositionService filmCompositionService;
  private final FilmMapper filmMapper;

  @GetMapping("/search")
  public List<FilmResponse> searchFilms(@RequestParam String query, @RequestParam List<String> by) {
    List<Long> filmIds = searchUseCase.searchFilms(query,
                                                   by.stream()
                                                     .map(String::toUpperCase)
                                                     .collect(Collectors.toSet()));

    if (filmIds.isEmpty()) {
      return Collections.emptyList();
    }

    return filmCompositionService.enrichFilmsWithDirectors(filmCompositionService.getFilmByIds(filmIds))
                                 .stream()
                                 .map(filmMapper::toResponse)
                                 .collect(Collectors.toList());
  }
}