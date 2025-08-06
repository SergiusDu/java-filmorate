package ru.yandex.practicum.filmorate.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.infrastructure.web.dto.EventResponse;
import ru.yandex.practicum.filmorate.events.domain.model.Event;

import java.util.List;

@Component
public class EventMapper {

    public EventResponse toResponse(Event event) {
        if (event == null) {
            return null;
        }

        return EventResponse.builder()
                .timestamp(event.timestamp())
                .userId(event.userId())
                .eventType(event.eventType())
                .operation(event.operation())
                .eventId(event.eventId())
                .entityId(event.entityId())
                .build();
    }

    public List<EventResponse> toResponseList(List<Event> events) {
        if (events == null) {
            return List.of();
        }

        return events.stream()
                .map(this::toResponse)
                .toList();
    }
}