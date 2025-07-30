package ru.yandex.practicum.filmorate.infrastructure.web.dto;

import lombok.Builder;

@Builder
public record ReviewResponse(Long reviewId,
                            String content,
                            boolean isPositive,
                            Integer useful,
                            Long userId,
                            Long filmId ) {}
