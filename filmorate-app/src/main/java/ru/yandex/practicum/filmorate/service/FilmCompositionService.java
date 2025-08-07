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
import ru.yandex.practicum.filmorate.films.application.port.in.FilmRatingQuery;
import ru.yandex.practicum.filmorate.films.application.port.in.FilmUseCase;
import ru.yandex.practicum.filmorate.films.application.port.in.RecommendationQuery;
import ru.yandex.practicum.filmorate.films.domain.model.Film;
import ru.yandex.practicum.filmorate.films.domain.model.value.Genre;
import ru.yandex.practicum.filmorate.films.domain.model.value.Mpa;
import ru.yandex.practicum.filmorate.films.domain.port.CreateFilmCommand;
import ru.yandex.practicum.filmorate.films.domain.port.UpdateFilmCommand;
import ru.yandex.practicum.filmorate.infrastructure.web.dto.FilmWithDirectors;
import ru.yandex.practicum.filmorate.likes.application.port.in.LikeUseCase;
import ru.yandex.practicum.filmorate.search.application.port.in.SearchUseCase;
import ru.yandex.practicum.filmorate.users.application.port.in.UserUseCase;
import ru.yandex.practicum.filmorate.users.domain.model.User;

import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class FilmCompositionService {
  private final FilmUseCase filmUseCase;
  private final LikeUseCase likeService;
  private final UserUseCase userUseCase;
  private final DirectorUseCase directorUseCase;
  private final ApplicationEventPublisher eventPublisher;
  private final SearchUseCase searchUseCase;

  @Transactional
  public FilmWithDirectors createFilm(CreateFilmCommand command) {
    Film film = filmUseCase.addFilm(command);
    directorUseCase.updateFilmDirectors(film.id(), command.directorIds());
    return getFilmWithDirectors(film);
  }

  private FilmWithDirectors getFilmWithDirectors(Film film) {
    Map<Long, List<Director>> directorsMap = directorUseCase.getDirectorsForFilmIds(Set.of(film.id()));
    List<Director> directors = directorsMap.getOrDefault(film.id(), Collections.emptyList());
    FilmWithDirectors filmWithDirectors = new FilmWithDirectors(film, directors);
    publishSearchUpdateEvent(filmWithDirectors);
    return filmWithDirectors;
  }

  private void publishSearchUpdateEvent(FilmWithDirectors filmWithDirectors) {
    Set<String> directorNames = filmWithDirectors.directors() != null
                                ? filmWithDirectors.directors()
                                                   .stream()
                                                   .map(Director::name)
                                                   .collect(Collectors.toSet())
                                : Collections.emptySet();

    FilmSearchDataUpdatedEvent event = new FilmSearchDataUpdatedEvent(this,
                                                                      filmWithDirectors.film()
                                                                                       .id(),
                                                                      filmWithDirectors.film()
                                                                                       .name(),
                                                                      directorNames);
    eventPublisher.publishEvent(event);
  }

  @Transactional
  public FilmWithDirectors updateFilm(UpdateFilmCommand command) {
    Film film = filmUseCase.updateFilm(command);
    directorUseCase.updateFilmDirectors(film.id(), command.directorIds());
    FilmWithDirectors filmWithDirectors = getFilmWithDirectors(film);
    publishSearchUpdateEvent(filmWithDirectors);
    return filmWithDirectors;
  }

  public List<Film> getRecommendations(RecommendationQuery query) {
    return filmUseCase.getRecommendations(query);
  }

  public List<FilmWithDirectors> getAllFilms() {
    return enrichFilmsWithDirectors(filmUseCase.getAllFilms());
  }

  public List<FilmWithDirectors> enrichFilmsWithDirectors(List<Film> films) {
    if (films.isEmpty()) {
      return Collections.emptyList();
    }
    Set<Long> filmIds = films.stream()
                             .map(Film::id)
                             .collect(Collectors.toSet());
    Map<Long, List<Director>> directorsByFilmId = directorUseCase.getDirectorsForFilmIds(filmIds);
    return films.stream()
                .map(film -> new FilmWithDirectors(film,
                                                   directorsByFilmId.getOrDefault(film.id(), Collections.emptyList())))
                .toList();
  }

  public boolean addLike(long filmId, long userId) {
    validateFilmExists(filmId);
    validateUserExists(userId);
    return likeService.addLike(filmId, userId);
  }

  public void validateFilmExists(long filmId) {
    if (filmUseCase.findFilmById(filmId)
                   .isEmpty())
      throw new ResourceNotFoundException("Film with id " + filmId + " not found");
  }

  public void validateUserExists(long userId) {
    if (userUseCase.findUserById(userId)
                   .isEmpty())
      throw new ResourceNotFoundException("User with id " + userId + " not found");
  }

  public boolean removeLike(long filmId, long userId) {
    validateFilmExists(filmId);
    validateUserExists(userId);
    return likeService.removeLike(filmId, userId);
  }

  public List<FilmWithDirectors> getPopularFilms(FilmRatingQuery query) {
    List<Long> filmIds = filmUseCase.getFilmIdsByFilters(query.genreId()
                                                              .orElse(null),
                                                         query.year()
                                                              .orElse(null));

    Optional<Long> directorIdOpt = query.directorId();
    if (directorIdOpt.isPresent()) {
      Set<Long> directorFilmIds = new HashSet<>(directorUseCase.getFilmIdsByDirector(directorIdOpt.get(),
                                                                                     SortBy.LIKES));
      filmIds = filmIds.stream()
                       .filter(directorFilmIds::contains)
                       .toList();
    }

    if (filmIds.isEmpty()) {
      return Collections.emptyList();
    }

    Map<Long, Integer> likeCounts = likeService.getLikeCountsForFilms(new HashSet<>(filmIds));

    List<Long> sortedPopularFilmIds = filmIds.stream()
                                             .sorted(Comparator.comparingInt((Long id) -> likeCounts.getOrDefault(id,
                                                                                                                  0))
                                                               .reversed())
                                             .limit(query.limit())
                                             .toList();

    if (sortedPopularFilmIds.isEmpty()) {
      return Collections.emptyList();
    }

    List<Film> popularFilms = filmUseCase.getFilmsByIds(sortedPopularFilmIds);
    return enrichFilmsWithDirectors(popularFilms);
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

    Set<Long> userLikes = likeService.findLikedFilms(userId);
    Set<Long> friendLikes = likeService.findLikedFilms(friendId);

    Set<Long> commonFilmIds = new HashSet<>(userLikes);
    commonFilmIds.retainAll(friendLikes);

    if (commonFilmIds.isEmpty()) {
      return List.of();
    }

    List<Film> commonFilms = filmUseCase.getFilmsByIds(commonFilmIds.stream()
                                                                    .toList());
    Map<Long, Integer> likeCounts = likeService.getLikeCountsForFilms(commonFilmIds);

    return commonFilms.stream()
                      .sorted(Comparator.comparingInt(film -> -likeCounts.getOrDefault(film.id(), 0)))
                      .toList();
  }

  public List<FilmWithDirectors> getDirectorFilms(long directorId, SortBy sortBy) {
    List<Long> filmIds = directorUseCase.getFilmIdsByDirector(directorId, sortBy);
    if (filmIds.isEmpty()) {
      return Collections.emptyList();
    }
    List<Film> films = filmUseCase.getFilmsByIds(filmIds);
    return enrichFilmsWithDirectors(films);
  }

  public User getUserOrThrow(long id) {
    return userUseCase.findUserById(id)
                      .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
  }

  public void deleteFilmById(long id) {
    filmUseCase.deleteFilmById(id);
  }

  public List<FilmWithDirectors> searchFilms(@RequestParam String query, @RequestParam List<String> by) {
    List<Long> filmIds = searchUseCase.searchFilms(query,
                                                   by.stream()
                                                     .map(String::toUpperCase)
                                                     .collect(Collectors.toSet()));

    if (filmIds.isEmpty()) {
      return Collections.emptyList();
    }

    return enrichFilmsWithDirectors(getFilmByIds(filmIds));
  }

  public List<Film> getFilmByIds(List<Long> ids) {
    return filmUseCase.getFilmsByIds(ids);
  }
}
