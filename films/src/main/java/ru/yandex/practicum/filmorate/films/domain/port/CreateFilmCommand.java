package ru.yandex.practicum.filmorate.films.domain.port;

import lombok.Builder;
import ru.yandex.practicum.filmorate.films.domain.model.value.Genre;

import java.time.LocalDate;
import java.util.Set;

@Builder
public record CreateFilmCommand(String name,
                                String description,
                                LocalDate releaseDate,
                                Long duration,
                                Set<Genre> genres,
                                Long mpa,
                                Set<Long> directorIds) {
}
