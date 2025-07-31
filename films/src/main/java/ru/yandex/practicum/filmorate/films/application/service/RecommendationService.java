package ru.yandex.practicum.filmorate.films.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.films.application.port.in.FilmUseCase;
import ru.yandex.practicum.filmorate.films.application.port.in.RecommendationUseCase;
import ru.yandex.practicum.filmorate.films.application.port.in.RecommendationQuery;
import ru.yandex.practicum.filmorate.films.domain.model.Film;
import ru.yandex.practicum.filmorate.likes.application.port.in.LikeUseCase;
import java.util.*;


@Service
@RequiredArgsConstructor
public class RecommendationService implements RecommendationUseCase {

    private final LikeUseCase likeUseCase;
    private final FilmUseCase filmUseCase;

    /**
     * Generates a list of recommended films for a user using Jaccard similarity.
     * Applies optional filtering by genre, release year, and limit.
     *
     * @param query recommendation criteria (user ID, genre, year, limit)
     * @return list of recommended films
     */
    @Override
    public List<Film> getRecommendations(RecommendationQuery query) {
        Set<Long> targetLikes = likeUseCase.findLikedFilms(query.userId());
        if (targetLikes.isEmpty()) {
            return List.of();
        }

        Map<Long, Set<Long>> allUserLikes = likeUseCase.findAllUserFilmLikes();
        allUserLikes.remove(query.userId());

        Optional<Long> mostSimilarUserId = allUserLikes.entrySet().stream()
                .max(Comparator.comparingDouble(entry -> jaccard(targetLikes, entry.getValue())))
                .map(Map.Entry::getKey);

        if (mostSimilarUserId.isEmpty()) {
            return List.of();
        }

        Set<Long> similarUserLikes = allUserLikes.get(mostSimilarUserId.get());
        Set<Long> recommendedIds = new HashSet<>(similarUserLikes);
        recommendedIds.removeAll(targetLikes);

        List<Film> candidates = filmUseCase.getFilmsByIds(recommendedIds);

        return candidates.stream()
                .filter(film -> !film.isDeleted()) // Exclude soft-deleted films
                .filter(film -> query.genreId().isEmpty() ||
                        film.genres().stream().anyMatch(g -> query.genreId().get().equals(g.id())))
                .filter(film -> query.year().isEmpty() ||
                        film.releaseDate().getYear() == query.year().get())
                .limit(query.limit().orElse(candidates.size()))
                .toList();
    }

    private double jaccard(Set<Long> a, Set<Long> b) {
        if (a.isEmpty() && b.isEmpty()) return 0.0;

        Set<Long> intersection = new HashSet<>(a);
        intersection.retainAll(b);

        Set<Long> union = new HashSet<>(a);
        union.addAll(b);

        return (double) intersection.size() / union.size();
    }
}
