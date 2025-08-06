package ru.yandex.practicum.filmorate.events.domain.model;

import lombok.Getter;
import ru.yandex.practicum.filmorate.events.domain.model.value.EventType;
import ru.yandex.practicum.filmorate.events.domain.model.value.Operation;

@Getter
public abstract class DomainEvent {
    private final Long userId;
    private final EventType eventType;
    private final Operation operation;
    private final Long entityId;
    private final Long timestamp;

    protected DomainEvent(Long userId, EventType eventType, Operation operation, Long entityId) {
        this.userId = userId;
        this.eventType = eventType;
        this.operation = operation;
        this.entityId = entityId;
        this.timestamp = System.currentTimeMillis();
    }

}