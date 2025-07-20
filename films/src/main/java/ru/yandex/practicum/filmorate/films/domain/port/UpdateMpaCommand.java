package ru.yandex.practicum.filmorate.films.domain.port;

public record UpdateMpaCommand(long id,
                               String name) {}
