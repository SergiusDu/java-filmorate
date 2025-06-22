package ru.yandex.practicum.filmorate.friendships.domain.port;

import java.util.Set;

public interface FriendshipRepository {
  boolean addUser(long userId);

  boolean deleteUser(long userId);

  void addFriend(long userId, long friendId);

  void removeFriend(long userId, long friendId);

  Set<Long> findFriendsById(long userId);
}
