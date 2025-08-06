package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.client.RestClientException;
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
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test", "db"})
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
            CreateUserRequest request = new CreateUserRequest("test@example.com", "testlogin", "Test User", LocalDate.of(1990, 5, 15));
            ResponseEntity<UserResponse> resp = restTemplate.postForEntity("/users", request, UserResponse.class);
            assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            UserResponse body = resp.getBody();
            assertThat(body).isNotNull();
            assertThat(body.id()).isNotNull();
            assertThat(body.email()).isEqualTo(request.email());
            assertThat(body.login()).isEqualTo(request.login());
            assertThat(body.name()).isEqualTo(request.name());
            assertThat(body.birthday()).isEqualTo(request.birthday());
        }

        @Test
        @DisplayName("Should fail to create user with invalid login")
        void invalidLogin() {
            CreateUserRequest req = new CreateUserRequest("e@e.com", "bad login", "N", LocalDate.of(1990, 1, 1));
            ResponseEntity<ValidationErrorResponse> resp = restTemplate.postForEntity("/users", req, ValidationErrorResponse.class);
            assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        @Test
        @DisplayName("Should fail to create user with invalid email")
        void invalidEmail() {
            CreateUserRequest req = new CreateUserRequest("not-email", "good", "N", LocalDate.of(1990, 1, 1));
            ResponseEntity<ValidationErrorResponse> resp = restTemplate.postForEntity("/users", req, ValidationErrorResponse.class);
            assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        @Test
        @DisplayName("Should fail to create user with future birthday")
        void futureBirthday() {
            CreateUserRequest req = new CreateUserRequest("e@e.com", "good", "N", LocalDate.now().plusDays(1));
            ResponseEntity<ValidationErrorResponse> resp = restTemplate.postForEntity("/users", req, ValidationErrorResponse.class);
            assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        @Test
        @DisplayName("Should update existing user")
        void updateUser() {
            UserResponse u = createUser(new CreateUserRequest("o@o.com", "orig", "Orig", LocalDate.of(1990, 1, 1)));
            UpdateUserRequest up = new UpdateUserRequest(u.id(), "n@n.com", "new", "New", LocalDate.of(1991, 2, 2));
            ResponseEntity<UserResponse> resp = restTemplate.exchange("/users", HttpMethod.PUT, new HttpEntity<>(up), UserResponse.class);
            assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
            UserResponse b = resp.getBody();
            assertThat(b).isNotNull();
            assertThat(b.email()).isEqualTo(up.email());
            assertThat(b.login()).isEqualTo(up.login());
        }

        @Test
        @DisplayName("404 on update unknown user")
        void updateNotFound() {
            UpdateUserRequest up = new UpdateUserRequest(9999L, "a@a.com", "l", "n", LocalDate.of(1990, 1, 1));
            ResponseEntity<ErrorResponse> resp = restTemplate.exchange("/users", HttpMethod.PUT, new HttpEntity<>(up), ErrorResponse.class);
            assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }

        @Test
        @DisplayName("Should get all users")
        void getAllUsers() {
            createUser(new CreateUserRequest("u1@u.com", "u1", "U1", LocalDate.of(1991, 1, 1)));
            createUser(new CreateUserRequest("u2@u.com", "u2", "U2", LocalDate.of(1992, 2, 2)));
            ResponseEntity<UserResponse[]> resp = restTemplate.getForEntity("/users", UserResponse[].class);
            assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(resp.getBody()).hasSize(2);
        }
    }

    @Nested
    @DisplayName("Friendship API Tests")
    class FriendshipTests {
        @Test
        @DisplayName("Add and pending friend")
        void addFriend() {
            UserResponse u1 = createUser(new CreateUserRequest("u1@u.com", "u1", "U1", LocalDate.of(1991, 1, 1)));
            UserResponse u2 = createUser(new CreateUserRequest("u2@u.com", "u2", "U2", LocalDate.of(1992, 2, 2)));
            restTemplate.put("/users/{id}/friends/{f}", null, u1.id(), u2.id());
            ResponseEntity<UserResponse[]> r1 = restTemplate.getForEntity("/users/{id}/friends", UserResponse[].class, u1.id());
            assertThat(r1.getBody()).hasSize(1);
            ResponseEntity<UserResponse[]> r2 = restTemplate.getForEntity("/users/{id}/friends", UserResponse[].class, u2.id());
            assertThat(r2.getBody()).isEmpty();
        }

        @Test
        @DisplayName("Confirm friendship")
        void confirmFriendship() {
            UserResponse u1 = createUser(new CreateUserRequest("u1@u.com", "u1", "U1", LocalDate.of(1991, 1, 1)));
            UserResponse u2 = createUser(new CreateUserRequest("u2@u.com", "u2", "U2", LocalDate.of(1992, 2, 2)));
            restTemplate.put("/users/{id}/friends/{f}", null, u1.id(), u2.id());
            restTemplate.put("/users/{id}/friends/{f}", null, u2.id(), u1.id());
            ResponseEntity<UserResponse[]> r1 = restTemplate.getForEntity("/users/{id}/friends", UserResponse[].class, u1.id());
            ResponseEntity<UserResponse[]> r2 = restTemplate.getForEntity("/users/{id}/friends", UserResponse[].class, u2.id());
            assertThat(r1.getBody()).hasSize(1);
            assertThat(r2.getBody()).hasSize(1);
        }

        @Test
        @DisplayName("Remove friend")
        void removeFriend() {
            UserResponse u1 = createUser(new CreateUserRequest("u1@u.com", "u1", "U1", LocalDate.of(1991, 1, 1)));
            UserResponse u2 = createUser(new CreateUserRequest("u2@u.com", "u2", "U2", LocalDate.of(1992, 2, 2)));
            restTemplate.put("/users/{id}/friends/{f}", null, u1.id(), u2.id());
            restTemplate.delete("/users/{id}/friends/{f}", u1.id(), u2.id());
            ResponseEntity<UserResponse[]> r = restTemplate.getForEntity("/users/{id}/friends", UserResponse[].class, u1.id());
            assertThat(r.getBody()).isEmpty();
        }

        @Test
        @DisplayName("Get common friends")
        void commonFriends() {
            UserResponse u1 = createUser(new CreateUserRequest("u1@u.com", "u1", "U1", LocalDate.of(1991, 1, 1)));
            UserResponse u2 = createUser(new CreateUserRequest("u2@u.com", "u2", "U2", LocalDate.of(1992, 2, 2)));
            UserResponse cf = createUser(new CreateUserRequest("cf@c.com", "cf", "CF", LocalDate.of(1993, 3, 3)));
            restTemplate.put("/users/{id}/friends/{f}", null, u1.id(), cf.id());
            restTemplate.put("/users/{id}/friends/{f}", null, cf.id(), u1.id());
            restTemplate.put("/users/{id}/friends/{f}", null, u2.id(), cf.id());
            restTemplate.put("/users/{id}/friends/{f}", null, cf.id(), u2.id());
            ResponseEntity<UserResponse[]> resp = restTemplate.getForEntity("/users/{id}/friends/common/{o}", UserResponse[].class, u1.id(), u2.id());
            assertThat(resp.getBody()).hasSize(1).extracting(UserResponse::id).contains(cf.id());
        }
    }

    @Nested
    @DisplayName("Film API Tests")
    class FilmTests {
        @Test
        @DisplayName("Create film")
        void createFilmTest() {
            CreateFilmRequest req = new CreateFilmRequest("Inception", "Thriller", LocalDate.of(2010, 7, 16), 148, Set.of(new Genre(1L, "Комедия")), new Mpa(1L, "G"));
            ResponseEntity<FilmResponse> resp = restTemplate.postForEntity("/films", req, FilmResponse.class);
            assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(resp.getBody()).extracting(FilmResponse::name).isEqualTo("Inception");
        }

        @Test
        @DisplayName("Blank name fails")
        void blankName() {
            CreateFilmRequest req = new CreateFilmRequest("", "Desc", LocalDate.of(2010, 1, 1), 120, Set.of(new Genre(1L, "Комедия")), new Mpa(1L, "G"));
            ResponseEntity<ValidationErrorResponse> resp = restTemplate.postForEntity("/films", req, ValidationErrorResponse.class);
            assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        @Test
        @DisplayName("Long description fails")
        void longDesc() {
            String d = "a".repeat(201);
            CreateFilmRequest req = new CreateFilmRequest("F", d, LocalDate.of(2010, 1, 1), 120, Set.of(new Genre(1L, "Комедия")), new Mpa(1L, "G"));
            ResponseEntity<ErrorResponse> resp = restTemplate.postForEntity("/films", req, ErrorResponse.class);
            assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        @Test
        @DisplayName("Invalid release date fails")
        void invalidDate() {
            CreateFilmRequest req = new CreateFilmRequest("F", "D", LocalDate.of(1890, 1, 1), 120, Set.of(new Genre(1L, "Комедия")), new Mpa(1L, "G"));
            ResponseEntity<ValidationErrorResponse> resp = restTemplate.postForEntity("/films", req, ValidationErrorResponse.class);
            assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        @Test
        @DisplayName("Negative duration fails")
        void negativeDuration() {
            CreateFilmRequest req = new CreateFilmRequest("F", "D", LocalDate.of(2000, 1, 1), -100, Set.of(new Genre(1L, "Комедия")), new Mpa(1L, "G"));
            ResponseEntity<ValidationErrorResponse> resp = restTemplate.postForEntity("/films", req, ValidationErrorResponse.class);
            assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        @Test
        @DisplayName("Get popular films")
        void getPopular() {
            UserResponse u1 = createUser(new CreateUserRequest("u1@u.com", "u1", "U1", LocalDate.of(1990, 1, 1)));
            UserResponse u2 = createUser(new CreateUserRequest("u2@u.com", "u2", "U2", LocalDate.of(1990, 1, 1)));
            UserResponse u3 = createUser(new CreateUserRequest("u3@u.com", "u3", "U3", LocalDate.of(1990, 1, 1)));
            FilmResponse f1 = createFilm(new CreateFilmRequest("F1", "d", LocalDate.of(2001, 1, 1), 100, Set.of(new Genre(1L, "Комедия")), new Mpa(1L, "G")));
            FilmResponse f2 = createFilm(new CreateFilmRequest("F2", "d", LocalDate.of(2002, 1, 1), 100, Set.of(new Genre(1L, "Комедия")), new Mpa(1L, "G")));
            FilmResponse f3 = createFilm(new CreateFilmRequest("F3", "d", LocalDate.of(2003, 1, 1), 100, Set.of(new Genre(1L, "Комедия")), new Mpa(1L, "G")));
            restTemplate.put("/films/{id}/like/{u}", null, f2.id(), u1.id());
            restTemplate.put("/films/{id}/like/{u}", null, f2.id(), u2.id());
            restTemplate.put("/films/{id}/like/{u}", null, f2.id(), u3.id());
            restTemplate.put("/films/{id}/like/{u}", null, f3.id(), u1.id());
            restTemplate.put("/films/{id}/like/{u}", null, f3.id(), u2.id());
            restTemplate.put("/films/{id}/like/{u}", null, f1.id(), u1.id());
            ResponseEntity<FilmResponse[]> resp = restTemplate.getForEntity("/films/popular?count=3", FilmResponse[].class);
            List<Long> ids = Arrays.stream(Objects.requireNonNull(resp.getBody())).map(FilmResponse::id).toList();
            assertThat(ids).containsExactly(f2.id(), f3.id(), f1.id());
        }

        @Test
        @DisplayName("Get common films")
        void commonFilms() {
            UserResponse u1 = createUser(new CreateUserRequest("u1@u.com", "u1", "U1", LocalDate.of(1990, 1, 1)));
            UserResponse u2 = createUser(new CreateUserRequest("u2@u.com", "u2", "U2", LocalDate.of(1990, 2, 2)));
            FilmResponse f1 = createFilm(new CreateFilmRequest("F1", "d", LocalDate.of(2000, 1, 1), 100, Set.of(new Genre(1L, "Комедия")), new Mpa(1L, "G")));
            FilmResponse f2 = createFilm(new CreateFilmRequest("F2", "d", LocalDate.of(2001, 1, 1), 100, Set.of(new Genre(1L, "Комедия")), new Mpa(1L, "G")));
            restTemplate.put("/films/{id}/like/{u}", null, f1.id(), u1.id());
            restTemplate.put("/films/{id}/like/{u}", null, f2.id(), u1.id());
            restTemplate.put("/films/{id}/like/{u}", null, f2.id(), u2.id());
            ResponseEntity<FilmResponse[]> resp = restTemplate.getForEntity("/films/common?userId={u}&friendId={f}", FilmResponse[].class, u1.id(), u2.id());
            assertThat(resp.getBody()).hasSize(1).extracting(FilmResponse::id).containsExactly(f2.id());
        }
    }

    @Nested
    @DisplayName("Like API Tests")
    class LikeTests {
        @Test
        @DisplayName("Add like")
        void addLike() {
            UserResponse u = createUser(new CreateUserRequest("u@u.com", "u", "U", LocalDate.of(1990, 1, 1)));
            FilmResponse f = createFilm(new CreateFilmRequest("F", "d", LocalDate.of(2000, 1, 1), 120, Set.of(new Genre(1L, "Комедия")), new Mpa(1L, "G")));
            ResponseEntity<Void> resp = restTemplate.exchange("/films/{id}/like/{u}", HttpMethod.PUT, null, Void.class, f.id(), u.id());
            assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        @Test
        @DisplayName("Remove like")
        void removeLike() {
            UserResponse u = createUser(new CreateUserRequest("u@u.com", "u", "U", LocalDate.of(1990, 1, 1)));
            FilmResponse f = createFilm(new CreateFilmRequest("F", "d", LocalDate.of(2000, 1, 1), 120, Set.of(new Genre(1L, "Комедия")), new Mpa(1L, "G")));
            restTemplate.put("/films/{id}/like/{u}", null, f.id(), u.id());
            ResponseEntity<Void> resp = restTemplate.exchange("/films/{id}/like/{u}", HttpMethod.DELETE, null, Void.class, f.id(), u.id());
            assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
    }

    @Nested
    @DisplayName("MPA API Tests")
    class MpaTests {
        @Test
        @DisplayName("Get all MPA")
        void getAllMpa() {
            ResponseEntity<MpaResponse[]> resp = restTemplate.getForEntity("/mpa", MpaResponse[].class);
            assertThat(resp.getBody()).hasSize(5).extracting("name").contains("G", "PG", "PG-13", "R", "NC-17");
        }

        @Test
        @DisplayName("Get MPA by ID")
        void getMpaById() {
            ResponseEntity<MpaResponse> resp = restTemplate.getForEntity("/mpa/1", MpaResponse.class);
            assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(resp.getBody().name()).isEqualTo("G");
        }

        @Test
        @DisplayName("404 on unknown MPA")
        void mpaNotFound() {
            ResponseEntity<ErrorResponse> resp = restTemplate.getForEntity("/mpa/9999", ErrorResponse.class);
            assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("Genre API Tests")
    class GenreTests {
        @Test
        @DisplayName("Get all Genres")
        void getAllGenres() {
            ResponseEntity<GenreResponse[]> resp = restTemplate.getForEntity("/genres", GenreResponse[].class);
            assertThat(resp.getBody()).hasSize(6).extracting("name").contains("Комедия", "Драма", "Мультфильм", "Триллер", "Документальный", "Боевик");
        }

        @Test
        @DisplayName("Get Genre by ID")
        void getGenreById() {
            ResponseEntity<GenreResponse> resp = restTemplate.getForEntity("/genres/1", GenreResponse.class);
            assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(resp.getBody().name()).isEqualTo("Комедия");
        }

        @Test
        @DisplayName("404 on unknown Genre")
        void genreNotFound() {
            ResponseEntity<ErrorResponse> resp = restTemplate.getForEntity("/genres/9999", ErrorResponse.class);
            assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("Review API Tests")
    class ReviewTests {
        private UserResponse user;
        private FilmResponse film;

        @BeforeEach
        void setUp() {
            user = createUser(new CreateUserRequest("nu@u.com", "nu", "NU", LocalDate.of(1990, 1, 1)));
            film = createFilm(new CreateFilmRequest("F", "d", LocalDate.of(2000, 1, 1), 120, Set.of(new Genre(1L, "Комедия")), new Mpa(1L, "G")));
        }

        @Test
        @DisplayName("Create review")
        void createReviewTest() {
            ReviewResponse r = createReview(new CreateReviewRequest("good", true, user.id(), film.id()));
            assertThat(r.content()).isEqualTo("good");
            assertThat(r.useful()).isEqualTo(0);
        }

        @Test
        @DisplayName("Update review")
        void updateReviewTest() {
            long rid = createReview(new CreateReviewRequest("good", true, user.id(), film.id())).reviewId();
            UpdateReviewRequest up = new UpdateReviewRequest(rid, "new", false, user.id(), film.id());
            ResponseEntity<ReviewResponse> resp = restTemplate.exchange("/reviews", HttpMethod.PUT, new HttpEntity<>(up), ReviewResponse.class);
            assertThat(resp.getBody().content()).isEqualTo("new");
        }

        @Test
        @DisplayName("Get review by id and params")
        void getReviewParams() {
            long rid = createReview(new CreateReviewRequest("good", true, user.id(), film.id())).reviewId();
            assertThat(restTemplate.getForEntity("/reviews/{id}", ReviewResponse.class, rid).getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(restTemplate.getForEntity("/reviews?filmId={f}&count={c}", ReviewResponse[].class, film.id(), 2).getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(restTemplate.getForEntity("/reviews?count={c}", ReviewResponse[].class, 2).getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        @Test
        @DisplayName("Delete review")
        void deleteReviewTest() {
            long rid = createReview(new CreateReviewRequest("good", true, user.id(), film.id())).reviewId();
            ResponseEntity<Void> resp = restTemplate.exchange("/reviews/{id}", HttpMethod.DELETE, null, Void.class, rid);
            assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        @Test
        @DisplayName("Reaction flow")
        void reactionFlow() {
            ReviewResponse r = createReview(new CreateReviewRequest("good", true, user.id(), film.id()));
            int init = r.useful();
            restTemplate.put("/reviews/{id}/like/{u}", null, r.reviewId(), user.id());
            assertEquals(init + 1, restTemplate.getForEntity("/reviews/{id}", ReviewResponse.class, r.reviewId()).getBody().useful());
            assertThat(restTemplate.exchange("/reviews/{id}/like/{u}", HttpMethod.PUT, null, Void.class, r.reviewId(), user.id()).getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            restTemplate.delete("/reviews/{id}/like/{u}", r.reviewId(), user.id());
            assertEquals(init, restTemplate.getForEntity("/reviews/{id}", ReviewResponse.class, r.reviewId()).getBody().useful());
            restTemplate.put("/reviews/{id}/dislike/{u}", null, r.reviewId(), user.id());
            assertEquals(init - 1, restTemplate.getForEntity("/reviews/{id}", ReviewResponse.class, r.reviewId()).getBody().useful());
            restTemplate.delete("/reviews/{id}/dislike/{u}", r.reviewId(), user.id());
            assertEquals(init, restTemplate.getForEntity("/reviews/{id}", ReviewResponse.class, r.reviewId()).getBody().useful());
        }
    }

    @Nested
    @DisplayName("Event Feed API Tests")
    class EventFeedTests {
        private UserResponse user;

        @BeforeEach
        void setUp() {
            user = createUser(new CreateUserRequest("u1@u.com", "u1", "U1", LocalDate.of(1990, 1, 1)));
            UserResponse friend = createUser(new CreateUserRequest("u2@u.com", "u2", "U2", LocalDate.of(1990, 2, 2)));
            restTemplate.put("/users/{id}/friends/{f}", null, user.id(), friend.id());
            restTemplate.delete("/users/{id}/friends/{f}", user.id(), friend.id());
            FilmResponse film = createFilm(new CreateFilmRequest("F", "d", LocalDate.of(2000, 1, 1), 100, Set.of(new Genre(1L, "Комедия")), new Mpa(1L, "G")));
            ReviewResponse rev = createReview(new CreateReviewRequest("c", false, user.id(), film.id()));
            restTemplate.put("/reviews", new HttpEntity<>(new UpdateReviewRequest(rev.reviewId(), "u", true, user.id(), film.id())));
            restTemplate.delete("/reviews/{id}", rev.reviewId());
            restTemplate.put("/films/{id}/like/{u}", null, film.id(), user.id());
            restTemplate.delete("/films/{id}/like/{u}", film.id(), user.id());
        }

        @Test
        @DisplayName("Should return 7 events chronologically")
        void feedEvents() {
            ResponseEntity<EventResponse[]> resp = restTemplate.getForEntity("/users/{id}/feed", EventResponse[].class, user.id());
            assertThat(resp.getBody()).hasSize(7);
            assertThat(resp.getBody()[0].eventType()).isEqualTo(EventType.FRIEND);
            assertThat(resp.getBody()[0].operation()).isEqualTo(Operation.ADD);
            assertThat(resp.getBody()[6].eventType()).isEqualTo(EventType.LIKE);
            assertThat(resp.getBody()[6].operation()).isEqualTo(Operation.REMOVE);
        }
    }

    @Nested
    @DisplayName("Most Popular API Tests")
    class MostPopularTests {
        private UserResponse u1, u2, u3;
        private FilmResponse f1, f2, f3, f4, f5;

        @BeforeEach
        void prep() {
            u1 = createUser(new CreateUserRequest("u1@u.com", "u1", "U1", LocalDate.of(1990, 1, 1)));
            u2 = createUser(new CreateUserRequest("u2@u.com", "u2", "U2", LocalDate.of(1990, 1, 1)));
            u3 = createUser(new CreateUserRequest("u3@u.com", "u3", "U3", LocalDate.of(1990, 1, 1)));
            f1 = createFilm(new CreateFilmRequest("F1", "d", LocalDate.of(1990, 1, 1), 100, Set.of(new Genre(1L, "Комедия")), new Mpa(1L, "G")));
            f2 = createFilm(new CreateFilmRequest("F2", "d", LocalDate.of(1990, 1, 1), 100, Set.of(new Genre(1L, "Комедия")), new Mpa(1L, "G")));
            f3 = createFilm(new CreateFilmRequest("F3", "d", LocalDate.of(2000, 1, 1), 100, Set.of(new Genre(1L, "Комедия")), new Mpa(1L, "G")));
            f4 = createFilm(new CreateFilmRequest("F4", "d", LocalDate.of(2000, 1, 1), 100, Set.of(new Genre(2L, "Драма")), new Mpa(1L, "G")));
            f5 = createFilm(new CreateFilmRequest("F5", "d", LocalDate.of(2000, 1, 1), 100, Set.of(new Genre(2L, "Драма")), new Mpa(1L, "G")));
            like(f1, u1);
            like(f1, u2);
            like(f2, u1);
            like(f2, u3);
            like(f3, u1);
            like(f4, u1);
            like(f4, u3);
            like(f5, u1);
            like(f5, u2);
            like(f5, u3);
        }

        @Test
        @DisplayName("Top 2 for genre 1/year 1990")
        void topByGenreYear() {
            like(f1, u1);
            like(f1, u2);
            like(f2, u1);
            ResponseEntity<FilmResponse[]> resp = restTemplate.getForEntity("/films/popular?count=2&genreId=1&year=1990", FilmResponse[].class);
            List<Long> ids = Arrays.stream(Objects.requireNonNull(resp.getBody())).map(FilmResponse::id).toList();
            assertThat(ids).containsExactly(f1.id(), f2.id());
        }

        @Test
        @DisplayName("Filter by genre only")
        void byGenre() {
            ResponseEntity<FilmResponse[]> resp = restTemplate.getForEntity("/films/popular?genreId=1", FilmResponse[].class);
            assertThat(resp.getBody()).hasSize(3);
        }

        @Test
        @DisplayName("Filter by year only")
        void byYear() {
            ResponseEntity<FilmResponse[]> resp = restTemplate.getForEntity("/films/popular?year=2000", FilmResponse[].class);
            assertThat(resp.getBody()).hasSize(3);
        }

        @Test
        @DisplayName("Invalid count throws")
        void invalidCount() {
            Assertions.assertThrows(RestClientException.class, () -> restTemplate.getForEntity("/films/popular?count=-1", FilmResponse[].class));
        }

        @Test
        @DisplayName("Invalid year throws")
        void invalidYear() {
            int ny = LocalDate.now().getYear() + 1;
            Assertions.assertThrows(RestClientException.class, () -> restTemplate.getForEntity("/films/popular?year=" + ny, FilmResponse[].class));
            Assertions.assertThrows(RestClientException.class, () -> restTemplate.getForEntity("/films/popular?year=1850", FilmResponse[].class));
        }

        @Test
        @DisplayName("Unknown genre yields empty")
        void unknownGenre() {
            ResponseEntity<FilmResponse[]> resp = restTemplate.getForEntity("/films/popular?genreId=999&year=1990", FilmResponse[].class);
            assertThat(resp.getBody()).isEmpty();
        }

        private void like(FilmResponse f, UserResponse u) {
            restTemplate.put("/films/{id}/like/{u}", null, f.id(), u.id());
        }
    }

    @Nested
    @DisplayName("Recommendation API Tests")
    class RecommendationTests {
        @Test
        @DisplayName("Recommend based on similar user")
        void recommend() {
            UserResponse a = createUser(new CreateUserRequest("a@a.com", "a", "A", LocalDate.of(1990, 1, 1)));
            UserResponse b = createUser(new CreateUserRequest("b@b.com", "b", "B", LocalDate.of(1991, 1, 1)));
            FilmResponse f1 = createFilm(new CreateFilmRequest("F1", "d", LocalDate.of(2010, 1, 1), 100, Set.of(new Genre(1L, "Комедия")), new Mpa(1L, "G")));
            FilmResponse f2 = createFilm(new CreateFilmRequest("F2", "d", LocalDate.of(2011, 1, 1), 100, Set.of(new Genre(1L, "Комедия")), new Mpa(1L, "G")));
            FilmResponse f3 = createFilm(new CreateFilmRequest("F3", "d", LocalDate.of(2012, 1, 1), 100, Set.of(new Genre(1L, "Комедия")), new Mpa(1L, "G")));
            restTemplate.put("/films/{id}/like/{u}", null, f1.id(), a.id());
            restTemplate.put("/films/{id}/like/{u}", null, f1.id(), b.id());
            restTemplate.put("/films/{id}/like/{u}", null, f2.id(), b.id());
            restTemplate.put("/films/{id}/like/{u}", null, f3.id(), b.id());
            ResponseEntity<FilmResponse[]> resp = restTemplate.getForEntity("/users/{id}/recommendations", FilmResponse[].class, a.id());
            assertThat(resp.getBody()).hasSize(2).extracting(FilmResponse::id).containsExactlyInAnyOrder(f2.id(), f3.id());
        }

        @Test
        @DisplayName("Apply filters in recommendations")
        void recommendWithFilters() {
            UserResponse a = createUser(new CreateUserRequest("a@a.com", "a", "A", LocalDate.of(1990, 1, 1)));
            UserResponse b = createUser(new CreateUserRequest("b@b.com", "b", "B", LocalDate.of(1991, 1, 1)));
            FilmResponse f1 = createFilm(new CreateFilmRequest("F1", "d", LocalDate.of(2020, 1, 1), 100, Set.of(new Genre(1L, "Комедия")), new Mpa(1L, "G")));
            FilmResponse f2 = createFilm(new CreateFilmRequest("F2", "d", LocalDate.of(2020, 1, 1), 100, Set.of(new Genre(2L, "Драма")), new Mpa(1L, "G")));
            FilmResponse f3 = createFilm(new CreateFilmRequest("F3", "d", LocalDate.of(2021, 1, 1), 100, Set.of(new Genre(1L, "Комедия")), new Mpa(1L, "G")));
            restTemplate.put("/films/{id}/like/{u}", null, f2.id(), a.id());
            restTemplate.put("/films/{id}/like/{u}", null, f1.id(), b.id());
            restTemplate.put("/films/{id}/like/{u}", null, f2.id(), b.id());
            restTemplate.put("/films/{id}/like/{u}", null, f3.id(), b.id());
            String url = "/users/{id}/recommendations?genreId=1&year=2020&limit=1";
            ResponseEntity<FilmResponse[]> resp = restTemplate.getForEntity(url, FilmResponse[].class, a.id());
            assertThat(resp.getBody()).hasSize(1).extracting(FilmResponse::id).containsExactly(f1.id());
        }

        @Test
        @DisplayName("No likes returns empty")
        void noLikes() {
            UserResponse u = createUser(new CreateUserRequest("n@n.com", "n", "N", LocalDate.of(1990, 1, 1)));
            ResponseEntity<FilmResponse[]> resp = restTemplate.getForEntity("/users/{id}/recommendations", FilmResponse[].class, u.id());
            assertThat(resp.getBody()).isEmpty();
        }

        @Test
        @DisplayName("No similar user returns empty")
        void noSimilar() {
            UserResponse u1 = createUser(new CreateUserRequest("u1@u.com", "u1", "U1", LocalDate.of(1990, 1, 1)));
            UserResponse u2 = createUser(new CreateUserRequest("u2@u.com", "u2", "U2", LocalDate.of(1991, 1, 1)));
            FilmResponse f1 = createFilm(new CreateFilmRequest("F1", "d", LocalDate.of(2020, 1, 1), 100, Set.of(new Genre(1L, "Комедия")), new Mpa(1L, "G")));
            FilmResponse f2 = createFilm(new CreateFilmRequest("F2", "d", LocalDate.of(2020, 1, 1), 100, Set.of(new Genre(1L, "Комедия")), new Mpa(1L, "G")));
            restTemplate.put("/films/{id}/like/{u}", null, f1.id(), u1.id());
            restTemplate.put("/films/{id}/like/{u}", null, f2.id(), u2.id());
            ResponseEntity<FilmResponse[]> resp = restTemplate.getForEntity("/users/{id}/recommendations", FilmResponse[].class, u1.id());
            assertThat(resp.getBody()).isEmpty();
        }
    }
}
