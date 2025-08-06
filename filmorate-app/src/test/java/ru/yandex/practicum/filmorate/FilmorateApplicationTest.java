package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.events.domain.model.value.EventType;
import ru.yandex.practicum.filmorate.events.domain.model.value.Operation;
import ru.yandex.practicum.filmorate.films.domain.model.value.Genre;
import ru.yandex.practicum.filmorate.films.domain.model.value.Mpa;
import ru.yandex.practicum.filmorate.infrastructure.web.dto.*;
import ru.yandex.practicum.filmorate.infrastructure.web.exception.ErrorResponse;
import ru.yandex.practicum.filmorate.infrastructure.web.exception.ValidationErrorResponse;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test",
                 "db"
})
@Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DisplayName("Filmorate Integration Tests")
class FilmorateApplicationTest {

  @Autowired
  private TestRestTemplate restTemplate;

  private UserResponse createUser(CreateUserRequest request) {
    ResponseEntity<UserResponse> response = restTemplate.postForEntity("/users", request, UserResponse.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    return response.getBody();
  }

  private FilmResponse createFilm(CreateFilmRequest request) {
    ResponseEntity<FilmResponse> response = restTemplate.postForEntity("/films", request, FilmResponse.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    return response.getBody();
  }

  private ReviewResponse createReview(CreateReviewRequest request) {
    ResponseEntity<ReviewResponse> response = restTemplate.postForEntity("/reviews", request, ReviewResponse.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    return response.getBody();
  }

  @Nested
  @DisplayName("User API Tests")
  class UserTests {

    @Test
    @DisplayName("Should create user successfully")
    void shouldCreateUser() {
      CreateUserRequest request = new CreateUserRequest("test@example.com", "testlogin", "Test User",
                                                        LocalDate.of(1990, 5, 15));

      ResponseEntity<UserResponse> response = restTemplate.postForEntity("/users", request, UserResponse.class);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
      UserResponse body = response.getBody();
      assertThat(body).isNotNull();
      assertThat(body.id()).isNotNull();
      assertThat(body.email()).isEqualTo(request.email());
      assertThat(body.login()).isEqualTo(request.login());
      assertThat(body.name()).isEqualTo(request.name());
      assertThat(body.birthday()).isEqualTo(request.birthday());
    }

    @Test
    @DisplayName("Should fail to create user with invalid login")
    void shouldFailToCreateUserWithInvalidLogin() {
      CreateUserRequest request = new CreateUserRequest("test@example.com", "invalid login", "Test",
                                                        LocalDate.of(1990, 1, 1));
      ResponseEntity<ValidationErrorResponse> response = restTemplate.postForEntity("/users", request,
                                                                                    ValidationErrorResponse.class);
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Should fail to create user with invalid email")
    void shouldFailToCreateUserWithInvalidEmail() {
      CreateUserRequest request = new CreateUserRequest("not-an-email", "validlogin", "Test", LocalDate.of(1990, 1, 1));
      ResponseEntity<ValidationErrorResponse> response = restTemplate.postForEntity("/users", request,
                                                                                    ValidationErrorResponse.class);
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Should fail to create user with future birthday")
    void shouldFailToCreateUserWithFutureBirthday() {
      CreateUserRequest request = new CreateUserRequest("test@example.com", "validlogin", "Test", LocalDate.now()
                                                                                                           .plusDays(
                                                                                                               1));
      ResponseEntity<ValidationErrorResponse> response = restTemplate.postForEntity("/users", request,
                                                                                    ValidationErrorResponse.class);
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Should update an existing user")
    void shouldUpdateUser() {
      UserResponse createdUser = createUser(
          new CreateUserRequest("original@test.com", "original", "Original", LocalDate.of(1990, 1, 1)));
      UpdateUserRequest updateRequest = new UpdateUserRequest(createdUser.id(), "updated@test.com", "updated",
                                                              "Updated", LocalDate.of(1991, 2, 2));

      ResponseEntity<UserResponse> response = restTemplate.exchange("/users", HttpMethod.PUT,
                                                                    new HttpEntity<>(updateRequest),
                                                                    UserResponse.class);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      UserResponse body = response.getBody();
      assertThat(body).isNotNull();
      assertThat(body.id()).isEqualTo(createdUser.id());
      assertThat(body.email()).isEqualTo(updateRequest.email());
      assertThat(body.login()).isEqualTo(updateRequest.login());
    }

    @Test
    @DisplayName("Should return 404 when updating a non-existent user")
    void shouldReturnNotFoundForUnknownUserUpdate() {
      UpdateUserRequest updateRequest = new UpdateUserRequest(9999L, "a@a.com", "login", "name",
                                                              LocalDate.of(1990, 1, 1));
      ResponseEntity<ErrorResponse> response = restTemplate.exchange("/users", HttpMethod.PUT,
                                                                     new HttpEntity<>(updateRequest),
                                                                     ErrorResponse.class);
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Should get all users")
    void shouldGetAllUsers() {
      createUser(new CreateUserRequest("user1@test.com", "user1", "User One", LocalDate.of(1991, 1, 1)));
      createUser(new CreateUserRequest("user2@test.com", "user2", "User Two", LocalDate.of(1992, 2, 2)));

      ResponseEntity<UserResponse[]> response = restTemplate.getForEntity("/users", UserResponse[].class);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isNotNull();
      assertThat(response.getBody()).hasSize(2);
    }
  }

  @Nested
  @DisplayName("Friendship API Tests")
  class FriendshipTests {

    @Test
    @DisplayName("Should add a friend and create a pending request")
    void shouldAddFriend() {
      UserResponse user1 = createUser(
          new CreateUserRequest("user1@test.com", "user1", "User One", LocalDate.of(1991, 1, 1)));
      UserResponse user2 = createUser(
          new CreateUserRequest("user2@test.com", "user2", "User Two", LocalDate.of(1992, 2, 2)));

      restTemplate.put("/users/{id}/friends/{friendId}", null, user1.id(), user2.id());

      ResponseEntity<UserResponse[]> user1Friends = restTemplate.getForEntity("/users/{id}/friends",
                                                                              UserResponse[].class, user1.id());
      assertThat(user1Friends.getBody()).hasSize(1);
      assertThat(user1Friends.getBody()[0].id()).isEqualTo(user2.id());

      ResponseEntity<UserResponse[]> user2Friends = restTemplate.getForEntity("/users/{id}/friends",
                                                                              UserResponse[].class, user2.id());
      assertThat(user2Friends.getBody()).isEmpty();
    }

    @Test
    @DisplayName("Should confirm friendship when both users add each other")
    void shouldConfirmFriendship() {
      UserResponse user1 = createUser(
          new CreateUserRequest("user1@test.com", "user1", "User One", LocalDate.of(1991, 1, 1)));
      UserResponse user2 = createUser(
          new CreateUserRequest("user2@test.com", "user2", "User Two", LocalDate.of(1992, 2, 2)));

      restTemplate.put("/users/{id}/friends/{friendId}", null, user1.id(), user2.id());
      restTemplate.put("/users/{id}/friends/{friendId}", null, user2.id(), user1.id());

      ResponseEntity<UserResponse[]> user1Friends = restTemplate.getForEntity("/users/{id}/friends",
                                                                              UserResponse[].class, user1.id());
      assertThat(user1Friends.getBody()).hasSize(1);

      ResponseEntity<UserResponse[]> user2Friends = restTemplate.getForEntity("/users/{id}/friends",
                                                                              UserResponse[].class, user2.id());
      assertThat(user2Friends.getBody()).hasSize(1);
    }

    @Test
    @DisplayName("Should remove a friend")
    void shouldRemoveFriend() {
      UserResponse user1 = createUser(
          new CreateUserRequest("user1@test.com", "user1", "User One", LocalDate.of(1991, 1, 1)));
      UserResponse user2 = createUser(
          new CreateUserRequest("user2@test.com", "user2", "User Two", LocalDate.of(1992, 2, 2)));
      restTemplate.put("/users/{id}/friends/{friendId}", null, user1.id(), user2.id());

      restTemplate.delete("/users/{id}/friends/{friendId}", user1.id(), user2.id());

      ResponseEntity<UserResponse[]> friends = restTemplate.getForEntity("/users/{id}/friends", UserResponse[].class,
                                                                         user1.id());
      assertThat(friends.getBody()).isEmpty();
    }

    @Test
    @DisplayName("Should get common friends")
    void shouldGetCommonFriends() {
      UserResponse user1 = createUser(new CreateUserRequest("user1@test.com", "user1", "U1", LocalDate.of(1991, 1, 1)));
      UserResponse user2 = createUser(new CreateUserRequest("user2@test.com", "user2", "U2", LocalDate.of(1992, 2, 2)));
      UserResponse commonFriend = createUser(
          new CreateUserRequest("common@test.com", "common", "CF", LocalDate.of(1993, 3, 3)));

      restTemplate.put("/users/{id}/friends/{friendId}", null, user1.id(), commonFriend.id());
      restTemplate.put("/users/{id}/friends/{friendId}", null, commonFriend.id(), user1.id());
      restTemplate.put("/users/{id}/friends/{friendId}", null, user2.id(), commonFriend.id());
      restTemplate.put("/users/{id}/friends/{friendId}", null, commonFriend.id(), user2.id());

      ResponseEntity<UserResponse[]> response = restTemplate.getForEntity("/users/{id}/friends/common/{otherId}",
                                                                          UserResponse[].class, user1.id(), user2.id());

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).hasSize(1);
      assertThat(response.getBody()[0].id()).isEqualTo(commonFriend.id());
    }
  }

  @Nested
  @DisplayName("Film API Tests")
  class FilmTests {

    @Test
    @DisplayName("Should create a film successfully")
    void shouldCreateFilm() {
      CreateFilmRequest request = new CreateFilmRequest("Inception", "Mind-bending thriller", LocalDate.of(2010, 7, 16),
                                                        148, Set.of(new Genre(1L, "Комедия")), new Mpa(1L, "G"));

      ResponseEntity<FilmResponse> response = restTemplate.postForEntity("/films", request, FilmResponse.class);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      FilmResponse body = response.getBody();
      assertThat(body).isNotNull();
      assertThat(body.id()).isNotNull();
      assertThat(body.name()).isEqualTo(request.name());
    }

    @Test
    @DisplayName("Should fail to create film with blank name")
    void shouldFailToCreateFilmWithBlankName() {
      CreateFilmRequest request = new CreateFilmRequest("", "Description", LocalDate.of(2010, 1, 1), 120,
                                                        Set.of(new Genre(1L, "Комедия")), new Mpa(1L, "G"));
      ResponseEntity<ValidationErrorResponse> response = restTemplate.postForEntity("/films", request,
                                                                                    ValidationErrorResponse.class);
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Should fail to create film with too long description")
    void shouldFailToCreateFilmWithLongDescription() {
      String longDescription = "a".repeat(201);
      CreateFilmRequest request = new CreateFilmRequest("Film", longDescription, LocalDate.of(2010, 1, 1), 120,
                                                        Set.of(new Genre(1L, "Комедия")), new Mpa(1L, "G"));
      ResponseEntity<ErrorResponse> response = restTemplate.postForEntity("/films", request, ErrorResponse.class);
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Should fail to create film with invalid release date")
    void shouldFailToCreateFilmWithInvalidReleaseDate() {
      CreateFilmRequest request = new CreateFilmRequest("Film", "Description", LocalDate.of(1890, 1, 1), 120,
                                                        Set.of(new Genre(1L, "Комедия")), new Mpa(1L, "G"));
      ResponseEntity<ValidationErrorResponse> response = restTemplate.postForEntity("/films", request,
                                                                                    ValidationErrorResponse.class);
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Should fail to create film with negative duration")
    void shouldFailToCreateFilmWithNegativeDuration() {
      CreateFilmRequest request = new CreateFilmRequest("Film", "Description", LocalDate.of(2000, 1, 1), -100,
                                                        Set.of(new Genre(1L, "Комедия")), new Mpa(1L, "G"));
      ResponseEntity<ValidationErrorResponse> response = restTemplate.postForEntity("/films", request,
                                                                                    ValidationErrorResponse.class);
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Should get popular films")
    void shouldGetPopularFilms() {
      UserResponse u1 = createUser(new CreateUserRequest("u1@a.com", "u1", "u1", LocalDate.of(1990, 1, 1)));
      UserResponse u2 = createUser(new CreateUserRequest("u2@a.com", "u2", "u2", LocalDate.of(1990, 1, 1)));
      UserResponse u3 = createUser(new CreateUserRequest("u3@a.com", "u3", "u3", LocalDate.of(1990, 1, 1)));

      FilmResponse f1 = createFilm(
          new CreateFilmRequest("Film 1", "d", LocalDate.of(2001, 1, 1), 100, Set.of(new Genre(1L, "Комедия")),
                                new Mpa(1L, "G")));
      FilmResponse f2 = createFilm(
          new CreateFilmRequest("Film 2", "d", LocalDate.of(2002, 1, 1), 100, Set.of(new Genre(1L, "Комедия")),
                                new Mpa(1L, "G")));
      FilmResponse f3 = createFilm(
          new CreateFilmRequest("Film 3", "d", LocalDate.of(2003, 1, 1), 100, Set.of(new Genre(1L, "Комедия")),
                                new Mpa(1L, "G")));

      // Likes: f2 (3), f3 (2), f1 (1)
      restTemplate.put("/films/{id}/like/{userId}", null, f2.id(), u1.id());
      restTemplate.put("/films/{id}/like/{userId}", null, f2.id(), u2.id());
      restTemplate.put("/films/{id}/like/{userId}", null, f2.id(), u3.id());

      restTemplate.put("/films/{id}/like/{userId}", null, f3.id(), u1.id());
      restTemplate.put("/films/{id}/like/{userId}", null, f3.id(), u2.id());

      restTemplate.put("/films/{id}/like/{userId}", null, f1.id(), u1.id());

      ResponseEntity<FilmResponse[]> response = restTemplate.getForEntity("/films/popular?count=3",
                                                                          FilmResponse[].class);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      List<Long> popularIds = Arrays.stream(Objects.requireNonNull(response.getBody()))
                                    .map(FilmResponse::id)
                                    .toList();

      assertThat(popularIds).containsExactly(f2.id(), f3.id(), f1.id());
    }
  }

  @Nested
  @DisplayName("Like API Tests")
  class LikeTests {

    @Test
    @DisplayName("Should add a like to a film")
    void shouldAddLike() {
      UserResponse user = createUser(new CreateUserRequest("user@a.com", "user", "User", LocalDate.of(1990, 1, 1)));
      FilmResponse film = createFilm(
          new CreateFilmRequest("Film", "d", LocalDate.of(2000, 1, 1), 120, Set.of(new Genre(1L, "Комедия")),
                                new Mpa(1L, "G")));

      ResponseEntity<Void> response = restTemplate.exchange("/films/{id}/like/{userId}", HttpMethod.PUT, null,
                                                            Void.class, film.id(), user.id());

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Should remove a like from a film")
    void shouldRemoveLike() {
      UserResponse user = createUser(new CreateUserRequest("user@a.com", "user", "User", LocalDate.of(1990, 1, 1)));
      FilmResponse film = createFilm(
          new CreateFilmRequest("Film", "d", LocalDate.of(2000, 1, 1), 120, Set.of(new Genre(1L, "Комедия")),
                                new Mpa(1L, "G")));
      restTemplate.put("/films/{id}/like/{userId}", null, film.id(), user.id());

      ResponseEntity<Void> response = restTemplate.exchange("/films/{id}/like/{userId}", HttpMethod.DELETE, null,
                                                            Void.class, film.id(), user.id());
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
  }

  @Nested
  @DisplayName("MPA API Tests")
  class MpaTests {

    @Test
    @DisplayName("Should get all MPA ratings")
    void shouldGetAllMpaRatings() {
      ResponseEntity<MpaResponse[]> response = restTemplate.getForEntity("/mpa", MpaResponse[].class);
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).hasSize(5);
      assertThat(response.getBody()).extracting("name")
                                    .contains("G", "PG", "PG-13", "R", "NC-17");
    }

    @Test
    @DisplayName("Should get MPA by ID")
    void shouldGetMpaById() {
      ResponseEntity<MpaResponse> response = restTemplate.getForEntity("/mpa/1", MpaResponse.class);
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      MpaResponse body = response.getBody();
      assertThat(body).isNotNull();
      assertThat(body.id()).isEqualTo(1L);
      assertThat(body.name()).isEqualTo("G");
    }

    @Test
    @DisplayName("Should return 404 for non-existent MPA ID")
    void shouldReturnNotFoundForUnknownMpaId() {
      ResponseEntity<ErrorResponse> response = restTemplate.getForEntity("/mpa/9999", ErrorResponse.class);
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
  }

  @Nested
  @DisplayName("Genre API Tests")
  class GenreTests {

    @Test
    @DisplayName("Should get all Genres")
    void shouldGetAllGenres() {
      ResponseEntity<GenreResponse[]> response = restTemplate.getForEntity("/genres", GenreResponse[].class);
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).hasSize(6);
      assertThat(response.getBody()).extracting("name")
                                    .contains("Комедия", "Драма", "Мультфильм", "Триллер", "Документальный", "Боевик");
    }

    @Test
    @DisplayName("Should get Genre by ID")
    void shouldGetGenreById() {
      ResponseEntity<GenreResponse> response = restTemplate.getForEntity("/genres/1", GenreResponse.class);
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      GenreResponse body = response.getBody();
      assertThat(body).isNotNull();
      assertThat(body.id()).isEqualTo(1L);
      assertThat(body.name()).isEqualTo("Комедия");
    }

    @Test
    @DisplayName("Should return 404 for non-existent Genre ID")
    void shouldReturnNotFoundForUnknownGenreId() {
      ResponseEntity<ErrorResponse> response = restTemplate.getForEntity("/genres/9999", ErrorResponse.class);
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
  }

  @Nested
  @DisplayName("Review API Tests")
  class ReviewTests {

    private UserResponse user;
    private FilmResponse film;

    @BeforeEach
    void setUp() {
      user = createUser(new CreateUserRequest("newuser@mail.com", "user", "user", LocalDate.of(1990, 1, 1)));
      film = createFilm(new CreateFilmRequest("Film", "description", LocalDate.of(2000, 1, 1), 120,
              Set.of(new Genre(1L, "Комедия")), new Mpa(1L, "G")));
    }

    @Test
    @DisplayName("Create Review")
    void shouldCreateReview() {
      CreateReviewRequest createReviewRequest = new CreateReviewRequest("positive review", true, user.id(), film.id());
      ResponseEntity<ReviewResponse> responseCreate = restTemplate.postForEntity("/reviews", createReviewRequest, ReviewResponse.class);

      assertResponse(responseCreate, HttpStatus.OK, "positive review", true, 0);
    }

    @Test
    @DisplayName("Update Review")
    void shouldUpdateReview() {
      long reviewId = createReview("positive review", user.id(), film.id()).reviewId();

      UpdateReviewRequest updateReviewRequest = new UpdateReviewRequest(reviewId, "new content", false,
              user.id(), film.id());
      ResponseEntity<ReviewResponse> responseUpdate = restTemplate.exchange("/reviews", HttpMethod.PUT,
              new HttpEntity<>(updateReviewRequest), ReviewResponse.class);

      assertResponse(responseUpdate, HttpStatus.OK, "new content", false, 0);
    }

    @Test
    @DisplayName("Should get Review by id, filmId, count")
    void getReviewByParams() {
      long reviewId = createReview("positive review", user.id(), film.id()).reviewId();

      ResponseEntity<ReviewResponse> getResponse = restTemplate.getForEntity("/reviews/{reviewId}", ReviewResponse.class, reviewId);
      assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

      ResponseEntity<ReviewResponse[]> getResponseIdCount = restTemplate.getForEntity("/reviews?filmId={filmId}&count={count}", ReviewResponse[].class, film.id(), 2);
      assertThat(getResponseIdCount.getStatusCode()).isEqualTo(HttpStatus.OK);

      ResponseEntity<ReviewResponse[]> getResponseCount = restTemplate.getForEntity("/reviews?count={count}", ReviewResponse[].class, 2);
      assertThat(getResponseCount.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Should delete Review")
    void deleteSelectedReview() {
      long reviewId = createReview("positive review", user.id(), film.id()).reviewId();

      ResponseEntity<Void> deleteResponse = restTemplate.exchange("/reviews/{id}", HttpMethod.DELETE, null,
              Void.class, reviewId);
      assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Check reactions adding and removing")
    void shouldProcessReactions() {
      ReviewResponse review = createReview("positive review", user.id(), film.id());
      Integer initialUseful = review.useful();

      assertLikeReview(review.reviewId(), user.id(), initialUseful, initialUseful + 1);

      assertConflictOnDoubleLike(review.reviewId(), user.id());

      assertRemoveLike(review.reviewId(), user.id(), initialUseful, initialUseful);

      assertDislikeReview(review.reviewId(), user.id(), initialUseful, initialUseful - 1);

      assertRemoveDislike(review.reviewId(), user.id(), initialUseful, initialUseful);
    }

    private ReviewResponse createReview(String content, long userId, long filmId) {
      CreateReviewRequest createReviewRequest = new CreateReviewRequest(content, true, userId, filmId);
      ResponseEntity<ReviewResponse> response = restTemplate.postForEntity("/reviews", createReviewRequest,
              ReviewResponse.class);
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      return response.getBody();
    }

    private void assertResponse(ResponseEntity<ReviewResponse> response, HttpStatus expectedStatus, String expectedContent, boolean expectedIsPositive, int expectedUseful) {
      assertThat(response.getStatusCode()).isEqualTo(expectedStatus);
      assertThat(response.getBody()).extracting("content").isEqualTo(expectedContent);
      assertThat(response.getBody()).extracting("isPositive").isEqualTo(expectedIsPositive);
      assertThat(response.getBody()).extracting("useful").isEqualTo(expectedUseful);
    }

    private void assertLikeReview(long reviewId, long userId, Integer initialUseful, Integer result) {
      restTemplate.put("/reviews/{id}/like/{userId}", null, reviewId, userId);
      ResponseEntity<ReviewResponse> afterLikeResponse = restTemplate.getForEntity("/reviews/{reviewId}", ReviewResponse.class, reviewId);
      assertThat(afterLikeResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertEquals(result, afterLikeResponse.getBody().useful(), "Useful did not increment correctly");
    }

    private void assertConflictOnDoubleLike(long reviewId, long userId) {
      ResponseEntity<Void> responseDoubleLike = restTemplate.exchange("/reviews/{id}/like/{userId}", HttpMethod.PUT, null, Void.class, reviewId, userId);
      assertThat(responseDoubleLike.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    private void assertRemoveLike(long reviewId, long userId, Integer usefulCount, Integer result) {
      restTemplate.delete("/reviews/{id}/like/{userId}", reviewId, userId);
      ResponseEntity<ReviewResponse> afterLikeRemoveResponse = restTemplate.getForEntity("/reviews/{reviewId}", ReviewResponse.class, reviewId);
      assertThat(afterLikeRemoveResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertEquals(result, afterLikeRemoveResponse.getBody().useful(), "Useful count should decrement");
    }

    private void assertDislikeReview(long reviewId, long userId, Integer usefulCount, Integer result) {
      restTemplate.put("/reviews/{id}/dislike/{userId}", null, reviewId, userId);
      ResponseEntity<ReviewResponse> afterDislikeResponse = restTemplate.getForEntity("/reviews/{reviewId}", ReviewResponse.class, reviewId);
      assertThat(afterDislikeResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertEquals(result, afterDislikeResponse.getBody().useful(), "Useful did not decrement correctly");
    }

    private void assertRemoveDislike(long reviewId, long userId, Integer usefulCount, Integer result) {
      restTemplate.delete("/reviews/{id}/dislike/{userId}", reviewId, userId);
      ResponseEntity<ReviewResponse> afterDislikeRemoveResponse = restTemplate.getForEntity("/reviews/{reviewId}", ReviewResponse.class, reviewId);
      assertThat(afterDislikeRemoveResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertEquals(result, afterDislikeRemoveResponse.getBody().useful(), "Useful should increment again");
    }
  }
  @Nested
  @DisplayName("Event Feed API Tests")
  class EventFeedTests {

    private UserResponse user;

      @BeforeEach
    void setUp() {
      user = createUser(new CreateUserRequest("u1@a.com", "u1", "User1", LocalDate.of(1990, 1, 1)));
        UserResponse friend = createUser(new CreateUserRequest("u2@a.com", "u2", "User2", LocalDate.of(1990, 2, 2)));
      restTemplate.put("/users/{id}/friends/{friendId}", null, user.id(), friend.id());
      restTemplate.delete("/users/{id}/friends/{friendId}", user.id(), friend.id());
        FilmResponse film = createFilm(new CreateFilmRequest("Film", "desc", LocalDate.of(2000, 1, 1), 100, Set.of(new Genre(1L, "Комедия")), new Mpa(1L, "G")));
          ReviewResponse review = createReview(new CreateReviewRequest("content", false, user.id(), film.id()));
      restTemplate.put("/reviews", new HttpEntity<>(new UpdateReviewRequest(review.reviewId(), "updated", true, user.id(), film.id())));
      restTemplate.delete("/reviews/{id}", review.reviewId());
      restTemplate.put("/films/{id}/like/{userId}", null, film.id(), user.id());
      restTemplate.delete("/films/{id}/like/{userId}", film.id(), user.id());
    }

    @Test
    @DisplayName("Should return 7 events in chronological order")
    void shouldReturnSevenEvents() {
      ResponseEntity<EventResponse[]> response = restTemplate.getForEntity("/users/{id}/feed", EventResponse[].class, user.id());
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      EventResponse[] feed = response.getBody();
      assertThat(feed).hasSize(7);
      assertThat(feed[0].eventType()).isEqualTo(EventType.FRIEND);
      assertThat(feed[0].operation()).isEqualTo(Operation.ADD);
      assertThat(feed[6].eventType()).isEqualTo(EventType.LIKE);
      assertThat(feed[6].operation()).isEqualTo(Operation.REMOVE);
    }
  }
}