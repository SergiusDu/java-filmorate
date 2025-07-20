package ru.yandex.practicum.filmorate.friendships.application.port.in;

import java.util.Set;

public interface FriendshipsUseCase {
  void addFriend(long userId,
                 long friendId);

  void removeFriend(long userId,
                    long friendId);

  Set<Long> getFriends(long userId);

  Set<Long> getMutualFriends(long userId,
                             long friendId);
}
