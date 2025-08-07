package ru.yandex.practicum.filmorate.events.application.port.in;

import ru.yandex.practicum.filmorate.events.domain.model.Event;
import ru.yandex.practicum.filmorate.events.domain.model.value.EventType;
import ru.yandex.practicum.filmorate.events.domain.model.value.Operation;

import java.util.List;

public interface EventUseCase {

  Event recordEvent(Long userId, EventType eventType, Operation operation, Long entityId);

  List<Event> getUserFeed(long userId);
}