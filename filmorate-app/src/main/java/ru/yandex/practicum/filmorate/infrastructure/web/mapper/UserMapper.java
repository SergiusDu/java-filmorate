package ru.yandex.practicum.filmorate.infrastructure.web.mapper;


import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.films.domain.model.User;
import ru.yandex.practicum.filmorate.films.domain.port.CreateUserCommand;
import ru.yandex.practicum.filmorate.films.domain.port.UpdateUserCommand;
import ru.yandex.practicum.filmorate.infrastructure.web.dto.CreateUserRequest;
import ru.yandex.practicum.filmorate.infrastructure.web.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.infrastructure.web.dto.UserResponse;

@Component
public class UserMapper {
  public CreateUserCommand toCommand(CreateUserRequest request) {
    return CreateUserCommand.builder()
                            .email(request.email())
                            .login(request.login())
                            .name(request.name() != null ? request.name() : request.login())
                            .birthday(request.birthday())
                            .build();
  }

  public UpdateUserCommand toCommand(UpdateUserRequest request) {
    return UpdateUserCommand.builder()
                            .id(request.id())
                            .email(request.email())
                            .login(request.login())
                            .name(request.name() != null ? request.name() : request.login())
                            .birthday(request.birthday())
                            .build();
  }

  public UserResponse toResponse(User user) {
    return UserResponse.builder()
                       .id(user.id())
                       .email(user.email()
                                  .email())
                       .login(user.login()
                                  .login())
                       .name(user.name())
                       .birthday(user.birthday())
                       .build();
  }
}
