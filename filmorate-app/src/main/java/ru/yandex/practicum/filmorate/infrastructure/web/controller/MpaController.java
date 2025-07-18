package ru.yandex.practicum.filmorate.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.infrastructure.web.dto.MpaResponse;
import ru.yandex.practicum.filmorate.infrastructure.web.mapper.MpaMapper;
import ru.yandex.practicum.filmorate.service.FilmCompositionService;

import java.util.List;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {
  private final MpaMapper mpaMapper;
  private final FilmCompositionService filmCompositionService;

  @GetMapping
  public List<MpaResponse> getMpas() {
    return filmCompositionService.getMpas()
                                 .stream()
                                 .map(mpaMapper::toResponse)
                                 .toList();
  }

  @GetMapping("{id}")
  public MpaResponse getMpaById(@PathVariable long id) {
    return mpaMapper.toResponse(filmCompositionService.getMpaById(id));
  }

}
