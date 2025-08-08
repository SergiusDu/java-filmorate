package ru.yandex.practicum.filmorate.infrastructure.web.dto;

import lombok.Builder;
import ru.yandex.practicum.filmorate.events.domain.model.value.EventType;
import ru.yandex.practicum.filmorate.events.domain.model.value.Operation;

@Builder
public record EventResponse(Long timestamp,
                            Long userId,
                            EventType eventType,
                            Operation operation,
                            Long eventId,
                            Long entityId) {
}
