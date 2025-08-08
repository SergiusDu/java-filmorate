
package ru.yandex.practicum.filmorate.events.domain.factory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.events.domain.model.Event;
import ru.yandex.practicum.filmorate.events.domain.port.CreateEventCommand;

@Component
public class EventFactory {

  /**
   * Creates a new Event from a command with generated ID.
   * Uses the timestamp from the repository save operation for consistency.
   *
   * @param eventId The generated event ID
   * @param command The creation command
   * @return A new Event instance
   */
  public Event create(Long eventId, CreateEventCommand command) {
    return Event.builder()
            .eventId(eventId)
            .timestamp(System.currentTimeMillis())
            .userId(command.userId())
            .eventType(command.eventType())
            .operation(command.operation())
            .entityId(command.entityId())
            .build();
  }
}