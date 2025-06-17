package ru.yandex.practicum.filmorate.infrastructure.web.exception;

import java.util.Map;

public record ValidationErrorResponse(Map<String, String> errors) {}
