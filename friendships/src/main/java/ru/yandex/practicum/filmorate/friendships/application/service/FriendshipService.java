package ru.yandex.practicum.filmorate.friendships.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.friendships.application.port.in.FriendshipsUseCase;
import ru.yandex.practicum.filmorate.friendships.domain.model.FriendshipEdge;
import ru.yandex.practicum.filmorate.friendships.domain.model.value.FriendshipStatus;
import ru.yandex.practicum.filmorate.friendships.domain.port.FriendshipRepository;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class FriendshipService implements FriendshipsUseCase {
  private final FriendshipRepository friendshipRepository;

  @Override
  public boolean addUser(long userId) {
    return friendshipRepository.addVertex(userId);
  }

  @Override
  public void addFriend(long userId, long friendId) {
    Optional<FriendshipEdge> directEdge = friendshipRepository.getEdge(userId, friendId);
    Optional<FriendshipEdge> reversedEdge = friendshipRepository.getEdge(friendId, userId);
    if (directEdge.isPresent() || reversedEdge.isPresent() && reversedEdge.get()
                                                                          .getStatus() == FriendshipStatus.CONFIRMED) {
      throw new IllegalStateException("Friendship request is pending or has confirmed status");
    }

    if (reversedEdge.isPresent() && reversedEdge.get()
                                                .getStatus() == FriendshipStatus.PENDING) {
      friendshipRepository.updateEdge(friendId, userId, new FriendshipEdge(FriendshipStatus.CONFIRMED));
    } else {
      friendshipRepository.addEdge(userId, friendId, new FriendshipEdge(FriendshipStatus.PENDING));
    }
  }

  @Override
  public void removeFriend(long userId, long friendId) {
    friendshipRepository.removeEdge(userId, friendId);
  }

  @Override
  public Set<Long> getFriends(long userId) {
    return friendshipRepository.finedEdgesByVertexId(userId);
  }

  @Override
  public Set<Long> getMutualFriends(long userId, long friendId) {
    Set<Long> userFriends = friendshipRepository.finedEdgesByVertexId(userId);
    if (userFriends.isEmpty())
      return Set.of();
    Set<Long> friendFriends = friendshipRepository.finedEdgesByVertexId(friendId);
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
