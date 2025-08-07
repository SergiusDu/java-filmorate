package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;
import ru.yandex.practicum.filmorate.common.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.directors.application.port.in.DirectorUseCase;
import ru.yandex.practicum.filmorate.events.domain.model.value.Operation;
import ru.yandex.practicum.filmorate.events.domain.service.DomainEventPublisher;
import ru.yandex.practicum.filmorate.films.application.port.in.FilmRatingQuery;
import ru.yandex.practicum.filmorate.films.application.port.in.FilmUseCase;
import ru.yandex.practicum.filmorate.films.domain.model.Film;
import ru.yandex.practicum.filmorate.films.domain.model.value.Genre;
import ru.yandex.practicum.filmorate.films.domain.model.value.Mpa;
import ru.yandex.practicum.filmorate.films.domain.port.CreateFilmCommand;
import ru.yandex.practicum.filmorate.films.domain.port.UpdateFilmCommand;
import ru.yandex.practicum.filmorate.infrastructure.web.dto.FilmWithDirectors;
import ru.yandex.practicum.filmorate.likes.application.port.in.LikeUseCase;
import ru.yandex.practicum.filmorate.search.application.port.in.SearchUseCase;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


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
  @Mock
  private ApplicationEventPublisher eventPublisher;
  @Mock
  private SearchUseCase searchUseCase;

  private Film film;
  private CreateFilmCommand createFilmCommand;
  private UpdateFilmCommand updateFilmCommand;
  private User user;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    Set<Genre> genres = Set.of(new Genre(1L, "Drama"));
    Mpa mpa = new Mpa(1L, "G");

    DomainEventPublisher noopPublisher = new DomainEventPublisher(eventPublisher) {
      public void publishLikeEvent(Long userId, Operation operation, Long filmId) {}
    };

    filmCompositionService = new FilmCompositionService(
            filmUseCase,
            likeService,
            userUseCase,
            directorUseCase,
            eventPublisher,
            searchUseCase,
            noopPublisher
    );

    film = Film.builder()
               .id(1L)
               .name("Test Film")
               .description("Description")
               .releaseDate(LocalDate.of(2020, 1, 1))
               .duration(Duration.ofMinutes(90))
               .genres(genres)
               .mpa(mpa)
               .build();

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
    when(filmUseCase.getAllFilms()).thenReturn(List.of(film));
    when(directorUseCase.getDirectorsForFilmIds(Set.of(1L))).thenReturn(Collections.emptyMap());
    FilmWithDirectors expected = new FilmWithDirectors(film, Collections.emptyList());
    assertThat(filmCompositionService.getAllFilms()).containsExactly(expected);
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
    Film updatedFilm = Film.builder()
                           .id(1L)
                           .name("Updated Film")
                           .description("Updated Desc")
                           .releaseDate(LocalDate.of(2021, 1, 1))
                           .duration(Duration.ofMinutes(100L))
                           .genres(film.genres())
                           .mpa(film.mpa())
                           .build();
    when(filmUseCase.updateFilm(updateFilmCommand)).thenReturn(updatedFilm);
    when(directorUseCase.getDirectorsForFilmIds(Set.of(updatedFilm.id()))).thenReturn(Collections.emptyMap());
    FilmWithDirectors expected = new FilmWithDirectors(updatedFilm, Collections.emptyList());
    FilmWithDirectors actual = filmCompositionService.updateFilm(updateFilmCommand);
    assertThat(actual).isEqualTo(expected);
  }

  @Nested
  class LikeOperationsTests {
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
  class PopularFilmsTests {
    @Test
    void shouldReturnPopularFilmsWithQuery() {
      FilmRatingQuery query = FilmRatingQuery.of(1, null, null, null, FilmRatingQuery.SortBy.LIKES);

      List<Long> filmIds = List.of(film.id());
      when(filmUseCase.getFilmIdsByFilters(null, null)).thenReturn(filmIds);
      when(likeService.getLikeCountsForFilms(new HashSet<>(filmIds))).thenReturn(Map.of(film.id(), 10));
      when(filmUseCase.getFilmsByIds(new ArrayList<>(filmIds))).thenReturn(List.of(film));
      when(directorUseCase.getDirectorsForFilmIds(Set.of(film.id()))).thenReturn(Collections.emptyMap());

      FilmWithDirectors expected = new FilmWithDirectors(film, Collections.emptyList());
      List<FilmWithDirectors> result = filmCompositionService.getPopularFilms(query);

      assertThat(result).containsExactly(expected);
    }

    @Test
    void shouldReturnEmptyWhenNoMatchingFilmsForQuery() {
      FilmRatingQuery query = FilmRatingQuery.ofDefault();
      when(filmUseCase.getFilmIdsByFilters(any(), any())).thenReturn(Collections.emptyList());

      List<FilmWithDirectors> result = filmCompositionService.getPopularFilms(query);

      assertThat(result).isEmpty();
    }
  }

  @Nested
  class GetterTests {
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
      when(filmUseCase.getGenres()).thenReturn(List.of());
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
      Film commonFilm = Film.builder()
                            .id(20L)
                            .name("Common Film")
                            .description("Desc")
                            .releaseDate(LocalDate.of(2020, 1, 1))
                            .duration(Duration.ofMinutes(120))
                            .genres(Set.of())
                            .mpa(new Mpa(1L, "G"))
                            .build();

      when(userUseCase.findUserById(1L)).thenReturn(Optional.of(user1));
      when(userUseCase.findUserById(2L)).thenReturn(Optional.of(friend));
      when(likeService.findLikedFilms(1L)).thenReturn(Set.of(10L, 20L));
      when(likeService.findLikedFilms(2L)).thenReturn(Set.of(20L, 30L));

      List<Long> commonFilmIds = List.of(20L);
      List<Film> commonFilmsFromRepo = List.of(commonFilm);
      when(filmUseCase.getFilmsByIds(commonFilmIds)).thenReturn(commonFilmsFromRepo);

      Map<Long, Integer> likeCounts = Map.of(20L, 2);
      when(likeService.getLikeCountsForFilms(Set.of(20L))).thenReturn(likeCounts);

      List<Film> result = filmCompositionService.getCommonFilms(1L, 2L);
      assertThat(result).containsExactly(commonFilm);
    }
  }
}