package ru.yandex.practicum.filmorate.friendships.domain.port;

import ru.yandex.practicum.filmorate.friendships.domain.model.FriendshipEdge;

import java.util.Optional;
import java.util.Set;

public interface FriendshipRepository {

  boolean addEdge(long sourceId,
                  long targetId,
                  FriendshipEdge edge);

  Optional<FriendshipEdge> removeEdge(long sourceId,
                                      long targetId);

  Set<Long> finedEdgesByVertexId(long sourceId);

  Optional<FriendshipEdge> getEdge(long sourceId,
                                   long targetId);

  boolean updateEdge(long sourceId,
                     long targetId,
                     FriendshipEdge edge);
}
