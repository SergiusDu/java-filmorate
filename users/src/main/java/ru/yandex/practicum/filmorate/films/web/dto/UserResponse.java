package ru.yandex.practicum.filmorate.films.web.dto;

import java.time.LocalDate;

public record UserResponse(Integer id,
                           String email,
                           String login,
                           String name,
                           LocalDate birthday) {}
