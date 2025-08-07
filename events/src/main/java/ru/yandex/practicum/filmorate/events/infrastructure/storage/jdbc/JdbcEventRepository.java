package ru.yandex.practicum.filmorate.events.infrastructure.storage.jdbc;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.events.domain.factory.EventFactory;
import ru.yandex.practicum.filmorate.events.domain.model.Event;
import ru.yandex.practicum.filmorate.events.domain.model.value.EventType;
import ru.yandex.practicum.filmorate.events.domain.model.value.Operation;
import ru.yandex.practicum.filmorate.events.domain.port.CreateEventCommand;
import ru.yandex.practicum.filmorate.events.domain.port.EventRepository;

import java.util.List;
import java.util.Map;

@Repository
@Profile("db")
@RequiredArgsConstructor
public class JdbcEventRepository implements EventRepository {

    private static final String FIND_BY_USER_ID_QUERY = """
            SELECT event_id, timestamp, user_id, event_type, operation, entity_id
            FROM events
            WHERE user_id = ?
            ORDER BY timestamp DESC
            LIMIT 7
            """;

    private static final String FIND_FRIENDS_EVENTS_QUERY = """
            SELECT e.event_id, e.timestamp, e.user_id, e.event_type, e.operation, e.entity_id
            FROM events e
            WHERE e.user_id = ?
            ORDER BY e.timestamp ASC
            """;

    private final JdbcTemplate jdbcTemplate;
    private final EventFactory eventFactory;

    @Override
    public Event save(CreateEventCommand command) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("events").usingGeneratedKeyColumns("event_id");

        Map<String, Object> params = Map.of("timestamp", System.currentTimeMillis(), "user_id", command.userId(), "event_type", command.eventType().name(), "operation", command.operation().name(), "entity_id", command.entityId());

        long eventId = simpleJdbcInsert.executeAndReturnKey(params).longValue();
        return eventFactory.create(eventId, command);
    }

    @Override
    public List<Event> findByUserId(long userId) {
        return jdbcTemplate.query(FIND_BY_USER_ID_QUERY, this::mapRowToEvent, userId);
    }

    @Override
    public List<Event> findFriendsEvents(long userId) {
        return jdbcTemplate.query(FIND_FRIENDS_EVENTS_QUERY, this::mapRowToEvent, userId);
    }

    private Event mapRowToEvent(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        return Event.builder()
                .eventId(rs.getLong("event_id"))
                .timestamp(rs.getLong("timestamp"))
                .userId(rs.getLong("user_id"))
                .eventType(EventType.valueOf(rs.getString("event_type")))
                .operation(Operation.valueOf(rs.getString("operation")))
                .entityId(rs.getLong("entity_id"))
                .build();
    }
}