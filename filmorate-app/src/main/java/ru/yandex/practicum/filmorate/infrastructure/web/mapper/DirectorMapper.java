package ru.yandex.practicum.filmorate.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.directors.domain.model.Director;
import ru.yandex.practicum.filmorate.directors.domain.port.CreateDirectorCommand;
import ru.yandex.practicum.filmorate.directors.domain.port.UpdateDirectorCommand;
import ru.yandex.practicum.filmorate.infrastructure.web.dto.CreateDirectorRequest;
import ru.yandex.practicum.filmorate.infrastructure.web.dto.DirectorResponse;
import ru.yandex.practicum.filmorate.infrastructure.web.dto.UpdateDirectorRequest;

@Component
public class DirectorMapper {
  public CreateDirectorCommand toCommand(CreateDirectorRequest request) {
    return new CreateDirectorCommand(request.name());
  }

  public UpdateDirectorCommand toCommand(UpdateDirectorRequest request) {
    return new UpdateDirectorCommand(request.id(), request.name());
  }

  public DirectorResponse toResponse(Director director) {
    return new DirectorResponse(director.id(), director.name());
  }
}
