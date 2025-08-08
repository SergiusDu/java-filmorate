package ru.yandex.practicum.filmorate.events.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.events.domain.model.DomainEvent;
import ru.yandex.practicum.filmorate.events.domain.model.value.EventType;
import ru.yandex.practicum.filmorate.events.domain.model.value.Operation;

@Component
@RequiredArgsConstructor
public class DomainEventPublisher {

  private final ApplicationEventPublisher eventPublisher;

  public void publishLikeEvent(Long userId, Operation operation, Long filmId) {
    LikeEvent event = new LikeEvent(userId, operation, filmId);
    eventPublisher.publishEvent(event);
  }

  public void publishFriendEvent(Long userId, Operation operation, Long friendId) {
    FriendEvent event = new FriendEvent(userId, operation, friendId);
    eventPublisher.publishEvent(event);
  }

  public void publishReviewEvent(Long userId, Operation operation, Long reviewId) {
    ReviewEvent event = new ReviewEvent(userId, operation, reviewId);
    eventPublisher.publishEvent(event);
  }

  public static class LikeEvent extends DomainEvent {
    public LikeEvent(Long userId, Operation operation, Long filmId) {
      super(userId, EventType.LIKE, operation, filmId);
    }
  }

  public static class FriendEvent extends DomainEvent {
    public FriendEvent(Long userId, Operation operation, Long friendId) {
      super(userId, EventType.FRIEND, operation, friendId);
    }
  }

  public static class ReviewEvent extends DomainEvent {
    public ReviewEvent(Long userId, Operation operation, Long reviewId) {
      super(userId, EventType.REVIEW, operation, reviewId);
    }
  }
}