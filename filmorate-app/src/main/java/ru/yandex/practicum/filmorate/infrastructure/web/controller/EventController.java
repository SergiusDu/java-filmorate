package ru.yandex.practicum.filmorate.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.infrastructure.web.dto.EventResponse;
import ru.yandex.practicum.filmorate.infrastructure.web.mapper.EventMapper;
import ru.yandex.practicum.filmorate.events.application.port.in.EventUseCase;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class EventController {

    private final EventUseCase eventUseCase;
    private final EventMapper eventResponseMapper;

    @GetMapping("/{userId}/feed")
    public ResponseEntity<List<EventResponse>> getUserFeed(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(eventResponseMapper.toResponseList(eventUseCase.getUserFeed(userId)));
    }
}