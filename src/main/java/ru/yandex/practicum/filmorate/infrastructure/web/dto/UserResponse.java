package ru.yandex.practicum.filmorate.infrastructure.web.dto;

import java.time.LocalDate;
import java.util.UUID;

public record UserResponse(UUID id,
                           String email,
                           String login,
                           String name,
                           LocalDate birthday) {}
