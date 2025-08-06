package ru.yandex.practicum.filmorate.films.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.films.application.port.in.FilmUseCase;
import ru.yandex.practicum.filmorate.films.application.port.in.RecommendationQuery;
import ru.yandex.practicum.filmorate.films.domain.model.Film;
import ru.yandex.practicum.filmorate.films.domain.model.value.Genre;
import ru.yandex.practicum.filmorate.films.domain.model.value.Mpa;
import ru.yandex.practicum.filmorate.likes.application.port.in.LikeUseCase;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RecommendationServiceTest {

  private LikeUseCase likeUseCase;
  private FilmUseCase filmUseCase;
  private RecommendationService service;

  @BeforeEach
  void setup() {
    likeUseCase = mock(LikeUseCase.class);
    filmUseCase = mock(FilmUseCase.class);
    service = new RecommendationService(likeUseCase, filmUseCase);
  }

  @Test
  @DisplayName("Should return empty list when user has no liked films")
  void shouldReturnEmptyWhenNoLikes() {
    when(likeUseCase.findLikedFilms(1L)).thenReturn(Set.of());

    var result = service.getRecommendations(RecommendationQuery.of(1L));

    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("Should return empty list when no similar user found")
  void shouldReturnEmptyWhenNoSimilarUser() {
    when(likeUseCase.findLikedFilms(1L)).thenReturn(Set.of(1L, 2L));
    when(likeUseCase.findAllUserFilmLikes()).thenReturn(new HashMap<>());

    var result = service.getRecommendations(RecommendationQuery.of(1L));

    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("Should return recommended films excluding already liked")
  void shouldReturnRecommendedFilms() {
    when(likeUseCase.findLikedFilms(1L)).thenReturn(Set.of(1L, 2L));

    Map<Long, Set<Long>> userLikesMap = new HashMap<>();
    userLikesMap.put(2L, new HashSet<>(Set.of(2L, 3L, 4L)));
    when(likeUseCase.findAllUserFilmLikes()).thenReturn(userLikesMap);

    Film f3 = film(3L);
    Film f4 = film(4L);
    when(filmUseCase.getFilmsByIds(List.of(3L, 4L))).thenReturn(List.of(f3, f4));

    var result = service.getRecommendations(RecommendationQuery.of(1L));

    assertThat(result).containsExactly(f3, f4);
  }

  private Film film(Long id) {
    return filmWith(id, 2020, 1L, "Комедия");
  }

  private Film filmWith(Long id, int year, Long genreId, String genreName) {
    return Film.builder()
               .id(id)
               .name("Film " + id)
               .description("Description")
               .releaseDate(LocalDate.of(year, 1, 1))
               .duration(Duration.ofMinutes(100))
               .genres(Set.of(new Genre(genreId, genreName)))
               .mpa(new Mpa(1L, "PG"))
               .build();
  }

  @Test
  @DisplayName("Should filter by genre, year, limit")
  void shouldApplyFiltersCorrectly() {
    Set<Long> userLikes = Set.of(1L, 99L);
    Set<Long> similarLikes = Set.of(2L, 3L, 4L, 5L, 99L);
    when(likeUseCase.findLikedFilms(1L)).thenReturn(userLikes);

    Map<Long, Set<Long>> similarMap = new HashMap<>();
    similarMap.put(2L, new HashSet<>(similarLikes));
    when(likeUseCase.findAllUserFilmLikes()).thenReturn(similarMap);

    Film matching = filmWith(2L, 2020, 1L, "Комедия");
    Film wrongGenre = filmWith(3L, 2020, 2L, "Драма");
    Film wrongYear = filmWith(4L, 2010, 1L, "Комедия");
    Film other = filmWith(5L, 2021, 1L, "Комедия");

    when(filmUseCase.getFilmsByIds(List.of(2L, 3L, 4L, 5L)))
        .thenReturn(List.of(matching, wrongGenre, wrongYear, other));

    RecommendationQuery query = new RecommendationQuery(
        1L,
        Optional.of(5),
        Optional.of(1L),
        Optional.of(2020)
    );

    var result = service.getRecommendations(query);

        assertThat(result).extracting(Film::id).containsExactly(2L);
    }

    private Film film(Long id) {
        return filmWith(id, 2020, 1L, "Комедия");
    }

    private Film filmWith(Long id, int year, Long genreId, String genreName) {
        Genre genre = null;

        if (genreId != null && genreName != null) {
            genre = new Genre(genreId, genreName);
        }

        Set<Genre> genres = (genre != null)
                ? Set.of(genre)
                : Set.of(); // гарантированно не содержит null

        return Film.builder()
                .id(id)
                .name("Film " + id)
                .description("Description")
                .releaseDate(LocalDate.of(year, 1, 1))
                .duration(Duration.ofMinutes(100))
                .genres(genres)
                .mpa(new Mpa(1L, "PG")) // можно тоже обернуть в защиту, если в будущем параметризуешь
                .isDeleted(false)
                .build();
    }
}