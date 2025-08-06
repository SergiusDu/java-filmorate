package ru.yandex.practicum.filmorate.directors.domain.port;

public record UpdateDirectorCommand(long id,
                                    String name) {
}
