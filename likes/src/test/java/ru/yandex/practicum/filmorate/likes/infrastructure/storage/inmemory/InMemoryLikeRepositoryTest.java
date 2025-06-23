package ru.yandex.practicum.filmorate.likes.infrastructure.storage.inmemory;

import domain.port.LikeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("In-Memory Like Repository Tests")
class InMemoryLikeRepositoryTest {

  private LikeRepository likeRepository;

  @BeforeEach
  void setUp() {
    likeRepository = new InMemoryLikeRepository();
  }

  private void createFilmWithZeroLikes(long filmId) {
    likeRepository.addLike(filmId,
                           1L);
    likeRepository.removeLike(filmId,
                              1L);
  }

  @Nested
  @DisplayName("addLike Method Tests")
  class AddLikeTests {

    @Test
    @DisplayName("Should return true when adding the first like to a film")
    void shouldReturnTrueForFirstLike() {
      boolean result = likeRepository.addLike(1L,
                                              100L);
      assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should return false when adding a duplicate like")
    void shouldReturnFalseForDuplicateLike() {
      likeRepository.addLike(1L,
                             100L);
      boolean result = likeRepository.addLike(1L,
                                              100L);
      assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should correctly update popularity index after adding likes")
    void shouldUpdatePopularityIndexOnAdd() {
      likeRepository.addLike(1L,
                             100L);
      likeRepository.addLike(1L,
                             101L);
      likeRepository.addLike(2L,
                             100L);

      Set<Long> popular = likeRepository.getPopularFilmIds(2);
      assertThat(popular).containsExactly(1L,
                                          2L);
    }
  }

  @Nested
  @DisplayName("removeLike Method Tests")
  class RemoveLikeTests {

    @Test
    @DisplayName("Should return true when removing an existing like")
    void shouldReturnTrueForRemovingExistingLike() {
      likeRepository.addLike(1L,
                             100L);
      boolean result = likeRepository.removeLike(1L,
                                                 100L);
      assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should return false when removing a non-existent like")
    void shouldReturnFalseForRemovingNonExistentLike() {
      likeRepository.addLike(1L,
                             100L);
      boolean result = likeRepository.removeLike(1L,
                                                 999L);
      assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should return false when removing a like for a non-existent film")
    void shouldReturnFalseForRemovingLikeFromNonExistentFilm() {
      boolean result = likeRepository.removeLike(99L,
                                                 100L);
      assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should correctly update popularity index after removing likes")
    void shouldUpdatePopularityIndexOnRemove() {
      likeRepository.addLike(1L,
                             100L);
      likeRepository.addLike(1L,
                             101L);
      likeRepository.addLike(2L,
                             100L);

      likeRepository.removeLike(1L,
                                100L);

      Set<Long> popular = likeRepository.getPopularFilmIds(2);
      assertThat(popular).containsExactly(1L,
                                          2L);
      assertThat(popular.contains(1L)).isTrue();
    }

    @Test
    @DisplayName("Should remove film from popularity index when its last like is removed")
    void shouldRemoveFilmFromIndexWhenLastLikeIsRemoved() {
      likeRepository.addLike(1L,
                             100L);
      likeRepository.removeLike(1L,
                                100L);

      Set<Long> popular = likeRepository.getPopularFilmIds(1);
      assertThat(popular).isEmpty();
    }
  }

  @Nested
  @DisplayName("getPopularFilmIds Method Tests")
  class GetPopularFilmIdsTests {

    @Test
    @DisplayName("Should return an empty list when no films have likes")
    void shouldReturnEmptyListWhenNoLikes() {
      Set<Long> popular = likeRepository.getPopularFilmIds(10);
      assertThat(popular).isNotNull()
                         .isEmpty();
    }

    @Test
    @DisplayName("Should return films sorted by like count in descending order")
    void shouldReturnFilmsSortedByLikeCount() {
      likeRepository.addLike(10L,
                             1L);

      likeRepository.addLike(20L,
                             1L);
      likeRepository.addLike(20L,
                             2L);
      likeRepository.addLike(20L,
                             3L);

      likeRepository.addLike(30L,
                             1L);
      likeRepository.addLike(30L,
                             2L);

      Set<Long> popular = likeRepository.getPopularFilmIds(3);
      assertThat(popular).containsExactly(20L,
                                          30L,
                                          10L);
    }

    @Test
    @DisplayName("Should handle ties by sorting by filmId ascending")
    void shouldHandleTiesByFilmId() {
      likeRepository.addLike(10L,
                             1L);
      likeRepository.addLike(10L,
                             2L);

      likeRepository.addLike(5L,
                             1L);
      likeRepository.addLike(5L,
                             2L);

      Set<Long> popular = likeRepository.getPopularFilmIds(2);
      assertThat(popular).containsExactly(5L,
                                          10L);
    }

    @Test
    @DisplayName("Should respect the count parameter")
    void shouldRespectCountParameter() {
      likeRepository.addLike(1L,
                             1L);
      likeRepository.addLike(2L,
                             1L);
      likeRepository.addLike(3L,
                             1L);

      Set<Long> popular = likeRepository.getPopularFilmIds(2);
      assertThat(popular).hasSize(2);
    }

    @Test
    @DisplayName("Should return all liked films if count is larger than total")
    void shouldReturnAllWhenCountIsLarge() {
      likeRepository.addLike(1L,
                             1L);
      likeRepository.addLike(2L,
                             1L);

      Set<Long> popular = likeRepository.getPopularFilmIds(10);
      assertThat(popular).hasSize(2);
    }

    @Test
    @DisplayName("Should return empty list for a count of zero")
    void shouldReturnEmptyForCountZero() {
      likeRepository.addLike(1L,
                             1L);
      Set<Long> popular = likeRepository.getPopularFilmIds(0);
      assertThat(popular).isEmpty();
    }

    @Test
    @DisplayName("Should dynamically update ranking when likes change")
    void shouldDynamicallyUpdateRanking() {
      likeRepository.addLike(10L,
                             1L);
      likeRepository.addLike(10L,
                             2L);
      likeRepository.addLike(20L,
                             1L);

      assertThat(likeRepository.getPopularFilmIds(2)).containsExactly(10L,
                                                                      20L);

      likeRepository.addLike(20L,
                             2L);
      likeRepository.addLike(20L,
                             3L);

      assertThat(likeRepository.getPopularFilmIds(2)).containsExactly(20L,
                                                                      10L);
    }
  }

  @Nested
  @DisplayName("findUsersWhoLikedFilm Method Tests")
  class FindUsersWhoLikedFilmTests {

    @Test
    @DisplayName("Should return correct set of users who liked a film")
    void shouldReturnCorrectSetOfUsers() {
      likeRepository.addLike(10L,
                             100L);
      likeRepository.addLike(10L,
                             200L);
      likeRepository.addLike(20L,
                             100L);

      Set<Long> users = likeRepository.findUsersWhoLikedFilm(10L);
      assertThat(users).hasSize(2)
                       .containsExactlyInAnyOrder(100L,
                                                  200L);
    }

    @Test
    @DisplayName("Should return an empty set for a film with no likes")
    void shouldReturnEmptySetForFilmWithNoLikes() {
      createFilmWithZeroLikes(10L);
      Set<Long> users = likeRepository.findUsersWhoLikedFilm(10L);
      assertThat(users).isNotNull()
                       .isEmpty();
    }

    @Test
    @DisplayName("Should return an empty set for a non-existent film")
    void shouldReturnEmptySetForNonExistentFilm() {
      Set<Long> users = likeRepository.findUsersWhoLikedFilm(99L);
      assertThat(users).isNotNull()
                       .isEmpty();
    }
  }
}