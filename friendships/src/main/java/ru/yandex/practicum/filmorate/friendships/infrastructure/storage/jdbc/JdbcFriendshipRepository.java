package ru.yandex.practicum.filmorate.friendships.infrastructure.storage.jdbc;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.friendships.domain.model.FriendshipEdge;
import ru.yandex.practicum.filmorate.friendships.domain.model.value.FriendshipStatus;
import ru.yandex.practicum.filmorate.friendships.domain.port.FriendshipRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@Profile("db")
@RequiredArgsConstructor
public class JdbcFriendshipRepository implements FriendshipRepository {

  private final JdbcTemplate jdbcTemplate;

  private final RowMapper<FriendshipEdge> edgeRowMapper = (rs, rowNum) -> new FriendshipEdge(
      FriendshipStatus.valueOf(rs.getString("status")));

  @Override
  public boolean addVertex(long sourceId) {
    // In the relational database model the vertex will exist.
    return true;
  }

  @Override
  public boolean deleteVertex(long sourceId) {
    String sql = "DELETE FROM friendships WHERE user_id = ? OR friend_id = ?";
    return jdbcTemplate.update(sql, sourceId, sourceId) > 0;
  }

  @Override
  public boolean addEdge(long sourceId, long targetId, FriendshipEdge edge) {
    String sql = "INSERT INTO friendships (user_id, friend_id, status) VALUES (?, ?, ?)";
    return jdbcTemplate.update(sql, sourceId, targetId, edge.getStatus()
                                                            .name()) > 0;
  }

  @Override
  public Optional<FriendshipEdge> removeEdge(long sourceId, long targetId) {
    Optional<FriendshipEdge> edge = getEdge(sourceId, targetId);

    if (edge.isPresent()) {
      String sql = "DELETE FROM friendships WHERE user_id = ? AND friend_id = ?";
      jdbcTemplate.update(sql, sourceId, targetId);
    }

    Optional<FriendshipEdge> reverseEdge = getEdge(targetId, sourceId);
    if (reverseEdge.isPresent() && reverseEdge.get()
                                              .getStatus() == FriendshipStatus.CONFIRMED) {
      String sql = "UPDATE friendships SET status = ? WHERE user_id = ? AND friend_id = ?";
      jdbcTemplate.update(sql, FriendshipStatus.PENDING.name(), targetId, sourceId);
    }

    return edge;
  }

  @Override
  public Set<Long> finedEdgesByVertexId(long sourceId) {
    String sql = "(SELECT friend_id FROM friendships WHERE user_id = ?) " + "UNION " +
                 "(SELECT user_id FROM friendships WHERE friend_id = ? AND status = 'CONFIRMED')";

    List<Long> friendIds = jdbcTemplate.queryForList(sql, Long.class, sourceId, sourceId);
    return new HashSet<>(friendIds);
  }

  @Override
  public Optional<FriendshipEdge> getEdge(long sourceId, long targetId) {
    String sql = "SELECT * FROM friendships WHERE user_id = ? AND friend_id = ?";
    List<FriendshipEdge> edges = jdbcTemplate.query(sql, edgeRowMapper, sourceId, targetId);
    return edges.stream()
                .findFirst();
  }
}