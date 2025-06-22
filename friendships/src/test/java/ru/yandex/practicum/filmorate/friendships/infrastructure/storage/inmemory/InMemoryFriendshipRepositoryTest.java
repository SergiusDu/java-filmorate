package ru.yandex.practicum.filmorate.friendships.infrastructure.storage.inmemory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("In-Memory Friendship Repository Tests")
class InMemoryFriendshipRepositoryTest {

  private InMemoryFriendshipRepository friendshipRepository;

  @BeforeEach
  void setUp() {
    friendshipRepository = new InMemoryFriendshipRepository();
  }

  @Test
  @DisplayName("Should successfully add a new user vertex to the graph")
  void shouldSuccessfullyAddUser() {
    boolean result = friendshipRepository.addUser(1L);
    assertThat(result).isTrue();
  }

  @Test
  @DisplayName("Should return false when adding an existing user")
  void shouldReturnFalseWhenAddingExistingUser() {
    friendshipRepository.addUser(1L);
    boolean result = friendshipRepository.addUser(1L);
    assertThat(result).isFalse();
  }

  @Test
  @DisplayName("Should successfully delete a user vertex from the graph")
  void shouldSuccessfullyDeleteUser() {
    friendshipRepository.addUser(1L);
    boolean result = friendshipRepository.deleteUser(1L);
    assertThat(result).isTrue();
  }

  @Test
  @DisplayName("Should return false when trying to delete a non-existent user")
  void shouldReturnFalseWhenDeletingNonExistentUser() {
    boolean result = friendshipRepository.deleteUser(99L);
    assertThat(result).isFalse();
  }

  @Test
  @DisplayName("Should successfully create a friendship between two users")
  void shouldSuccessfullyAddFriend() {
    friendshipRepository.addUser(1L);
    friendshipRepository.addUser(2L);

    friendshipRepository.addFriend(1L,
                                   2L);

    Set<Long> friendsOfUser1 = friendshipRepository.findFriendsById(1L);
    Set<Long> friendsOfUser2 = friendshipRepository.findFriendsById(2L);

    assertThat(friendsOfUser1).containsExactly(2L);
    assertThat(friendsOfUser2).containsExactly(1L);
  }

  @Test
  @DisplayName("Should throw an exception when trying to add a friend with a non-existent user")
  void shouldThrowExceptionWhenAddingFriendWithNonExistentUser() {
    friendshipRepository.addUser(1L);

    assertThrows(IllegalArgumentException.class,
                 () -> {
                   friendshipRepository.addFriend(1L,
                                                  99L);
                 });
  }

  @Test
  @DisplayName("Should successfully remove a friendship between two users")
  void shouldSuccessfullyRemoveFriend() {
    friendshipRepository.addUser(1L);
    friendshipRepository.addUser(2L);
    friendshipRepository.addFriend(1L,
                                   2L);

    assertThat(friendshipRepository.findFriendsById(1L)).contains(2L);

    friendshipRepository.removeFriend(1L,
                                      2L);

    assertThat(friendshipRepository.findFriendsById(1L)).isEmpty();
    assertThat(friendshipRepository.findFriendsById(2L)).isEmpty();
  }

  @Test
  @DisplayName("Should return an empty set for a user with no friends")
  void shouldReturnEmptySetForUserWithNoFriends() {
    friendshipRepository.addUser(1L);
    Set<Long> friends = friendshipRepository.findFriendsById(1L);
    assertThat(friends).isNotNull()
                       .isEmpty();
  }

  @Test
  @DisplayName("Should return a correct set of multiple friends")
  void shouldReturnCorrectSetOfFriends() {
    friendshipRepository.addUser(1L);
    friendshipRepository.addUser(2L);
    friendshipRepository.addUser(3L);
    friendshipRepository.addUser(4L);

    friendshipRepository.addFriend(1L,
                                   2L);
    friendshipRepository.addFriend(1L,
                                   4L);

    Set<Long> friendsOfUser1 = friendshipRepository.findFriendsById(1L);
    assertThat(friendsOfUser1).hasSize(2)
                              .containsExactlyInAnyOrder(2L,
                                                         4L);
  }

  @Test
  @DisplayName("Should remove all friendships when a user is deleted")
  void shouldRemoveAllFriendshipsWhenUserIsDeleted() {
    friendshipRepository.addUser(1L);
    friendshipRepository.addUser(2L);
    friendshipRepository.addUser(3L);

    friendshipRepository.addFriend(1L,
                                   2L);
    friendshipRepository.addFriend(1L,
                                   3L);

    assertThat(friendshipRepository.findFriendsById(2L)).contains(1L);

    friendshipRepository.deleteUser(1L);

    assertThat(friendshipRepository.findFriendsById(2L)).isEmpty();
    assertThat(friendshipRepository.findFriendsById(3L)).isEmpty();
  }
}