package ru.yandex.practicum.filmorate.films.domain.port;

public record UpdateGenreCommand(long id,
                                 String name) {}
