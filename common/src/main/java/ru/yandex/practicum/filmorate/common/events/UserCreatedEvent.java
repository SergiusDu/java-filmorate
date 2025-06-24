package ru.yandex.practicum.filmorate.common.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.Clock;

@Getter
public class UserCreatedEvent extends ApplicationEvent {
  private final long userId;

  public UserCreatedEvent(Object source, long userId) {
    super(source);
    this.userId = userId;
  }

  public UserCreatedEvent(Object source, Clock clock, long userId) {
    super(source,
          clock);
    this.userId = userId;
  }
}
