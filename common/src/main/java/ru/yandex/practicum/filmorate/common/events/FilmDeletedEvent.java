package ru.yandex.practicum.filmorate.common.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.Clock;

@Getter
public class FilmDeletedEvent extends ApplicationEvent {
  private final long filmId;

  public FilmDeletedEvent(Object source, long filmId) {
    super(source);
    this.filmId = filmId;
  }

  public FilmDeletedEvent(Object source, Clock clock, long filmId) {
    super(source,
          clock);
    this.filmId = filmId;
  }
}
