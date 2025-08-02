package ru.yandex.practicum.filmorate.users.domain.port;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record CreateUserCommand(String email,
                                String login,
                                String name,
                                LocalDate birthday) {}
