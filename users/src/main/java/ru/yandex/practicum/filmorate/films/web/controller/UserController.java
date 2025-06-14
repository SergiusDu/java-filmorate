package ru.yandex.practicum.filmorate.films.web.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.films.application.port.in.UserUseCase;
import ru.yandex.practicum.filmorate.films.web.dto.CreateUserRequest;
import ru.yandex.practicum.filmorate.films.web.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.films.web.dto.UserResponse;
import ru.yandex.practicum.filmorate.films.web.mapper.UserMapper;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
  private final UserUseCase userUseCase;

  @GetMapping
  public List<UserResponse> getAllUsers() {
    return userUseCase.getAllUsers()
                      .stream()
                      .map(UserMapper::toResponse)
                      .toList();
  }

  @PostMapping
  public UserResponse createUser(@Valid @RequestBody CreateUserRequest request) {
    return UserMapper.toResponse(userUseCase.addUser(request));
  }

  @PutMapping
  public UserResponse updateUser(@Valid @RequestBody UpdateUserRequest request) {
    return UserMapper.toResponse(userUseCase.updateUser(request));
  }
}
