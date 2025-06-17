package ru.yandex.practicum.filmorate.films.web.mapper;


import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.films.domain.model.User;
import ru.yandex.practicum.filmorate.films.domain.model.value.Email;
import ru.yandex.practicum.filmorate.films.domain.model.value.Login;
import ru.yandex.practicum.filmorate.films.domain.port.CreateUserCommand;
import ru.yandex.practicum.filmorate.films.domain.port.UpdateUserCommand;
import ru.yandex.practicum.filmorate.films.web.dto.CreateUserRequest;
import ru.yandex.practicum.filmorate.films.web.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.films.web.dto.UserResponse;

@Component
public class UserMapper {

  public CreateUserCommand toCommand(CreateUserRequest request) {
    return new CreateUserCommand(request.email(),
                                 request.login(),
                                 request.name(),
                                 request.birthday());
  }

  public UpdateUserCommand toCommand(UpdateUserRequest request) {
    return new UpdateUserCommand(request.id(),
                                 request.email(),
                                 request.login(),
                                 request.name(),
                                 request.birthday());
  }

  public User fromCommand(Integer id, CreateUserCommand command) {
    return new User(id,
                    new Email(command.email()),
                    new Login(command.login()),
                    command.name(),
                    command.birthday());
  }

  public User fromCommand(UpdateUserCommand updateCommand) {
    return new User(updateCommand.id(),
                    new Email(updateCommand.email()),
                    new Login(updateCommand.login()),
                    updateCommand.name(),
                    updateCommand.birthday());
  }

  public UserResponse toResponse(User user) {
    return new UserResponse(user.id(),
                            user.email()
                                .email(),
                            user.login()
                                .login(),
                            user.name(),
                            user.birthday());
  }

  public User toDomain(CreateUserRequest request, int id) {
    return new User(id,
                    new Email(request.email()),
                    new Login(request.login()),
                    request.name(),
                    request.birthday());
  }

  public User toDomain(UpdateUserRequest request) {
    return new User(request.id(),
                    new Email(request.email()),
                    new Login(request.login()),
                    request.name(),
                    request.birthday());
  }
}
