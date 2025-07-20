package ru.yandex.practicum.filmorate.friendships.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jgrapht.graph.DefaultEdge;
import ru.yandex.practicum.filmorate.friendships.domain.model.value.FriendshipStatus;

@Getter
@RequiredArgsConstructor
public class FriendshipEdge extends DefaultEdge {
  private final FriendshipStatus status;

  @Override
  public String toString() {
    return "(" + getSource() + " : " + getTarget() + " : " + status + ")";
  }
}
