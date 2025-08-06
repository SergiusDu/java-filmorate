package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.yandex.practicum.filmorate.common.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.common.exception.ValidationException;
import ru.yandex.practicum.filmorate.directors.application.port.in.DirectorUseCase;
import ru.yandex.practicum.filmorate.directors.domain.model.Director;
import ru.yandex.practicum.filmorate.films.application.port.in.FilmUseCase;
import ru.yandex.practicum.filmorate.users.application.port.in.UserUseCase;
import ru.yandex.practicum.filmorate.films.domain.model.Film;
import ru.yandex.practicum.filmorate.films.domain.model.value.Genre;
import ru.yandex.practicum.filmorate.films.domain.model.value.Mpa;
import ru.yandex.practicum.filmorate.films.domain.port.CreateFilmCommand;
import ru.yandex.practicum.filmorate.films.domain.port.UpdateFilmCommand;
import ru.yandex.practicum.filmorate.infrastructure.web.dto.FilmWithDirectors;
import ru.yandex.practicum.filmorate.likes.application.port.in.LikeUseCase;
import ru.yandex.practicum.filmorate.service.FilmCompositionService;
import ru.yandex.practicum.filmorate.users.application.port.in.UserUseCase;
import ru.yandex.practicum.filmorate.users.domain.model.User;
import ru.yandex.practicum.filmorate.users.domain.model.value.Email;
import ru.yandex.practicum.filmorate.users.domain.model.value.Login;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


class FilmCompositionServiceTest {

  @Mock
  private FilmUseCase filmUseCase;
  @Mock
  private LikeUseCase likeService;
  @Mock
  private UserUseCase userUseCase;
  @Mock
  private DirectorUseCase directorUseCase;
  @InjectMocks
  private FilmCompositionService filmCompositionService;

  private Film film;
  private CreateFilmCommand createFilmCommand;
  private UpdateFilmCommand updateFilmCommand;
  private User user;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    Set<Genre> genres = Set.of(new Genre(1L, "Drama"));
    Mpa mpa = new Mpa(1L, "G");

    film = new Film(1L,
                    "Test Film",
                    "Description",
                    LocalDate.of(2020, 1, 1),
                    Duration.ofMinutes(90),
                    genres,
                    mpa);

    createFilmCommand = new CreateFilmCommand("Test Film",
                                              "Description",
                                              LocalDate.of(2020, 1, 1),
                                              90L,
                                              genres,
                                              mpa.id(),
                                              Collections.emptySet());

    updateFilmCommand = new UpdateFilmCommand(1L,
                                              "Updated Film",
                                              "Updated Desc",
                                              LocalDate.of(2021, 1, 1),
                                              100L,
                                              genres,
                                              mpa.id(),
                                              Collections.emptySet());

