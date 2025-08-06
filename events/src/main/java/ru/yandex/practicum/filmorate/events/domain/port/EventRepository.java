package ru.yandex.practicum.filmorate.events.domain.port;

import ru.yandex.practicum.filmorate.events.domain.model.Event;

import java.util.List;

public interface EventRepository {

    /**
     * Saves a new event to the repository.
     *
     * @param command The event creation command
     * @return The saved event with generated ID
     */
    Event save(CreateEventCommand command);

    /**
     * Retrieves all events for a specific user, ordered by timestamp descending.
     *
     * @param userId The ID of the user
     * @return List of events for the user
     */
    List<Event> findByUserId(long userId);

    /**
     * Retrieves events for all friends of a specific user, ordered by timestamp descending.
     * This method should join with friendship data to get events from user's friends only.
     *
     * @param userId The ID of the user whose friends' events should be retrieved
     * @return List of events from user's friends
     */
    List<Event> findFriendsEvents(long userId);
}