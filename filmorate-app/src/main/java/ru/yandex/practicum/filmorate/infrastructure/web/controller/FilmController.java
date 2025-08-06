package ru.yandex.practicum.filmorate.infrastructure.web.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.films.application.port.in.FilmRatingQuery;
import ru.yandex.practicum.filmorate.films.application.port.in.RecommendationQuery;
import ru.yandex.practicum.filmorate.infrastructure.web.dto.CreateFilmRequest;
import ru.yandex.practicum.filmorate.infrastructure.web.dto.FilmResponse;
import ru.yandex.practicum.filmorate.infrastructure.web.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.infrastructure.web.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.service.FilmCompositionService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
@Validated
public class FilmController {

    private final FilmCompositionService filmCompositionService;
    private final FilmMapper filmMapper;

    @GetMapping
    public List<FilmResponse> getAllFilms() {
        return filmCompositionService.getAllFilms().stream()
                .map(filmMapper::toResponse)
                .toList();
    }

    @PostMapping
    public FilmResponse createFilm(@Valid @RequestBody CreateFilmRequest request) {
        return filmMapper.toResponse(
                filmCompositionService.createFilm(filmMapper.toCommand(request))
        );
    }

    @PutMapping
    public FilmResponse updateFilm(@Valid @RequestBody UpdateFilmRequest request) {
        return filmMapper.toResponse(
                filmCompositionService.updateFilm(filmMapper.toCommand(request))
        );
    }

    @PutMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void likeFilm(@PathVariable long id, @PathVariable long userId) {
        filmCompositionService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteLike(@PathVariable long id, @PathVariable long userId) {
        filmCompositionService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<FilmResponse> getPopularFilmsWithFilters(
            @RequestParam(defaultValue = "10") @Min(1) @Max(1000) Integer count,
            @RequestParam(required = false) Long genreId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Long directorId,
            @RequestParam(defaultValue = "LIKES") FilmRatingQuery.SortBy sortBy
    ) {
        var query = FilmRatingQuery.of(count, genreId, year, directorId, sortBy);
        return filmCompositionService.getPopularFilms(query).stream()
                .map(filmMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public FilmResponse getFilmById(@PathVariable long id) {
        return filmMapper.toResponse(filmCompositionService.getFilmById(id));
    }

    @GetMapping("/common")
    public List<FilmResponse> getCommonFilms(@RequestParam long userId,
                                             @RequestParam long friendId) {
        return filmCompositionService.getCommonFilms(userId, friendId).stream()
                .map(filmMapper::toResponse)
                .toList();
    }

    @GetMapping("/recommendations")
    public List<FilmResponse> getRecommendations(@RequestParam long userId,
                                                 @RequestParam(required = false) Long genreId,
                                                 @RequestParam(required = false) Integer year,
                                                 @RequestParam(defaultValue = "10") @Positive Integer limit) {
        var query = new RecommendationQuery(
                userId,
                Optional.ofNullable(limit),
                Optional.ofNullable(genreId),
                Optional.ofNullable(year)
        );
        return filmCompositionService.getRecommendations(query).stream()
                .map(filmMapper::toResponse)
                .toList();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFilm(@PathVariable long id) {
        filmCompositionService.deleteFilmById(id);
    }
}