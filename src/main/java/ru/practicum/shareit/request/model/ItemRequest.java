package ru.practicum.shareit.request.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ItemRequest {

    private final Long id; // УИН запроса

    private final String description; // что просим

    private final String requester; // кто отправил запрос

    private final LocalDateTime created; // дата и времени нового запроса

}

