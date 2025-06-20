package ru.yandex.practicum.filmorate.films.domain.port;

import java.time.LocalDate;

public record UpdateUserCommand(Long id,
                                String email,
                                String login,
                                String name,
                                LocalDate birthday) {}
