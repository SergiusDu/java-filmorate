package ru.yandex.practicum.filmorate.films.web.dto;

import java.time.LocalDate;
import java.util.UUID;

public record UserResponse(UUID id,
                           String email,
                           String login,
                           String name,
                           LocalDate birthday) {}
