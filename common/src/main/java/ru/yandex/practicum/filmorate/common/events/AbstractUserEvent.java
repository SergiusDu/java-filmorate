package ru.yandex.practicum.filmorate.common.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.Clock;

@Getter
abstract class AbstractUserEvent extends ApplicationEvent {
  private final Long userId;

  protected AbstractUserEvent(Object source, Long userId) {
    super(source);
    this.userId = userId;
  }

  protected AbstractUserEvent(Object source, Clock clock, Long userId) {
    super(source,
          clock);
    this.userId = userId;
  }
}
