package ru.yandex.practicum.filmorate.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.events.application.port.in.EventUseCase;
import ru.yandex.practicum.filmorate.infrastructure.web.dto.EventResponse;
import ru.yandex.practicum.filmorate.infrastructure.web.mapper.EventMapper;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class EventController {

    private final EventUseCase eventUseCase;
    private final EventMapper eventResponseMapper;

    @GetMapping("/{userId}/feed")
    public List<EventResponse> getUserFeed(@PathVariable("userId") Long userId) {
        return eventResponseMapper.toResponseList(eventUseCase.getUserFeed(userId));
    }
}