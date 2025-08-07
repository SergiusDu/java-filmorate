package ru.yandex.practicum.filmorate.search.domain.model;

public record SearchableFilm(long filmId,
                             String title,
                             String directorNames) {
}
