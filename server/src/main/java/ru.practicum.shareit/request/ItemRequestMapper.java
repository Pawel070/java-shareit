package ru.practicum.shareit.request;


import java.util.List;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.springframework.stereotype.Component;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemRequestMapper {

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(itemRequest.getId(), itemRequest.getDescription(), itemRequest.getRequester().getId(), itemRequest.getCreated());
    }

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User user) {
        return new ItemRequest(itemRequestDto.getId(), itemRequestDto.getDescription(),
                user, itemRequestDto.getCreated()
        );
    }

    public static ItemRequestInfoDto findAllByRequest_IdOrderByIdDesc(ItemRequest itemRequest, List<ItemDto> items) {
        return new ItemRequestInfoDto(itemRequest.getId(), itemRequest.getDescription(), itemRequest.getCreated(), items);
    }
}
