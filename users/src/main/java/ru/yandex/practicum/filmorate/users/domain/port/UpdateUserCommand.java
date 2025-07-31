package ru.yandex.practicum.filmorate.users.domain.port;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record UpdateUserCommand(Long id,
                                String email,
                                String login,
                                String name,
                                LocalDate birthday) {}
