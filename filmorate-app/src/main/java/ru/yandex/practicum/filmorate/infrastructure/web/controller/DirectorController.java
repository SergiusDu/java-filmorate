package ru.yandex.practicum.filmorate.infrastructure.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.directors.application.port.in.DirectorUseCase;
import ru.yandex.practicum.filmorate.infrastructure.web.dto.CreateDirectorRequest;
import ru.yandex.practicum.filmorate.infrastructure.web.dto.DirectorResponse;
import ru.yandex.practicum.filmorate.infrastructure.web.dto.UpdateDirectorRequest;
import ru.yandex.practicum.filmorate.infrastructure.web.mapper.DirectorMapper;

import java.util.List;

@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {
  private final DirectorUseCase directorUseCase;
  private final DirectorMapper directorMapper;

  @GetMapping
  public List<DirectorResponse> getAllDirectors() {
    return directorUseCase
               .findAll()
               .stream()
               .map(directorMapper::toResponse)
               .toList();
  }

  @GetMapping("/{id}")
  public DirectorResponse getDirectorById(@PathVariable long id) {
    return directorMapper.toResponse(directorUseCase.findDirectorById(id));
  }

  @PostMapping
  public DirectorResponse createDirector(@Valid @RequestBody CreateDirectorRequest request) {
    return directorMapper.toResponse(directorUseCase.createDirector(directorMapper.toCommand(request)));
  }

  @PutMapping
  public DirectorResponse updateDirector(@Valid @RequestBody UpdateDirectorRequest request) {
    return directorMapper.toResponse(directorUseCase.updateDirector(directorMapper.toCommand(request)));
  }

  @DeleteMapping("/{id}")
  public void deleteDirector(@PathVariable long id) {
    directorUseCase.deleteDirectorById(id);
  }
}
