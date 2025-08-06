package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.common.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.events.domain.service.DomainEventPublisher;
import ru.yandex.practicum.filmorate.events.domain.model.value.Operation;
import ru.yandex.practicum.filmorate.friendships.application.port.in.FriendshipsUseCase;
import ru.yandex.practicum.filmorate.likes.application.port.in.LikeUseCase;
import ru.yandex.practicum.filmorate.users.application.port.in.UserUseCase;
import ru.yandex.practicum.filmorate.users.domain.model.User;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserCompositionService {
  private final UserUseCase userUseCase;
  private final FriendshipsUseCase friendshipsUseCase;
  private final LikeUseCase likeUseCase;
  private final DomainEventPublisher eventPublisher;

  public List<User> getFriendsOfUser(long userId) {
    validateUserExists(userId);
    Set<Long> friendsIds = friendshipsUseCase.getFriends(userId);
    if (friendsIds.isEmpty())
      return Collections.emptyList();

    return userUseCase.findUsersByIds(friendsIds);
  }

  public void validateUserExists(long userId) {
    if (userUseCase.findUserById(userId)
                   .isEmpty())
      throw new ResourceNotFoundException("User with id " + userId + " not found.");
  }

  public List<User> getMutualFriends(long userId,
                                     long friendId) {
    Set<Long> mutualFriendsIds = friendshipsUseCase.getMutualFriends(userId,
                                                                     friendId);
    if (mutualFriendsIds.isEmpty())
      return Collections.emptyList();
    return userUseCase.findUsersByIds(mutualFriendsIds);
  }

  public void addFriend(long userId,
                        long friendId) {
    validateUsersExists(Set.of(userId,
                               friendId));
    friendshipsUseCase.addFriend(userId,
                                 friendId);
    eventPublisher.publishFriendEvent(userId, Operation.ADD, friendId);
  }

  private void validateUsersExists(Set<Long> userIds) {
    if (userUseCase.findUsersByIds(userIds)
                   .size() != userIds.size())
      throw new ResourceNotFoundException("One or more users not found.");
  }

  public void removeFriend(long userId,
                           long friendId) {
    validateUsersExists(Set.of(userId,
                               friendId));
    friendshipsUseCase.removeFriend(userId,
                                    friendId);
    eventPublisher.publishFriendEvent(userId, Operation.REMOVE, friendId);
  }

  public User getUserById(long id) {
    return userUseCase.findUserById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found."));
  }

  public void deleteUserById(long userId) {
    likeUseCase.deleteLikesByUserId(userId);
    userUseCase.deleteUserById(userId);
  }
}
