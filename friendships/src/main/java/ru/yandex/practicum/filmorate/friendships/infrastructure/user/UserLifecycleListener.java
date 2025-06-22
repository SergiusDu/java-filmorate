package ru.yandex.practicum.filmorate.friendships.infrastructure.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.common.events.UserCreatedEvent;
import ru.yandex.practicum.filmorate.friendships.application.port.in.FriendshipsUseCase;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserLifecycleListener {
  private final FriendshipsUseCase friendshipsService;

  @Async
  @EventListener(UserCreatedEvent.class)
  public void addUser(UserCreatedEvent event) {
    log.info("Handling {}: Adding user {} to friendship graph",
             event.getClass()
                  .getSimpleName(),
             event.getUserId());
    friendshipsService.addUser(event.getUserId());
  }
}