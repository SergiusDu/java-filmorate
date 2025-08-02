package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.yandex.practicum.filmorate.common.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.common.exception.ValidationException;
import ru.yandex.practicum.filmorate.films.application.port.in.FilmUseCase;
import ru.yandex.practicum.filmorate.users.application.port.in.UserUseCase;
import ru.yandex.practicum.filmorate.films.domain.model.Film;
import ru.yandex.practicum.filmorate.films.domain.model.value.Genre;
import ru.yandex.practicum.filmorate.films.domain.model.value.Mpa;
import ru.yandex.practicum.filmorate.films.domain.port.CreateFilmCommand;
import ru.yandex.practicum.filmorate.films.domain.port.UpdateFilmCommand;
import ru.yandex.practicum.filmorate.likes.application.port.in.LikeUseCase;
import ru.yandex.practicum.filmorate.service.FilmCompositionService;
import ru.yandex.practicum.filmorate.users.domain.model.User;
import ru.yandex.practicum.filmorate.users.domain.model.value.Email;
import ru.yandex.practicum.filmorate.users.domain.model.value.Login;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class FilmCompositionServiceTest {

    @Mock
    private FilmUseCase filmUseCase;
    @Mock
    private LikeUseCase likeService;
    @Mock
    private UserUseCase userUseCase;
    @InjectMocks
    private FilmCompositionService filmCompositionService;

    private Film film;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        film = new Film(1L, "Test Film", "Description", LocalDate.of(2020, 1, 1), Duration.ofMinutes(90), Set.of(new Genre(1L, "Drama")), new Mpa(1L, "G"));
    }

    @Test
    void shouldReturnAllFilms() {
        when(filmUseCase.getAllFilms()).thenReturn(List.of(film));
        assertThat(filmCompositionService.getAllFilms()).containsExactly(film);
    }

    @Test
    void shouldCreateFilm() {
        var cmd = new CreateFilmCommand(film.name(), film.description(), film.releaseDate(), film.duration().toMinutes(), film.genres(), film.mpa());
        when(filmUseCase.addFilm(cmd)).thenReturn(film);
        assertThat(filmCompositionService.createFilm(cmd)).isEqualTo(film);
    }

    @Test
    void shouldUpdateFilm() {
        var cmd = new UpdateFilmCommand(film.id(), film.name(), film.description(), film.releaseDate(), (int) film.duration().toMinutes(), film.genres(), film.mpa());
        when(filmUseCase.updateFilm(cmd)).thenReturn(film);
        assertThat(filmCompositionService.updateFilm(cmd)).isEqualTo(film);
    }

    @Nested
    class LikeOperations {
        @Test
        void shouldAddLike() {
            when(filmUseCase.findFilmById(1L)).thenReturn(Optional.of(film));
            User user = new User(2L, new Email("u2@example.com"), new Login("u2"), "User Two", LocalDate.of(1991, 2, 2));
            when(userUseCase.findUserById(2L)).thenReturn(Optional.of(user));
            when(likeService.addLike(1L, 2L)).thenReturn(true);
            assertThat(filmCompositionService.addLike(1L, 2L)).isTrue();
        }

        @Test
        void shouldThrowWhenFilmNotFoundOnAddLike() {
            when(filmUseCase.findFilmById(1L)).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class, () -> filmCompositionService.addLike(1L, 2L));
        }

        @Test
        void shouldThrowWhenUserNotFoundOnAddLike() {
            when(filmUseCase.findFilmById(1L)).thenReturn(Optional.of(film));
            when(userUseCase.findUserById(2L)).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class, () -> filmCompositionService.addLike(1L, 2L));
        }

        @Test
        void shouldRemoveLike() {
            when(filmUseCase.findFilmById(1L)).thenReturn(Optional.of(film));
            User user = new User(2L, new Email("u2@example.com"), new Login("u2"), "User Two", LocalDate.of(1991, 2, 2));
            when(userUseCase.findUserById(2L)).thenReturn(Optional.of(user));
            when(likeService.removeLike(1L, 2L)).thenReturn(true);
            assertThat(filmCompositionService.removeLike(1L, 2L)).isTrue();
        }
    }

    @Nested
    class PopularFilms {
        @Test
        void shouldReturnPopularFilms() {
            when(filmUseCase.getAllFilms()).thenReturn(List.of(film));
            when(likeService.getLikeCounts()).thenReturn(Map.of(film.id(), 1L));
            List<Film> popular = filmCompositionService.getPopularFilms(5);
            assertThat(popular).containsExactly(film);
        }

        @Test
        void shouldThrowWhenCountNegative() {
            assertThrows(ValidationException.class, () -> filmCompositionService.getPopularFilms(-1));
        }
    }

    @Nested
    class Getters {
        @Test
        void shouldGetFilmById() {
            when(filmUseCase.findFilmById(1L)).thenReturn(Optional.of(film));
            assertThat(filmCompositionService.getFilmById(1L)).isEqualTo(film);
        }

        @Test
        void shouldThrowWhenFilmNotFound() {
            when(filmUseCase.findFilmById(1L)).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class, () -> filmCompositionService.getFilmById(1L));
        }

        @Test
        void shouldGetGenres() {
            when(filmUseCase.getGeners()).thenReturn(List.of());
            assertThat(filmCompositionService.getGenres()).isEmpty();
        }

        @Test
        void shouldGetGenreById() {
            Genre genre = new Genre(1L, "Drama");
            when(filmUseCase.getGenreById(1L)).thenReturn(Optional.of(genre));
            assertThat(filmCompositionService.getGenreById(1L)).isEqualTo(genre);
        }

        @Test
        void shouldThrowWhenGenreNotFound() {
            when(filmUseCase.getGenreById(1L)).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class, () -> filmCompositionService.getGenreById(1L));
        }

        @Test
        void shouldGetMpas() {
            when(filmUseCase.getMpas()).thenReturn(List.of());
            assertThat(filmCompositionService.getMpas()).isEmpty();
        }

        @Test
        void shouldGetMpaById() {
            Mpa mpa = new Mpa(1L, "G");
            when(filmUseCase.getMpaById(1L)).thenReturn(Optional.of(mpa));
            assertThat(filmCompositionService.getMpaById(1L)).isEqualTo(mpa);
        }

        @Test
        void shouldThrowWhenMpaNotFound() {
            when(filmUseCase.getMpaById(1L)).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class, () -> filmCompositionService.getMpaById(1L));
        }

        @Test
        void shouldReturnCommonFilms() {
            when(userUseCase.findUserById(1L)).thenReturn(Optional.of(new User(1L, new Email("u1@x.com"), new Login("u1"), "U1", LocalDate.of(1990, 1, 1))));
            when(userUseCase.findUserById(2L)).thenReturn(Optional.of(new User(2L, new Email("u2@x.com"), new Login("u2"), "U2", LocalDate.of(1990, 1, 1))));
            when(likeService.findLikedFilms(1L)).thenReturn(Set.of(10L, 20L));
            when(likeService.findLikedFilms(2L)).thenReturn(Set.of(20L, 30L));
            when(filmUseCase.getFilmsByIds(Set.of(20L))).thenReturn(List.of(film));
            when(likeService.findUsersWhoLikedFilm(20L)).thenReturn(Set.of(1L, 2L, 3L));
            var result = filmCompositionService.getCommonFilms(1L, 2L);
            assertThat(result).containsExactly(film);
        }
    }
}