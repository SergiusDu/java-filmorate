package ru.yandex.practicum.filmorate.events.infrastructure.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.events.domain.model.DomainEvent;
import ru.yandex.practicum.filmorate.events.domain.port.CreateEventCommand;
import ru.yandex.practicum.filmorate.events.domain.port.EventRepository;
import ru.yandex.practicum.filmorate.events.domain.service.DomainEventPublisher;

@Slf4j
@Component
@RequiredArgsConstructor
public class DomainEventListener {

    private final EventRepository eventRepository;

    /**
     * Handles like events and persists them.
     *
     * @param event The like event
     */
    @EventListener
    public void handleLikeEvent(DomainEventPublisher.LikeEvent event) {
        log.debug("Handling like event: userId={}, operation={}, filmId={}",
                event.getUserId(), event.getOperation(), event.getEntityId());

        persistEvent(event);
    }

    /**
     * Handles friend events and persists them.
     *
     * @param event The friend event
     */
    @EventListener
    public void handleFriendEvent(DomainEventPublisher.FriendEvent event) {
        log.debug("Handling friend event: userId={}, operation={}, friendId={}",
                event.getUserId(), event.getOperation(), event.getEntityId());

        persistEvent(event);
    }

    /**
     * Handles review events and persists them.
     *
     * @param event The review event
     */
    @EventListener
    public void handleReviewEvent(DomainEventPublisher.ReviewEvent event) {
        log.debug("Handling review event: userId={}, operation={}, reviewId={}",
                event.getUserId(), event.getOperation(), event.getEntityId());

        persistEvent(event);
    }

    /**
     * Persists the domain event to the event repository.
     *
     * @param event The domain event to persist
     */
    private void persistEvent(DomainEvent event) {
        try {
            CreateEventCommand command = new CreateEventCommand(
                    event.getUserId(),
                    event.getEventType(),
                    event.getOperation(),
                    event.getEntityId()
            );

            eventRepository.save(command);

            log.info("Event persisted successfully: userId={}, eventType={}, operation={}, entityId={}",
                    event.getUserId(), event.getEventType(), event.getOperation(), event.getEntityId());
        } catch (Exception e) {
            log.error("Failed to persist event: userId={}, eventType={}, operation={}, entityId={}",
                    event.getUserId(), event.getEventType(), event.getOperation(), event.getEntityId(), e);
        }
    }
}