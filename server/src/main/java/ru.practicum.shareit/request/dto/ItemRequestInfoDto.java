package ru.practicum.shareit.request.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.model.User;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
public class ItemRequestInfoDto {
    private Long id;
    private String description;
//    private User requestor; //создатель
    private LocalDateTime created;
    private List<ItemDto> items;

}