package ru.yandex.practicum.filmorate.events.domain.port;

import ru.yandex.practicum.filmorate.events.domain.model.value.EventType;
import ru.yandex.practicum.filmorate.events.domain.model.value.Operation;

/**
 * Command for creating a new event.
 *
 * @param userId     The ID of the user who performed the action
 * @param eventType  The type of event
 * @param operation  The operation performed
 * @param entityId   The ID of the entity involved
 */
public record CreateEventCommand(
        Long userId,
        EventType eventType,
        Operation operation,
        Long entityId
) {
    public CreateEventCommand {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID must be positive");
        }
        if (eventType == null) {
            throw new IllegalArgumentException("Event type must not be null");
        }
        if (operation == null) {
            throw new IllegalArgumentException("Operation must not be null");
        }
        if (entityId == null || entityId <= 0) {
            throw new IllegalArgumentException("Entity ID must be positive");
        }
    }
}