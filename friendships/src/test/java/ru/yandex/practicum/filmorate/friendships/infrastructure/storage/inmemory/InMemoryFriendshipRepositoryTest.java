package ru.yandex.practicum.filmorate.friendships.infrastructure.storage.inmemory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.friendships.domain.model.FriendshipEdge;
import ru.yandex.practicum.filmorate.friendships.domain.model.value.FriendshipStatus;
import ru.yandex.practicum.filmorate.friendships.domain.port.FriendshipRepository;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("In-Memory Friendship Repository")
class InMemoryFriendshipRepositoryTest {

  private static final long USER_ID_1 = 1L;
  private static final long USER_ID_2 = 2L;
  private static final long USER_ID_3 = 3L;
  private static final long USER_ID_4 = 4L;
  private static final long NON_EXISTENT_USER_ID = 99L;
  private FriendshipRepository repository;

  @BeforeEach
  void setUp() {
    repository = new InMemoryFriendshipRepository();
    repository.addVertex(USER_ID_1);
    repository.addVertex(USER_ID_2);
    repository.addVertex(USER_ID_3);
    repository.addVertex(USER_ID_4);
  }

  private FriendshipEdge pendingEdge() {
    return new FriendshipEdge(FriendshipStatus.PENDING);
  }

  private FriendshipEdge confirmedEdge() {
    return new FriendshipEdge(FriendshipStatus.CONFIRMED);
  }

  @Nested
  @DisplayName("Vertex (User) Operations")
  class VertexManagementTests {

    @Test
    @DisplayName("Should add a new vertex for a new user")
    void shouldAddVertex() {
      assertThat(repository.addVertex(100L)).isTrue();
    }

    @Test
    @DisplayName("Should return false when adding an existing vertex")
    void shouldNotAddExistingVertex() {
      assertThat(repository.addVertex(USER_ID_1)).isFalse();
    }

    @Test
    @DisplayName("Should remove an existing vertex")
    void shouldDeleteExistingVertex() {
      assertThat(repository.deleteVertex(USER_ID_1)).isTrue();
    }

    @Test
    @DisplayName("Should return false when removing a non-existent vertex")
    void shouldReturnFalseForNonExistentVertex() {
      assertThat(repository.deleteVertex(NON_EXISTENT_USER_ID)).isFalse();
    }
  }

  @Nested
  @DisplayName("Edge (Friendship) Operations")
  class EdgeManagementTests {

    @Test
    @DisplayName("addEdge should create a single directed edge")
    void shouldAddDirectedEdge() {
      boolean added = repository.addEdge(USER_ID_1,
                                         USER_ID_2,
                                         pendingEdge());

      assertThat(added).isTrue();
      assertThat(repository.getEdge(USER_ID_1,
                                    USER_ID_2)).isPresent();
      assertThat(repository.getEdge(USER_ID_2,
                                    USER_ID_1)).isEmpty();
    }

    @Test
    @DisplayName("getEdge should find an existing directed edge")
    void shouldGetExistingEdge() {
      repository.addEdge(USER_ID_1,
                         USER_ID_2,
                         pendingEdge());

      Optional<FriendshipEdge> edge = repository.getEdge(USER_ID_1,
                                                         USER_ID_2);

      assertThat(edge).isPresent();
      assertThat(edge.get()
                     .getStatus()).isEqualTo(FriendshipStatus.PENDING);
    }

    @Test
    @DisplayName("getEdge should return empty for a non-existent edge")
    void shouldReturnEmptyForNonExistentEdge() {
      Optional<FriendshipEdge> edge = repository.getEdge(USER_ID_1,
                                                         USER_ID_2);
      assertThat(edge).isEmpty();
    }

    @Test
    @DisplayName("removeEdge should remove a direct edge (user -> friend)")
    void shouldRemoveDirectEdge() {
      repository.addEdge(USER_ID_1,
                         USER_ID_2,
                         pendingEdge());

      Optional<FriendshipEdge> removedEdge = repository.removeEdge(USER_ID_1,
                                                                   USER_ID_2);

      assertThat(removedEdge).isPresent();
      assertThat(repository.getEdge(USER_ID_1,
                                    USER_ID_2)).isEmpty();
    }

    @Test
    @DisplayName("removeEdge should remove a reverse edge if direct not found")
    void shouldRemoveReverseEdge() {
      repository.addEdge(USER_ID_1,
                         USER_ID_2,
                         pendingEdge());

      Optional<FriendshipEdge> removedEdge = repository.removeEdge(USER_ID_2,
                                                                   USER_ID_1);

      assertThat(removedEdge).isPresent();
      assertThat(repository.getEdge(USER_ID_1,
                                    USER_ID_2)).isEmpty();
    }

    @Test
    @DisplayName("removeEdge should return empty optional if no edge exists in either direction")
    void shouldReturnEmptyWhenRemovingNonExistentEdge() {
      Optional<FriendshipEdge> removedEdge = repository.removeEdge(USER_ID_1,
                                                                   USER_ID_2);
      assertThat(removedEdge).isEmpty();
    }
  }

  @Nested
  @DisplayName("`finedEdgesByVertexId` Method Tests")
  class FindEdgesByVertexIdTests {

    @Test
    @DisplayName("Should return all neighbors, both incoming and outgoing, regardless of status")
    void shouldReturnAllNeighborsRegardlessOfDirectionAndStatus() {
      repository.addEdge(USER_ID_1,
                         USER_ID_2,
                         pendingEdge());
      repository.addEdge(USER_ID_3,
                         USER_ID_1,
                         confirmedEdge());

      Set<Long> neighborsOfUser1 = repository.finedEdgesByVertexId(USER_ID_1);

      assertThat(neighborsOfUser1).hasSize(2)
                                  .containsExactlyInAnyOrder(USER_ID_2,
                                                             USER_ID_3);
    }

    @Test
    @DisplayName("Should return an empty set for a user with no connections")
    void shouldReturnEmptySetForUserWithNoConnections() {
      Set<Long> neighbors = repository.finedEdgesByVertexId(USER_ID_4);
      assertThat(neighbors).isNotNull()
                           .isEmpty();
    }
  }
}