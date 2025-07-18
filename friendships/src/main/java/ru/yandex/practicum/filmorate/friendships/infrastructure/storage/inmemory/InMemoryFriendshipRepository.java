package ru.yandex.practicum.filmorate.friendships.infrastructure.storage.inmemory;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.concurrent.AsSynchronizedGraph;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.friendships.domain.model.FriendshipEdge;
import ru.yandex.practicum.filmorate.friendships.domain.port.FriendshipRepository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Repository
@Profile("in-memory")
public class InMemoryFriendshipRepository implements FriendshipRepository {
  private final Graph<Long, FriendshipEdge> friendshipGraph = new AsSynchronizedGraph<>(
      new DefaultDirectedGraph<>(FriendshipEdge.class));

  @Override
  public boolean addVertex(long sourceId) {
    return friendshipGraph.addVertex(sourceId);
  }

  @Override
  public boolean deleteVertex(long sourceId) {
    return friendshipGraph.removeVertex(sourceId);
  }

  @Override
  public boolean addEdge(long sourceId, long targetId, FriendshipEdge edge) {
    return friendshipGraph.addEdge(sourceId, targetId, edge);
  }

  @Override
  public Optional<FriendshipEdge> removeEdge(long sourceId, long targetId) {
    FriendshipEdge edge = friendshipGraph.removeEdge(sourceId, targetId);
    if (edge == null) {
      edge = friendshipGraph.removeEdge(targetId, sourceId);
    }
    return Optional.ofNullable(edge);
  }

  @Override
  public Set<Long> finedEdgesByVertexId(long sourceId) {
    return new HashSet<>(Graphs.neighborListOf(friendshipGraph, sourceId));
  }

  @Override
  public Optional<FriendshipEdge> getEdge(long sourceId, long targetId) {
    return Optional.ofNullable(friendshipGraph.getEdge(sourceId, targetId));
  }

  @Override
  public boolean updateEdge(long sourceId, long targetId, FriendshipEdge edge) {
    if (friendshipGraph.containsEdge(sourceId, targetId)) {
      friendshipGraph.removeEdge(sourceId, targetId);
      return friendshipGraph.addEdge(sourceId, targetId, edge);
    }
    return false;
  }
}
