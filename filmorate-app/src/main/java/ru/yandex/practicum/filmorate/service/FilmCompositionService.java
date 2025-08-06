package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.jheaps.annotations.VisibleForTesting;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.common.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.common.exception.ValidationException;
import ru.yandex.practicum.filmorate.films.application.port.in.FilmRatingQuery;
import ru.yandex.practicum.filmorate.films.application.port.in.FilmUseCase;
import ru.yandex.practicum.filmorate.films.application.port.in.RecommendationQuery;
import ru.yandex.practicum.filmorate.films.domain.model.Film;
import ru.yandex.practicum.filmorate.films.domain.model.value.Genre;
import ru.yandex.practicum.filmorate.films.domain.model.value.Mpa;
import ru.yandex.practicum.filmorate.films.domain.port.CreateFilmCommand;
import ru.yandex.practicum.filmorate.films.domain.port.UpdateFilmCommand;
import ru.yandex.practicum.filmorate.likes.application.port.in.LikeUseCase;
import ru.yandex.practicum.filmorate.users.application.port.in.UserUseCase;
import ru.yandex.practicum.filmorate.users.domain.model.User;

import java.util.*;

@Service
@RequiredArgsConstructor
public class FilmCompositionService {
    private final FilmUseCase filmUseCase;
    private final LikeUseCase likeUseCase;
    private final UserUseCase userUseCase;


    public List<Film> getAllFilms() {
        return filmUseCase.getAllFilms();
    }

    public Film createFilm(CreateFilmCommand command) {
        return filmUseCase.addFilm(command);
    }

    public Film updateFilm(UpdateFilmCommand command) {
        return filmUseCase.updateFilm(command);
    }

    public boolean addLike(long filmId, long userId) {
        var film = getFilmOrThrow(filmId);
        var user = getUserOrThrow(userId);
        return likeUseCase.addLike(film.id(), user.id());
    }

    private void validateFilmId(long filmId) {
        if (filmUseCase.findFilmById(filmId)
                .isEmpty())
            throw new ResourceNotFoundException("Film with id " + filmId + " not found");
    }

    private void validateUserId(long userId) {
        if (userUseCase.findUserById(userId)
                .isEmpty())
            throw new ResourceNotFoundException("User with id " + userId + " not found");
    }

    public boolean removeLike(long filmId, long userId) {
        var film = getFilmOrThrow(filmId);
        var user = getUserOrThrow(userId);
        return likeUseCase.removeLike(film.id(), user.id());
    }

    public List<Film> getPopularFilms(FilmRatingQuery query) {
        return filmUseCase.findPopularFilms(query);
    }

    public List<Film> getRecommendations(RecommendationQuery query) {
        return filmUseCase.getRecommendations(query);
    }

    public List<Genre> getGenres() {
        return filmUseCase.getGenres();
    }

    public Genre getGenreById(long id) {
        return filmUseCase.getGenreById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Genre with id " + id + " not found"));
    }

    public List<Mpa> getMpas() {
        return filmUseCase.getMpas();
    }

    public Mpa getMpaById(long id) {
        return filmUseCase.getMpaById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mpa with id " + id + " not found"));
    }

    public Film getFilmById(long id) {
        return getFilmOrThrow(id);
    }

    public List<Film> getCommonFilms(long userId, long friendId) {
        if (userId == friendId) {
            throw new ValidationException("User cannot be compared with themselves.");
        }

        var user = getUserOrThrow(userId);
        var friend = getUserOrThrow(friendId);

        Set<Long> userLikes = likeUseCase.findLikedFilms(user.id());
        Set<Long> friendLikes = likeUseCase.findLikedFilms(friend.id());

        Set<Long> commonFilmIds = new HashSet<>(userLikes);
        commonFilmIds.retainAll(friendLikes);

        if (commonFilmIds.isEmpty()) {
            return List.of();
        }

        List<Film> commonFilms = filmUseCase.getFilmsByIds(commonFilmIds);
        Map<Long, Integer> likeCounts = likeUseCase.getLikeCountsForFilms(commonFilmIds);

        return commonFilms.stream()
                .sorted(Comparator.comparingInt(film -> -likeCounts.getOrDefault(film.id(), 0)))
                .toList();
    }

    public Film getFilmOrThrow(long id) {
        return filmUseCase.findFilmById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Film with id " + id + " not found"));
    }

    public User getUserOrThrow(long id) {
        return userUseCase.findUserById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
    }

    /**
     *
     * @deprecated Use {@link #getPopularFilms(FilmRatingQuery)} instead.
     */
    @Deprecated
    @VisibleForTesting
    public List<Film> getPopularFilms(int count) {
        if (count < 0) {
            throw new ValidationException("Count parameter cannot be negative");
        }
        return filmUseCase.getFilmsByIds(likeUseCase.getPopularFilmIds(count));
    }
}
