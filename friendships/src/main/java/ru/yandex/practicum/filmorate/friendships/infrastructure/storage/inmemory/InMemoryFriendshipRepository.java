package ru.yandex.practicum.filmorate.friendships.infrastructure.storage.inmemory;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.concurrent.AsSynchronizedGraph;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.friendships.domain.port.FriendshipRepository;

import java.util.HashSet;
import java.util.Set;

@Repository
public class InMemoryFriendshipRepository implements FriendshipRepository {
  private final Graph<Long, DefaultEdge> friendshipGraph =
      new AsSynchronizedGraph<>(new SimpleGraph<>(DefaultEdge.class));

  @Override
  public boolean addUser(long userId) {
    return friendshipGraph.addVertex(userId);
  }

  @Override
  public boolean deleteUser(long userId) {
    return friendshipGraph.removeVertex(userId);
  }

  @Override
  public void addFriend(long userId, long friendId) {
    friendshipGraph.addEdge(userId,
                            friendId);
  }

  @Override
  public void removeFriend(long userId, long friendId) {
    friendshipGraph.removeEdge(userId,
                               friendId);
  }

  @Override
  public Set<Long> findFriendsById(long userId) {
    return new HashSet<>(Graphs.neighborListOf(friendshipGraph,
                                               userId));
  }
}
