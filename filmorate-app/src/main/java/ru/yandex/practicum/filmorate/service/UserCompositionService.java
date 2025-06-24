package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.common.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.films.application.port.in.UserUseCase;
import ru.yandex.practicum.filmorate.films.domain.model.User;
import ru.yandex.practicum.filmorate.friendships.application.port.in.FriendshipsUseCase;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserCompositionService {
  private final UserUseCase userUseCase;
  private final FriendshipsUseCase friendshipsUseCase;

  public List<User> getFriendsOfUser(long userId) {
    validateUserExists(userId);
    Set<Long> friendsIds = friendshipsUseCase.getFriends(userId);
    if (friendsIds.isEmpty())
      return Collections.emptyList();

    return userUseCase.findUsersByIds(friendsIds);
  }

  private void validateUserExists(long userId) {
    if (userUseCase.findUserById(userId)
                   .isEmpty())
      throw new ResourceNotFoundException("User with id " + userId + " not found.");
  }

  public List<User> getMutualFriends(long userId, long friendId) {
    Set<Long> mutualFriendsIds = friendshipsUseCase.getMutualFriends(userId,
                                                                     friendId);
    if (mutualFriendsIds.isEmpty())
      return Collections.emptyList();
    return userUseCase.findUsersByIds(mutualFriendsIds);
  }

  public void addFriend(long userId, long friendId) {
    validateUsersExists(Set.of(userId,
                               friendId));
    friendshipsUseCase.addFriend(userId,
                                 friendId);
  }

  private void validateUsersExists(Set<Long> userIds) {
    if (userUseCase.findUsersByIds(userIds)
                   .size() != userIds.size())
      throw new ResourceNotFoundException("One or more users not found.");
  }

  public void removeFriend(long userId, long friendId) {
    validateUsersExists(Set.of(userId,
                               friendId));
    friendshipsUseCase.removeFriend(userId,
                                    friendId);
  }
}
