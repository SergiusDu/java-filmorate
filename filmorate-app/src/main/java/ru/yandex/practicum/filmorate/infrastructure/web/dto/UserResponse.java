package ru.yandex.practicum.filmorate.infrastructure.web.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record UserResponse(Long id,
                           String email,
                           String login,
                           String name,
                           LocalDate birthday) {}
