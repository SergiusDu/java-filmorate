package ru.yandex.practicum.filmorate.films.domain.port;

import java.time.LocalDate;

public record CreateUserCommand(String email,
                                String login,
                                String name,
                                LocalDate birthday) {}
