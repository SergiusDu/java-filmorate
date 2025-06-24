package ru.yandex.practicum.filmorate.friendships.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.friendships.application.port.in.FriendshipsUseCase;
import ru.yandex.practicum.filmorate.friendships.domain.port.FriendshipRepository;

import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class FriendshipService implements FriendshipsUseCase {
  private final FriendshipRepository friendshipRepository;

  @Override
  public boolean addUser(long userId) {
    return friendshipRepository.addUser(userId);
  }

  @Override
  public void addFriend(long userId, long friendId) {
    friendshipRepository.addFriend(userId,
                                   friendId);
  }

  @Override
  public void removeFriend(long userId, long friendId) {
    friendshipRepository.removeFriend(userId,
                                      friendId);
  }

  @Override
  public Set<Long> getFriends(long userId) {
    return friendshipRepository.findFriendsById(userId);
  }

  @Override
  public Set<Long> getMutualFriends(long userId, long friendId) {
    Set<Long> userFriends = friendshipRepository.findFriendsById(userId);
    if (userFriends.isEmpty())
      return Set.of();
    Set<Long> friendFriends = friendshipRepository.findFriendsById(friendId);
    if (friendFriends.isEmpty())
      return Set.of();

    return userFriends.size() < friendFriends.size()
           ? userFriends.stream()
                        .filter(friendFriends::contains)
                        .collect(Collectors.toSet())
           : friendFriends.stream()
                          .filter(userFriends::contains)
                          .collect(Collectors.toSet());
  }
}
