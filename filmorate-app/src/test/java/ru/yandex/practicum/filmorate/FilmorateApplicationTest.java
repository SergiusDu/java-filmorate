package ru.yandex.practicum.filmorate;

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
import ru.yandex.practicum.filmorate.films.domain.model.value.Genre;
import ru.yandex.practicum.filmorate.films.domain.model.value.Mpa;
import ru.yandex.practicum.filmorate.infrastructure.web.dto.*;
import ru.yandex.practicum.filmorate.infrastructure.web.exception.ErrorResponse;
import ru.yandex.practicum.filmorate.infrastructure.web.exception.ValidationErrorResponse;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

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

  private DirectorResponse createDirector(CreateDirectorRequest request) {
    ResponseEntity<DirectorResponse> response = restTemplate.postForEntity("/directors",
                                                                           request,
                                                                           DirectorResponse.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    return response.getBody();
  }

  private FilmResponse createFilm(CreateFilmRequest request) {
    ResponseEntity<FilmResponse> response = restTemplate.postForEntity("/films", request, FilmResponse.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    return response.getBody();
  }

  @Nested
  @DisplayName("User API Tests")
  class UserTests {

    @Test
    @DisplayName("Should create user successfully")
    void shouldCreateUser() {
      CreateUserRequest request = new CreateUserRequest("test@example.com",
                                                        "testlogin",
                                                        "Test User",
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
      CreateUserRequest request = new CreateUserRequest("test@example.com",
                                                        "invalid login",
                                                        "Test",
                                                        LocalDate.of(1990, 1, 1));
      ResponseEntity<ValidationErrorResponse> response = restTemplate.postForEntity("/users",
                                                                                    request,
                                                                                    ValidationErrorResponse.class);
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Should fail to create user with invalid email")
    void shouldFailToCreateUserWithInvalidEmail() {
      CreateUserRequest request = new CreateUserRequest("not-an-email", "validlogin", "Test", LocalDate.of(1990, 1, 1));
      ResponseEntity<ValidationErrorResponse> response = restTemplate.postForEntity("/users",
                                                                                    request,
                                                                                    ValidationErrorResponse.class);
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Should fail to create user with future birthday")
    void shouldFailToCreateUserWithFutureBirthday() {
      CreateUserRequest request = new CreateUserRequest("test@example.com",
                                                        "validlogin",
                                                        "Test",
                                                        LocalDate.now()
                                                                 .plusDays(1));
      ResponseEntity<ValidationErrorResponse> response = restTemplate.postForEntity("/users",
                                                                                    request,
                                                                                    ValidationErrorResponse.class);
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Should update an existing user")
    void shouldUpdateUser() {
      UserResponse createdUser = createUser(new CreateUserRequest("original@test.com",
                                                                  "original",
                                                                  "Original",
                                                                  LocalDate.of(1990, 1, 1)));
      UpdateUserRequest updateRequest = new UpdateUserRequest(createdUser.id(),
                                                              "updated@test.com",
                                                              "updated",
                                                              "Updated",
                                                              LocalDate.of(1991, 2, 2));

      ResponseEntity<UserResponse> response = restTemplate.exchange("/users",
                                                                    HttpMethod.PUT,
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
      UpdateUserRequest updateRequest = new UpdateUserRequest(9999L,
                                                              "a@a.com",
                                                              "login",
                                                              "name",
                                                              LocalDate.of(1990, 1, 1));
      ResponseEntity<ErrorResponse> response = restTemplate.exchange("/users",
                                                                     HttpMethod.PUT,
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
    @DisplayName("Should add a friend successfully")
    void shouldAddFriend() {
      UserResponse user1 = createUser(new CreateUserRequest("user1@test.com", "user1", "U1", LocalDate.of(1991, 1, 1)));
      UserResponse user2 = createUser(new CreateUserRequest("user2@test.com", "user2", "U2", LocalDate.of(1992, 2, 2)));

      ResponseEntity<Void> response = restTemplate.exchange("/users/{id}/friends/{friendId}",
                                                            HttpMethod.PUT,
                                                            null,
                                                            Void.class,
                                                            user1.id(),
                                                            user2.id());
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

      ResponseEntity<UserResponse[]> user1Friends = restTemplate.getForEntity("/users/{id}/friends",
                                                                              UserResponse[].class,
                                                                              user1.id());
      assertThat(user1Friends.getBody()).hasSize(1);
      assertThat(user1Friends.getBody()[0].id()).isEqualTo(user2.id());
    }

    @Test
    @DisplayName("Should remove a friend successfully")
    void shouldRemoveFriend() {
      UserResponse user1 = createUser(new CreateUserRequest("user1@test.com",
                                                            "user1",
                                                            "User One",
                                                            LocalDate.of(1991, 1, 1)));
      UserResponse user2 = createUser(new CreateUserRequest("user2@test.com",
                                                            "user2",
                                                            "User Two",
                                                            LocalDate.of(1992, 2, 2)));
      restTemplate.put("/users/{id}/friends/{friendId}", null, user1.id(), user2.id());

      ResponseEntity<Void> response = restTemplate.exchange("/users/{id}/friends/{friendId}",
                                                            HttpMethod.DELETE,
                                                            null,
                                                            Void.class,
                                                            user1.id(),
                                                            user2.id());
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);


      ResponseEntity<UserResponse[]> friends = restTemplate.getForEntity("/users/{id}/friends",
                                                                         UserResponse[].class,
                                                                         user1.id());
      assertThat(friends.getBody()).isEmpty();
    }

    @Test
    @DisplayName("Should get common friends")
    void shouldGetCommonFriends() {
      UserResponse user1 = createUser(new CreateUserRequest("user1@test.com", "user1", "U1", LocalDate.of(1991, 1, 1)));
      UserResponse user2 = createUser(new CreateUserRequest("user2@test.com", "user2", "U2", LocalDate.of(1992, 2, 2)));
      UserResponse commonFriend = createUser(new CreateUserRequest("common@test.com",
                                                                   "common",
                                                                   "CF",
                                                                   LocalDate.of(1993, 3, 3)));

      restTemplate.put("/users/{id}/friends/{friendId}", null, user1.id(), commonFriend.id());
      restTemplate.put("/users/{id}/friends/{friendId}", null, user2.id(), commonFriend.id());

      ResponseEntity<UserResponse[]> response = restTemplate.getForEntity("/users/{id}/friends/common/{otherId}",
                                                                          UserResponse[].class,
                                                                          user1.id(),
                                                                          user2.id());

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).hasSize(1);
      assertThat(response.getBody()[0].id()).isEqualTo(commonFriend.id());
    }
  }

  @Nested
  @DisplayName("Director API Tests")
  class DirectorTests {
    @Test
    @DisplayName("Should create, get, update, and delete a director")
    void directorLifecycleTest() {
      // Create
      CreateDirectorRequest createRequest = new CreateDirectorRequest("Quentin Tarantino");
      ResponseEntity<DirectorResponse> createResponse = restTemplate.postForEntity("/directors",
                                                                                   createRequest,
                                                                                   DirectorResponse.class);
      assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
      DirectorResponse createdDirector = createResponse.getBody();
      assertThat(createdDirector).isNotNull();
      assertThat(createdDirector.id()).isPositive();
      assertThat(createdDirector.name()).isEqualTo("Quentin Tarantino");
      long directorId = createdDirector.id();

      // Get by ID
      ResponseEntity<DirectorResponse> getResponse = restTemplate.getForEntity("/directors/{id}",
                                                                               DirectorResponse.class,
                                                                               directorId);
      assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(getResponse.getBody()).isEqualTo(createdDirector);

      // Get All
      ResponseEntity<DirectorResponse[]> getAllResponse = restTemplate.getForEntity("/directors",
                                                                                    DirectorResponse[].class);
      assertThat(getAllResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(getAllResponse.getBody()).contains(createdDirector);

      // Update
      UpdateDirectorRequest updateRequest = new UpdateDirectorRequest(directorId, "Quentin Jerome Tarantino");
      ResponseEntity<DirectorResponse> updateResponse = restTemplate.exchange("/directors",
                                                                              HttpMethod.PUT,
                                                                              new HttpEntity<>(updateRequest),
                                                                              DirectorResponse.class);
      assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(updateResponse.getBody()
                               .name()).isEqualTo("Quentin Jerome Tarantino");

      // Delete
      restTemplate.delete("/directors/{id}", directorId);
      ResponseEntity<ErrorResponse> getAfterDeleteResponse = restTemplate.getForEntity("/directors/{id}",
                                                                                       ErrorResponse.class,
                                                                                       directorId);
      assertThat(getAfterDeleteResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
  }

  @Nested
  @DisplayName("Film API Tests")
  class FilmTests {

    @Test
    @DisplayName("Should create a film with directors successfully")
    void shouldCreateFilmWithDirectors() {
      DirectorResponse director = createDirector(new CreateDirectorRequest("Christopher Nolan"));
      Set<DirectorIdDto> directors = Set.of(new DirectorIdDto(director.id()));
      CreateFilmRequest request = new CreateFilmRequest("Inception",
                                                        "Mind-bending thriller",
                                                        LocalDate.of(2010, 7, 16),
                                                        148L,
                                                        Set.of(new Genre(4L, null)),
                                                        new Mpa(3L, null),
                                                        directors);

      FilmResponse body = createFilm(request);

      assertThat(body).isNotNull();
      assertThat(body.id()).isPositive();
      assertThat(body.name()).isEqualTo(request.name());
      assertThat(body.directors()).hasSize(1);
      assertThat(body.directors()
                     .iterator()
                     .next()
                     .id()).isEqualTo(director.id());
      assertThat(body.genres()).hasSize(1);
    }

    @Test
    @DisplayName("Should update film and change its directors")
    void shouldUpdateFilmAndDirectors() {
      DirectorResponse dir1 = createDirector(new CreateDirectorRequest("Director One"));
      DirectorResponse dir2 = createDirector(new CreateDirectorRequest("Director Two"));

      CreateFilmRequest createRequest = new CreateFilmRequest("Initial Film",
                                                              "Desc",
                                                              LocalDate.of(2020, 1, 1),
                                                              120L,
                                                              null,
                                                              new Mpa(1L, "G"),
                                                              Set.of(new DirectorIdDto(dir1.id())));
      FilmResponse createdFilm = createFilm(createRequest);
      assertThat(createdFilm.directors()).extracting(DirectorResponse::id)
                                         .containsExactly(dir1.id());

      UpdateFilmRequest updateRequest = new UpdateFilmRequest(createdFilm.id(),
                                                              "Updated Film",
                                                              "New Desc",
                                                              LocalDate.of(2021, 1, 1),
                                                              130L,
                                                              null,
                                                              new Mpa(2L, "PG"),
                                                              Set.of(new DirectorIdDto(dir2.id())));

      ResponseEntity<FilmResponse> response = restTemplate.exchange("/films",
                                                                    HttpMethod.PUT,
                                                                    new HttpEntity<>(updateRequest),
                                                                    FilmResponse.class);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      FilmResponse updatedFilm = response.getBody();
      assertThat(updatedFilm).isNotNull();
      assertThat(updatedFilm.name()).isEqualTo("Updated Film");
      assertThat(updatedFilm.directors()).extracting(DirectorResponse::id)
                                         .containsExactly(dir2.id());
    }

    @Test
    @DisplayName("Should fail to create film with blank name")
    void shouldFailWithBlankName() {
      CreateFilmRequest request = new CreateFilmRequest(" ",
                                                        "Desc",
                                                        LocalDate.of(2020, 1, 1),
                                                        120L,
                                                        null,
                                                        new Mpa(1L, "G"),
                                                        null);
      ResponseEntity<ValidationErrorResponse> response = restTemplate.postForEntity("/films",
                                                                                    request,
                                                                                    ValidationErrorResponse.class);
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Should fail to create film with too long description")
    void shouldFailWithLongDescription() {
      String longDesc = "a".repeat(201);
      CreateFilmRequest request = new CreateFilmRequest("Film",
                                                        longDesc,
                                                        LocalDate.of(2020, 1, 1),
                                                        120L,
                                                        null,
                                                        new Mpa(1L, "G"),
                                                        null);
      ResponseEntity<ErrorResponse> response = restTemplate.postForEntity("/films", request, ErrorResponse.class);
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Should fail to create film with invalid release date")
    void shouldFailWithInvalidDate() {
      CreateFilmRequest request = new CreateFilmRequest("Film",
                                                        "Desc",
                                                        LocalDate.of(1890, 1, 1),
                                                        120L,
                                                        null,
                                                        new Mpa(1L, "G"),
                                                        null);
      ResponseEntity<ValidationErrorResponse> response = restTemplate.postForEntity("/films",
                                                                                    request,
                                                                                    ValidationErrorResponse.class);
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Should fail to create film with non-existent MPA")
    void shouldFailWithInvalidMpa() {
      CreateFilmRequest request = new CreateFilmRequest("Film",
                                                        "Desc",
                                                        LocalDate.of(2000, 1, 1),
                                                        120L,
                                                        null,
                                                        new Mpa(9999L, "G"),
                                                        null);
      ResponseEntity<ErrorResponse> response = restTemplate.postForEntity("/films", request, ErrorResponse.class);
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Should fail to create film with non-existent Genre")
    void shouldFailWithInvalidGenre() {
      Set<Genre> genres = Set.of(new Genre(9999L, "Fake"));
      CreateFilmRequest request = new CreateFilmRequest("Film",
                                                        "Desc",
                                                        LocalDate.of(2000, 1, 1),
                                                        120L,
                                                        genres,
                                                        new Mpa(1L, "G"),
                                                        null);
      ResponseEntity<ErrorResponse> response = restTemplate.postForEntity("/films", request, ErrorResponse.class);
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Should get popular films")
    void shouldGetPopularFilms() {
      UserResponse u1 = createUser(new CreateUserRequest("u1@a.com", "u1", "u1", LocalDate.of(1990, 1, 1)));
      UserResponse u2 = createUser(new CreateUserRequest("u2@a.com", "u2", "u2", LocalDate.of(1990, 1, 1)));
      UserResponse u3 = createUser(new CreateUserRequest("u3@a.com", "u3", "u3", LocalDate.of(1990, 1, 1)));
      FilmResponse f1 = createFilm(new CreateFilmRequest("Film 1",
                                                         "d",
                                                         LocalDate.of(2001, 1, 1),
                                                         100L,
                                                         null,
                                                         new Mpa(1L, "G"),
                                                         null));
      FilmResponse f2 = createFilm(new CreateFilmRequest("Film 2",
                                                         "d",
                                                         LocalDate.of(2002, 1, 1),
                                                         100L,
                                                         null,
                                                         new Mpa(1L, "G"),
                                                         null));
      FilmResponse f3 = createFilm(new CreateFilmRequest("Film 3",
                                                         "d",
                                                         LocalDate.of(2003, 1, 1),
                                                         100L,
                                                         null,
                                                         new Mpa(1L, "G"),
                                                         null));

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

    @Test
    @DisplayName("Should get director's films sorted by year")
    void shouldGetDirectorFilmsSortedByYear() {
      DirectorResponse director = createDirector(new CreateDirectorRequest("Test Director"));
      Set<DirectorIdDto> directorSet = Set.of(new DirectorIdDto(director.id()));
      FilmResponse film2000 = createFilm(new CreateFilmRequest("Film 2000",
                                                               "d",
                                                               LocalDate.of(2000, 1, 1),
                                                               100L,
                                                               null,
                                                               new Mpa(1L, "G"),
                                                               directorSet));
      FilmResponse film1990 = createFilm(new CreateFilmRequest("Film 1990",
                                                               "d",
                                                               LocalDate.of(1990, 1, 1),
                                                               100L,
                                                               null,
                                                               new Mpa(1L, "G"),
                                                               directorSet));

      ResponseEntity<FilmResponse[]> response = restTemplate.getForEntity("/films/director/{id}?sortBy=year",
                                                                          FilmResponse[].class,
                                                                          director.id());

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isNotNull();
      assertThat(Arrays.stream(response.getBody())
                       .map(FilmResponse::id)
                       .collect(Collectors.toList()))
          .containsExactly(film1990.id(), film2000.id());
    }

    @Test
    @DisplayName("Should get director's films sorted by likes")
    void shouldGetDirectorFilmsSortedByLikes() {
      DirectorResponse director = createDirector(new CreateDirectorRequest("Test Director"));
      Set<DirectorIdDto> directorSet = Set.of(new DirectorIdDto(director.id()));
      UserResponse user1 = createUser(new CreateUserRequest("u1@a.com", "u1", "u1", LocalDate.of(1990, 1, 1)));
      UserResponse user2 = createUser(new CreateUserRequest("u2@a.com", "u2", "u2", LocalDate.of(1990, 1, 1)));
      FilmResponse film1 = createFilm(new CreateFilmRequest("Film One",
                                                            "d",
                                                            LocalDate.of(2000, 1, 1),
                                                            100L,
                                                            null,
                                                            new Mpa(1L, "G"),
                                                            directorSet));
      FilmResponse film2 = createFilm(new CreateFilmRequest("Film Two",
                                                            "d",
                                                            LocalDate.of(2001, 1, 1),
                                                            100L,
                                                            null,
                                                            new Mpa(1L, "G"),
                                                            directorSet));

      restTemplate.put("/films/{id}/like/{userId}", null, film2.id(), user1.id());
      restTemplate.put("/films/{id}/like/{userId}", null, film2.id(), user2.id());
      restTemplate.put("/films/{id}/like/{userId}", null, film1.id(), user1.id());

      ResponseEntity<FilmResponse[]> response = restTemplate.getForEntity("/films/director/{id}?sortBy=likes",
                                                                          FilmResponse[].class,
                                                                          director.id());

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isNotNull();
      assertThat(Arrays.stream(response.getBody())
                       .map(FilmResponse::id)
                       .collect(Collectors.toList()))
          .containsExactly(film2.id(), film1.id());
    }
  }

  @Nested
  @DisplayName("MPA API Tests")
  class MpaTests {

    @Test
    @DisplayName("Should get all MPA ratings in order")
    void shouldGetAllMpaRatings() {
      ResponseEntity<MpaResponse[]> response = restTemplate.getForEntity("/mpa", MpaResponse[].class);
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).hasSize(5);
      assertThat(response.getBody()).isSortedAccordingTo(Comparator.comparing(MpaResponse::id));
      assertThat(response.getBody()).extracting("name")
                                    .containsExactly("G", "PG", "PG-13", "R", "NC-17");
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
    @DisplayName("Should get all Genres in order")
    void shouldGetAllGenres() {
      ResponseEntity<GenreResponse[]> response = restTemplate.getForEntity("/genres", GenreResponse[].class);
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).hasSize(6);
      assertThat(response.getBody()).isSortedAccordingTo(Comparator.comparing(GenreResponse::id));
      assertThat(response.getBody()).extracting("name")
                                    .containsExactly("Комедия",
                                                     "Драма",
                                                     "Мультфильм",
                                                     "Триллер",
                                                     "Документальный",
                                                     "Боевик");
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
    @DisplayName("Film duration should be returned in minutes")
    void filmDurationShouldBeInMinutes() {
      long durationInMinutes = 125;
      CreateFilmRequest request = new CreateFilmRequest("Test Film",
                                                        "Description",
                                                        LocalDate.now()
                                                                 .minusYears(1),
                                                        durationInMinutes,
                                                        null,
                                                        new Mpa(1L, "G"),
                                                        null);

      ResponseEntity<FilmResponse> response = restTemplate.postForEntity("/films", request, FilmResponse.class);
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()
                         .duration()).isEqualTo(durationInMinutes);
    }
  }
}