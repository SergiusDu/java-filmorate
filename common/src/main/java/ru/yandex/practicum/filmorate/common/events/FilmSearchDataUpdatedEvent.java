package ru.yandex.practicum.filmorate.common.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.Set;

@Getter
public class FilmSearchDataUpdatedEvent
    extends ApplicationEvent {
  private final long filmId;
  private final String title;
  private final Set<String> directors;

  public FilmSearchDataUpdatedEvent(Object source, long filmId, String title, Set<String> directors) {
    super(source);
    this.filmId = filmId;
    this.title = title;
    this.directors = directors;
  }
}
