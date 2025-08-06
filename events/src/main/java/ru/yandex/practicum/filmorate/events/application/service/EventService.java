package ru.yandex.practicum.filmorate.events.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.events.application.port.in.EventUseCase;
import ru.yandex.practicum.filmorate.events.domain.model.Event;
import ru.yandex.practicum.filmorate.events.domain.model.value.EventType;
import ru.yandex.practicum.filmorate.events.domain.model.value.Operation;
import ru.yandex.practicum.filmorate.events.domain.port.CreateEventCommand;
import ru.yandex.practicum.filmorate.events.domain.port.EventRepository;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService implements EventUseCase {

    private final EventRepository eventRepository;

    @Override
    public Event recordEvent(Long userId, EventType eventType, Operation operation, Long entityId) {
        log.debug("Recording event: userId={}, eventType={}, operation={}, entityId={}", userId, eventType, operation, entityId);
        CreateEventCommand command = new CreateEventCommand(userId, eventType, operation, entityId);
        Event savedEvent = eventRepository.save(command);
        log.info("Event recorded successfully: eventId={}, userId={}, eventType={}, operation={}", savedEvent.eventId(), userId, eventType, operation);
        return savedEvent;
    }

    @Override
    public List<Event> getUserFeed(long userId) {
        log.debug("Retrieving feed for user: {}", userId);
        List<Event> feed = eventRepository.findByUserId(userId);
        Collections.reverse(feed);
        log.info("Retrieved {} events for user feed: {}", feed.size(), userId);
        return feed;
    }
}