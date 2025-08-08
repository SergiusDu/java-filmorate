package ru.yandex.practicum.filmorate.events.domain.model;

import lombok.Builder;
import ru.yandex.practicum.filmorate.common.exception.InvalidEventDataException;
import ru.yandex.practicum.filmorate.common.validation.ValidationUtils;
import ru.yandex.practicum.filmorate.events.domain.model.value.EventType;
import ru.yandex.practicum.filmorate.events.domain.model.value.Operation;

import java.time.Instant;

/**
 * Represents an event that occurred on the platform.
 * Events are immutable records of user actions like adding friends, liking films, etc.
 *
 * @param eventId    The unique identifier for the event
 * @param timestamp  The Unix timestamp when the event occurred
 * @param userId     The ID of the user who performed the action
 * @param eventType  The type of event (LIKE, REVIEW, FRIEND)
 * @param operation  The operation performed (ADD, REMOVE, UPDATE)
 * @param entityId   The ID of the entity involved in the event
 */
@Builder
public record Event(
        Long eventId,
        Long timestamp,
        Long userId,
        EventType eventType,
        Operation operation,
        Long entityId
) {

  /**
   * Validates all fields during record construction.
   *
   * @throws InvalidEventDataException if any required field is null or invalid
   */
  public Event {
    ValidationUtils.notNull(timestamp, msg -> new InvalidEventDataException("Event timestamp must not be null"));
    ValidationUtils.notNull(userId, msg -> new InvalidEventDataException("Event userId must not be null"));
    ValidationUtils.notNull(eventType, msg -> new InvalidEventDataException("Event type must not be null"));
    ValidationUtils.notNull(operation, msg -> new InvalidEventDataException("Event operation must not be null"));
    ValidationUtils.notNull(entityId, msg -> new InvalidEventDataException("Event entityId must not be null"));

    ValidationUtils.positive(userId, msg -> new InvalidEventDataException("User ID must be positive"));
    ValidationUtils.positive(entityId, msg -> new InvalidEventDataException("Entity ID must be positive"));

    if (timestamp <= 0) {
      throw new InvalidEventDataException("Timestamp must be positive");
    }
  }

  /**
   * Creates a new Event with current timestamp.
   *
   * @param userId     The ID of the user who performed the action
   * @param eventType  The type of event
   * @param operation  The operation performed
   * @param entityId   The ID of the entity involved
   * @return A new Event instance
   */
  public static Event create(Long userId, EventType eventType, Operation operation, Long entityId) {
    return Event.builder()
            .timestamp(Instant.now().toEpochMilli())
            .userId(userId)
            .eventType(eventType)
            .operation(operation)
            .entityId(entityId)
            .build();
  }
}