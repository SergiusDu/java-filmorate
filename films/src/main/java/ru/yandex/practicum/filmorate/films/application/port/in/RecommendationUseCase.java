package ru.yandex.practicum.filmorate.films.application.port.in;

import ru.yandex.practicum.filmorate.films.domain.model.Film;

import java.util.List;

public interface RecommendationUseCase {

    /**
     * Generates personalized film recommendations using collaborative filtering.
     *
     * @param query recommendation parameters including userId, limit, genre, year
     * @return list of recommended films
     */
    List<Film> getRecommendations(RecommendationQuery query);
}