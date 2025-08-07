package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.filmorate.common.enums.SortBy;
import ru.yandex.practicum.filmorate.common.events.FilmSearchDataUpdatedEvent;
import ru.yandex.practicum.filmorate.common.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.common.exception.ValidationException;
import ru.yandex.practicum.filmorate.directors.application.port.in.DirectorUseCase;
import ru.yandex.practicum.filmorate.directors.domain.model.Director;
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
import ru.yandex.practicum.filmorate.users.application.port.in.UserUseCase;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmCompositionService {
  private final FilmUseCase filmUseCase;
  private final LikeUseCase likeService;
  private final UserUseCase userUseCase;
  private final DirectorUseCase directorUseCase;
  private final SearchUseCase searchUseCase;
  private final ApplicationEventPublisher eventPublisher;
  private final DomainEventPublisher domainEventPublisher;

  @Transactional
  public FilmWithDirectors createFilm(CreateFilmCommand command) {
    Film film = filmUseCase.addFilm(command);
    directorUseCase.updateFilmDirectors(film.id(), command.directorIds());
    return getFilmWithDirectors(film);
  }

  @Transactional
  public FilmWithDirectors updateFilm(UpdateFilmCommand command) {
    Film film = filmUseCase.updateFilm(command);
    directorUseCase.updateFilmDirectors(film.id(), command.directorIds());
    FilmWithDirectors fw = getFilmWithDirectors(film);
    publishSearchUpdateEvent(fw);
    return fw;
  }

  private FilmWithDirectors getFilmWithDirectors(Film film) {
    Map<Long, List<Director>> map = directorUseCase.getDirectorsForFilmIds(Set.of(film.id()));
    List<Director> directors = map.getOrDefault(film.id(), Collections.emptyList());
    FilmWithDirectors fw = new FilmWithDirectors(film, directors);
    publishSearchUpdateEvent(fw);
    return fw;
  }

  private void publishSearchUpdateEvent(FilmWithDirectors fw) {
    Set<String> names = fw.directors().stream()
            .map(Director::name)
            .collect(Collectors.toSet());
    FilmSearchDataUpdatedEvent ev = new FilmSearchDataUpdatedEvent(
            this,
            fw.film().id(),
            fw.film().name(),
            names
    );
    eventPublisher.publishEvent(ev);
  }

  public List<FilmWithDirectors> getAllFilms() {
    return enrichFilmsWithDirectors(filmUseCase.getAllFilms());
  }

  public List<FilmWithDirectors> enrichFilmsWithDirectors(List<Film> films) {
    if (films.isEmpty()) return Collections.emptyList();
    Set<Long> ids = films.stream().map(Film::id).collect(Collectors.toSet());
    Map<Long, List<Director>> map = directorUseCase.getDirectorsForFilmIds(ids);
    return films.stream()
                .map(film -> new FilmWithDirectors(film,
                                                   directorsByFilmId.getOrDefault(film.id(), Collections.emptyList())))
                .toList();
  }

  public boolean addLike(long filmId, long userId) {
    validateFilmExists(filmId);
    validateUserExists(userId);
    boolean added = likeService.addLike(filmId, userId);
    if (added) domainEventPublisher.publishLikeEvent(userId, Operation.ADD, filmId);
    return added;
  }

  public boolean removeLike(long filmId, long userId) {
    validateFilmExists(filmId);
    validateUserExists(userId);
    boolean removed = likeService.removeLike(filmId, userId);
    if (removed) domainEventPublisher.publishLikeEvent(userId, Operation.REMOVE, filmId);
    return removed;
  }

  public List<FilmWithDirectors> getPopularFilms(FilmRatingQuery query) {
    List<Long> filmIds = filmUseCase.getFilmIdsByFilters(
            query.genreId().orElse(null),
            query.year().orElse(null)
    );
    query.directorId().ifPresent(directorId -> {
      Set<Long> dirs = new HashSet<>(
              directorUseCase.getFilmIdsByDirector(directorId, SortBy.LIKES)
      );
      filmIds.retainAll(dirs);
    });
    if (filmIds.isEmpty()) return Collections.emptyList();
    Map<Long, Integer> counts = likeService.getLikeCountsForFilms(new HashSet<>(filmIds));
    List<Long> sorted = filmIds.stream()
            .sorted(Comparator.comparingInt(id -> -counts.getOrDefault(id, 0)))
            .limit(query.limit())
            .toList();
    if (sorted.isEmpty()) return Collections.emptyList();
    List<Film> films = filmUseCase.getFilmsByIds(sorted);
    return enrichFilmsWithDirectors(films);
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

  public FilmWithDirectors getFilmById(long id) {
    Film film = filmUseCase.findFilmById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Film with id " + id + " not found"));
    return getFilmWithDirectors(film);
  }

  public List<Film> getCommonFilms(long userId, long friendId) {
    if (userId == friendId) {
      throw new ValidationException("User cannot be compared with themselves.");
    }
    validateUserExists(userId);
    validateUserExists(friendId);
    Set<Long> u = likeService.findLikedFilms(userId);
    Set<Long> f = likeService.findLikedFilms(friendId);
    u.retainAll(f);
    if (u.isEmpty()) return Collections.emptyList();
    List<Film> films = filmUseCase.getFilmsByIds(new ArrayList<>(u));
    Map<Long, Integer> counts = likeService.getLikeCountsForFilms(u);
    return films.stream()
            .sorted(Comparator.comparingInt(fm -> -counts.getOrDefault(fm.id(), 0)))
            .toList();
  }

  public List<FilmWithDirectors> getDirectorFilms(long directorId, SortBy sortBy) {
    List<Long> ids = directorUseCase.getFilmIdsByDirector(directorId, sortBy);
    if (ids.isEmpty()) return Collections.emptyList();
    List<Film> films = filmUseCase.getFilmsByIds(ids);
    return enrichFilmsWithDirectors(films);
  }

  public void deleteFilmById(long id) {
    filmUseCase.deleteFilmById(id);
  }

  public List<FilmWithDirectors> searchFilms(@RequestParam String query,
                                             @RequestParam List<String> by) {
    Set<String> keys = by.stream().map(String::toUpperCase).collect(Collectors.toSet());
    List<Long> ids = searchUseCase.searchFilms(query, keys);
    if (ids.isEmpty()) return Collections.emptyList();
    List<Film> films = filmUseCase.getFilmsByIds(ids);
    return enrichFilmsWithDirectors(films);
  }

  private void validateFilmExists(long filmId) {
    if (filmUseCase.findFilmById(filmId).isEmpty()) {
      throw new ResourceNotFoundException("Film with id " + filmId + " not found");
    }
  }

  private void validateUserExists(long userId) {
    if (userUseCase.findUserById(userId).isEmpty()) {
      throw new ResourceNotFoundException("User with id " + userId + " not found");
    }
  }
}
