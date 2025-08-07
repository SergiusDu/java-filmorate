package ru.yandex.practicum.filmorate.search.infrastructure.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.common.events.FilmSearchDataUpdatedEvent;
import ru.yandex.practicum.filmorate.search.application.port.out.SearchRepository;
import ru.yandex.practicum.filmorate.search.domain.model.SearchableFilm;

@Slf4j
@Component
@RequiredArgsConstructor
public class FilmEventsListener {

  private final SearchRepository searchRepository;

  @EventListener
  public void handleFilmSearchDataUpdate(FilmSearchDataUpdatedEvent event) {
    log.info("Received event to update search index for filmId: {}", event.getFilmId());
    try {
      SearchableFilm searchableFilm = new SearchableFilm(event.getFilmId(),
                                                         event.getTitle()
                                                              .toLowerCase(),
                                                         String.join(" ", event.getDirectors())
                                                               .toLowerCase());
      searchRepository.save(searchableFilm);
      log.info("Successfully updated search index for filmId: {}", event.getFilmId());
    } catch (Exception e) {
      log.error("Failed to update search index for filmId: {}", event.getFilmId(), e);
    }
  }
}