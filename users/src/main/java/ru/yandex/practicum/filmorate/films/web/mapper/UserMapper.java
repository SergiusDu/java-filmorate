package ru.yandex.practicum.filmorate.films.web.mapper;


import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.films.domain.model.User;
import ru.yandex.practicum.filmorate.films.domain.model.value.Email;
import ru.yandex.practicum.filmorate.films.domain.model.value.Login;
import ru.yandex.practicum.filmorate.films.web.dto.CreateUserRequest;
import ru.yandex.practicum.filmorate.films.web.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.films.web.dto.UserResponse;

import java.util.UUID;

@Component
public class UserMapper {
  public static UserResponse toResponse(User user) {
    return new UserResponse(user.id(),
                            user.email()
                                .email(),
                            user.login()
                                .login(),
                            user.name(),
                            user.birthday());
  }

  public static User toDomain(CreateUserRequest request, UUID id) {
    return new User(id,
                    new Email(request.email()),
                    new Login(request.login()),
                    request.name(),
                    request.birthday());
  }

  public static User toDomain(UpdateUserRequest request) {
    return new User(request.id(),
                    new Email(request.email()),
                    new Login(request.login()),
                    request.name(),
                    request.birthday());
  }
}