    user = User.builder()
               .id(2L)
               .email(new Email("test@user.com"))
               .login(new Login("testuser"))
               .name("Test User")
               .birthday(LocalDate.of(2000, 1, 1))
               .build();
  }

  @Test
  void shouldReturnAllFilms() {
    List<Director> directors = Collections.emptyList();
    FilmWithDirectors filmWithDirectors = new FilmWithDirectors(film, directors);

    when(filmUseCase.getAllFilms()).thenReturn(List.of(film));
    when(directorUseCase.getDirectorsForFilmIds(Set.of(1L))).thenReturn(Collections.emptyMap());

    assertThat(filmCompositionService.getAllFilms()).containsExactly(filmWithDirectors);
  }

  @Test
  void shouldCreateFilm() {
    when(filmUseCase.addFilm(createFilmCommand)).thenReturn(film);
    when(directorUseCase.getDirectorsForFilmIds(Set.of(film.id()))).thenReturn(Collections.emptyMap());

    FilmWithDirectors expected = new FilmWithDirectors(film, Collections.emptyList());
    FilmWithDirectors actual = filmCompositionService.createFilm(createFilmCommand);

    assertThat(actual).isEqualTo(expected);
  }

  @Test
  void shouldUpdateFilm() {
    Film updatedFilm = new Film(1L,
                                "Updated Film",
                                "Updated Desc",
                                LocalDate.of(2021, 1, 1),
                                Duration.ofMinutes(100L),
                                film.genres(),
                                film.mpa());
    when(filmUseCase.updateFilm(updateFilmCommand)).thenReturn(updatedFilm);
    when(directorUseCase.getDirectorsForFilmIds(Set.of(updatedFilm.id()))).thenReturn(Collections.emptyMap());

    FilmWithDirectors expected = new FilmWithDirectors(updatedFilm, Collections.emptyList());
    FilmWithDirectors actual = filmCompositionService.updateFilm(updateFilmCommand);

    assertThat(actual).isEqualTo(expected);
  }

  @Nested
  class LikeOperations {
    @Test
    void shouldAddLike() {
      when(filmUseCase.findFilmById(1L)).thenReturn(Optional.of(film));
      when(userUseCase.findUserById(2L)).thenReturn(Optional.of(user));
      when(likeService.addLike(1L, 2L)).thenReturn(true);
      assertThat(filmCompositionService.addLike(1L, 2L)).isTrue();
    }

    @Test
    void shouldThrowExceptionWhenFilmNotFoundOnAddLike() {
      when(filmUseCase.findFilmById(1L)).thenReturn(Optional.empty());
      assertThrows(ResourceNotFoundException.class, () -> filmCompositionService.addLike(1L, 2L));
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundOnAddLike() {
      when(filmUseCase.findFilmById(1L)).thenReturn(Optional.of(film));
      when(userUseCase.findUserById(2L)).thenReturn(Optional.empty());
      assertThrows(ResourceNotFoundException.class, () -> filmCompositionService.addLike(1L, 2L));
    }

    @Test
    void shouldRemoveLike() {
      when(filmUseCase.findFilmById(1L)).thenReturn(Optional.of(film));
      when(userUseCase.findUserById(2L)).thenReturn(Optional.of(user));
      when(likeService.removeLike(1L, 2L)).thenReturn(true);
      assertThat(filmCompositionService.removeLike(1L, 2L)).isTrue();
    }
  }

  @Nested
  class PopularFilms {
    @Test
    void shouldReturnPopularFilms() {
      when(likeService.getPopularFilmIds(5)).thenReturn(new LinkedHashSet<>(List.of(1L)));
      when(filmUseCase.getFilmsByIds(List.of(1L))).thenReturn(List.of(film));

      List<Director> directors = Collections.emptyList();
      FilmWithDirectors filmWithDirectors = new FilmWithDirectors(film, directors);
      when(directorUseCase.getDirectorsForFilmIds(Set.of(1L))).thenReturn(Collections.emptyMap());

      assertThat(filmCompositionService.getPopularFilms(5)).containsExactly(filmWithDirectors);
    }

    @Test
    void shouldThrowExceptionWhenCountIsNegative() {
      assertThrows(ValidationException.class, () -> filmCompositionService.getPopularFilms(-1));
    }
  }

  @Nested
  class Getters {
    @Test
    void shouldGetFilmById() {
      when(filmUseCase.findFilmById(1L)).thenReturn(Optional.of(film));
      when(directorUseCase.getDirectorsForFilmIds(Set.of(1L))).thenReturn(Collections.emptyMap());

      FilmWithDirectors expected = new FilmWithDirectors(film, Collections.emptyList());
      FilmWithDirectors actual = filmCompositionService.getFilmById(1L);

      assertThat(actual).isEqualTo(expected);
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
      User user1 = User.builder()
                       .id(1L)
                       .email(new Email("u1@a.com"))
                       .login(new Login("u1"))
                       .name("u1")
                       .birthday(LocalDate.of(1990, 1, 1))
                       .build();
      User friend = User.builder()
                        .id(2L)
                        .email(new Email("u2@a.com"))
                        .login(new Login("u2"))
                        .name("u2")
                        .birthday(LocalDate.of(1991, 1, 1))
                        .build();
      Film commonFilm = new Film(20L,
                                 "Common Film",
                                 "Desc",
                                 LocalDate.of(2020, 1, 1),
                                 Duration.ofMinutes(120),
                                 Set.of(),
                                 new Mpa(1L, "G"));

      when(userUseCase.findUserById(1L)).thenReturn(Optional.of(user1));
      when(userUseCase.findUserById(2L)).thenReturn(Optional.of(friend));
      when(likeService.findLikedFilms(1L)).thenReturn(Set.of(10L, 20L));
      when(likeService.findLikedFilms(2L)).thenReturn(Set.of(20L, 30L));
      when(filmUseCase.getFilmsByIds(List.of(20L))).thenReturn(List.of(commonFilm));
      when(likeService.getLikeCountsForFilms(Set.of(20L))).thenReturn(Map.of(20L, 2));
}
        @Test
        void shouldReturnAllWhenNoFilters() {
            Film film = new Film(1L, "Test Film", "Description", LocalDate.of(2020, 1, 1),
                    Duration.ofMinutes(90), Set.of(new Genre(1L, "Drama")), false, new Mpa(1L, "G"));

            FilmRatingQuery query = FilmRatingQuery.of(5, null, null, null, null);

            when(filmUseCase.findPopularFilms(query)).thenReturn(List.of(film));

            List<Film> result = filmCompositionService.getPopularFilms(query);

            assertThat(result).containsExactly(film);
        }

        @Test
        void shouldReturnEmptyWhenNoMatchingFilms() {
            when(likeService.getPopularFilmIds(5)).thenReturn(Set.of());
            when(filmUseCase.getFilmsByIds(Set.of())).thenReturn(List.of());

            List<Film> result = filmCompositionService.getPopularFilms(defaultQuery);

            assertThat(result).isEmpty();
        }
    }
    }
