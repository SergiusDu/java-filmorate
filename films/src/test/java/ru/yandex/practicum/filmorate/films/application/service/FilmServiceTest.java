package ru.yandex.practicum.filmorate.films.application.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.yandex.practicum.filmorate.films.domain.model.Film;
import ru.yandex.practicum.filmorate.films.domain.model.value.Genre;
import ru.yandex.practicum.filmorate.films.domain.model.value.Mpa;
import ru.yandex.practicum.filmorate.films.domain.port.FilmRepository;
import ru.yandex.practicum.filmorate.films.domain.service.FilmValidationService;
import ru.yandex.practicum.filmorate.films.domain.port.GenreRepository;
import ru.yandex.practicum.filmorate.films.domain.port.MpaRepository;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class FilmServiceTest {

    @Mock
    private FilmRepository filmRepository;

    @Mock
    private GenreRepository genreRepository;

    @Mock
    private MpaRepository mpaRepository;

    @Mock
    private FilmValidationService filmValidationService;

    @InjectMocks
    private FilmService filmService;

    public FilmServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnCommonFilms_whenValidUserIds() {
        long userId = 1L;
        long friendId = 2L;

        Film film = new Film(10L, "Common Film", "desc", LocalDate.of(2020, 1, 1),
                Duration.ofMinutes(100), Set.of(new Genre(1L, "Drama")), new Mpa(1L, "G"));
        List<Film> expectedFilms = List.of(film);

        when(filmRepository.findCommonFilmsSortedByLikes(userId, friendId)).thenReturn(expectedFilms);

        List<Film> result = filmService.getCommonFilms(userId, friendId);

        assertThat(result).isEqualTo(expectedFilms);
        verify(filmRepository, times(1)).findCommonFilmsSortedByLikes(userId, friendId);
    }

    @Test
    void shouldReturnEmptyList_whenNoCommonFilmsFound() {
        long userId = 1L;
        long friendId = 2L;

        when(filmRepository.findCommonFilmsSortedByLikes(userId, friendId)).thenReturn(List.of());

        List<Film> result = filmService.getCommonFilms(userId, friendId);

        assertThat(result).isEmpty();
        verify(filmRepository).findCommonFilmsSortedByLikes(userId, friendId);
    }

    @Test
    void shouldHandleNullFromRepository() {
        long userId = 1L;
        long friendId = 2L;

        when(filmRepository.findCommonFilmsSortedByLikes(userId, friendId)).thenReturn(null);

        List<Film> result = filmService.getCommonFilms(userId, friendId);

        assertThat(result).isNull();
        verify(filmRepository).findCommonFilmsSortedByLikes(userId, friendId);
    }


}