package ru.yandex.practicum.filmorate.infrastructure.web.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.films.application.port.in.RecommendationQuery;
import ru.yandex.practicum.filmorate.films.application.port.in.RecommendationUseCase;
import ru.yandex.practicum.filmorate.infrastructure.web.dto.CreateUserRequest;
import ru.yandex.practicum.filmorate.infrastructure.web.dto.FilmResponse;
import ru.yandex.practicum.filmorate.infrastructure.web.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.infrastructure.web.dto.UserResponse;
import ru.yandex.practicum.filmorate.infrastructure.web.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.infrastructure.web.mapper.UserMapper;
import ru.yandex.practicum.filmorate.service.UserCompositionService;
import ru.yandex.practicum.filmorate.users.application.port.in.UserUseCase;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

  private final UserUseCase userUseCase;
  private final UserMapper userMapper;
  private final UserCompositionService userCompositionService;
  private final RecommendationUseCase recommendationUseCase;
  private final FilmMapper filmMapper;

  @GetMapping
  public List<UserResponse> getAllUsers() {
    return userUseCase.getAllUsers()
                      .stream()
                      .map(userMapper::toResponse)
                      .toList();
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public UserResponse createUser(@Valid @RequestBody CreateUserRequest request) {
    return userMapper.toResponse(userUseCase.addUser(userMapper.toCommand(request)));
  }

  @PutMapping
  public UserResponse updateUser(@Valid @RequestBody UpdateUserRequest request) {
    return userMapper.toResponse(userUseCase.updateUser(userMapper.toCommand(request)));
  }

  @PutMapping("/{id}/friends/{friendId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void addFriend(@PathVariable long id,
                        @PathVariable long friendId) {
    userCompositionService.addFriend(id,
                                     friendId);
  }

  @DeleteMapping("/{id}/friends/{friendId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteFriend(@PathVariable long id,
                           @PathVariable long friendId) {
    userCompositionService.removeFriend(id,
                                        friendId);
  }

  @GetMapping("/{id}/friends")
  public List<UserResponse> getFriends(@PathVariable long id) {
    return userCompositionService.getFriendsOfUser(id)
                                 .stream()
                                 .map(userMapper::toResponse)
                                 .toList();
  }

  @GetMapping("/{id}/friends/common/{otherId}")
  public List<UserResponse> getMutualFriends(@PathVariable long id,
                                             @PathVariable long otherId) {
    return userCompositionService.getMutualFriends(id,
                                                   otherId)
                                 .stream()
                                 .map(userMapper::toResponse)
                                 .toList();
  }

  /**
   * Returns a list of recommended films for the user with optional filters.
   *
   * @param id      ID of the target user
   * @param limit   (optional) max number of results
   * @param genreId (optional) genre filter
   * @param year    (optional) year filter
   * @return list of recommended films
   */

  @GetMapping("/{id}/recommendations")
  public List<FilmResponse> getRecommendations(
          @PathVariable long id,
          @RequestParam(required = false) Integer limit,
          @RequestParam(required = false) Long genreId,
          @RequestParam(required = false) Integer year
  ) {
    RecommendationQuery query = new RecommendationQuery(
            id,
            Optional.ofNullable(limit),
            Optional.ofNullable(genreId),
            Optional.ofNullable(year)
    );

    return recommendationUseCase.getRecommendations(query).stream()
            .map(filmMapper::toResponse)
            .toList();
  }
}
