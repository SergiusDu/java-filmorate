package ru.yandex.practicum.filmorate.films.web.dto;

import java.time.LocalDate;

public record UserResponse(Long id,
                           String email,
                           String login,
                           String name,
                           LocalDate birthday) {}
