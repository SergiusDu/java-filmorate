package ru.yandex.practicum.filmorate.search.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.search.application.port.in.SearchUseCase;
import ru.yandex.practicum.filmorate.search.domain.port.SearchRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService
    implements SearchUseCase {
  private final SearchRepository searchRepository;

  @Override
  public List<Long> searchFilms(String query, Set<String> by) {
    Set<String> searchBy = by.stream()
                             .map(String::toUpperCase)
                             .collect(Collectors.toSet());
    return searchRepository.search(query.toLowerCase(), searchBy);
  }
}
