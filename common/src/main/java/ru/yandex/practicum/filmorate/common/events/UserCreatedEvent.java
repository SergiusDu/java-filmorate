package ru.yandex.practicum.filmorate.common.events;

import java.time.Clock;

public class UserCreatedEvent extends AbstractUserEvent {
  public UserCreatedEvent(Object source, Long userId) {
    super(source,
          userId);
  }

  public UserCreatedEvent(Object source, Clock clock, Long userId) {
    super(source,
          clock,
          userId);
  }
}
