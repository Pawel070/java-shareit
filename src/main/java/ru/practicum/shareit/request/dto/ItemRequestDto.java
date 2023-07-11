package ru.practicum.shareit.request.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ItemRequestDto {
    private final Long id;
    private String description;
    private String requester;
    private LocalDateTime created;
}

