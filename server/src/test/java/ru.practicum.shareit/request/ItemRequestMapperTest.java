package ru.practicum.shareit.request;


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

public class ItemRequestMapperTest {

    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;
    private ItemRequestInfoDto itemRequestInfoDto;
    private User user;
    private ItemDto itemDto;
    private ItemDto itemDto2;
    List<ItemDto> itemDtos = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        user = new User(1L, "user1", "user1@mail.ru");
        itemRequest = new ItemRequest(1L, "Khren", user, LocalDateTime.now());
        itemRequestDto = new ItemRequestDto(1L, "Khren",1L, LocalDateTime.now());
        itemDto = new ItemDto(1L, "Khren", "Description", true, null, 1L);
        itemDto2 = new ItemDto(2L, "Rediska", "BU", true, null, 1L);
        itemDtos.add(itemDto);
        itemDtos.add(itemDto2);
        itemRequestInfoDto = new ItemRequestInfoDto(1L, "Khren", LocalDateTime.now(), itemDtos);
    }

    @Test
    public void toItemRequestDto() {
        ItemRequestDto requestDto = ItemRequestMapper.toItemRequestDto(itemRequest);

        assertEquals(requestDto.getId(), itemRequest.getId());
        assertEquals(requestDto.getDescription(), itemRequest.getDescription());
    }

    @Test
    public void toItemRequest() {
        ItemRequest request = ItemRequestMapper.toItemRequest(itemRequestDto, user);

        assertEquals(request.getId(), itemRequestDto.getId());
        assertEquals(request.getDescription(), itemRequestDto.getDescription());
    }

    @Test
    public void toItemRequestWithListItemsDtoList() {
        ItemRequestInfoDto requestWithItemsDto =
                ItemRequestMapper.findAllByRequest_IdOrderByIdDesc(itemRequest, itemDtos);

        assertEquals(requestWithItemsDto.getId(), itemRequestInfoDto.getId());
        assertEquals(requestWithItemsDto.getItems(), itemRequestInfoDto.getItems());
    }
}

