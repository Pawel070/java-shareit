package ru.practicum.shareit.item.dto;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

@Slf4j
@Transactional
@SpringBootTest
//@RequiredArgsConstructor
class ItemMapperTest {

    ItemRequest itemRequest;
    ItemRequestDto itemRequestDto;

    @MockBean
    ItemMapper mapper;

    User user;
    Item item;
    ItemDto itemDto;

    @BeforeEach
    void beforeEach() {
        user = new User(1L, "name", "user@mail.ru");
        itemRequest = new ItemRequest(
                1L,
                "d2",
                user,
                LocalDateTime.of(2022, 12, 12, 12, 12, 12));
        item = new Item(1L, "item", "d1", true, user, itemRequest);
        itemDto = new ItemDto(1L, "item", "dd1", true, user, 1L);
        itemRequestDto = new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                user.getId(),
                itemRequest.getCreated());
    }

    @Test
    void toItemRequestTest() {
        when(mapper.toItemRequest(any())).thenReturn(itemRequest);
        log.info("itemRequest > {} , itemRequestDto > {} ", itemRequest, itemRequestDto);
        //   ItemRequest res = mapper.toItemRequest(itemRequestDto);
        assertNotNull(mapper.toItemRequest(itemRequestDto));
        assertEquals(ItemRequest.class, mapper.toItemRequest(itemRequestDto).getClass());
        assertEquals(itemRequestDto.getId(), mapper.toItemRequest(itemRequestDto).getId());
        assertEquals(itemRequestDto.getDescription(), mapper.toItemRequest(itemRequestDto).getDescription());
        assertEquals(itemRequestDto.getCreated(), mapper.toItemRequest(itemRequestDto).getCreated());
    }

    @Test
    void updatedItemTest() {
        when(mapper.updatedItem(any(), any())).thenReturn(item);
        Item item1 = mapper.updatedItem(itemDto, item);
        log.info("itemRequestDto => {} ", item1);
        assertNotNull(item1);
        assertThat(item1).hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    void toItemRequestNullTest() {
        //when(mapper.toItemRequest(any())).thenReturn(itemRequest);
        log.info("itemRequestDto > {} при null > ", itemRequestDto, mapper.toItemRequest(null));
        assertNull(mapper.toItemRequest(null));
    }

    @Test
    void toItemDtoNullTest() {
        //when(mapper.toItemDto(any())).thenReturn(item);
        log.info("item > {} при null > ", itemDto, mapper.toItemDto(null));
        assertNull(mapper.toItemDto(null));
    }

    @Test
    void mapToItemDtoResponseTest() {
        when(mapper.mapToItemDtoResponse(any())).thenReturn(itemDto);
        assertNotNull(mapper.mapToItemDtoResponse(item));
        assertEquals(ItemDto.class, mapper.mapToItemDtoResponse(item).getClass());
        assertEquals(itemDto.getId(), mapper.mapToItemDtoResponse(item).getId());
        assertEquals(itemDto.getDescription(), mapper.mapToItemDtoResponse(item).getDescription());
    }

    @Test
    void mapToItemFromItemDtoTest() {
        when(mapper.mapToItemFromItemDto(any())).thenReturn(item);
        assertNotNull(mapper.mapToItemFromItemDto(itemDto));
        item = new Item(1L, "item", "d1", true, user, itemRequest);
        itemDto = new ItemDto(1L, "item", "dd1", true, user, 1L);
        assertEquals(Item.class, mapper.mapToItemFromItemDto(itemDto).getClass());
        assertEquals(itemDto.getId(), mapper.mapToItemFromItemDto(itemDto).getId());
        assertEquals(item.getDescription(), mapper.mapToItemFromItemDto(itemDto).getDescription());
    }

    @Test
    void toItemExtDtoTest() {
        item.setId(100L);
        assertThrows(NullPointerException.class,
                () -> mapper.toItemExtDto(item).getClass());
    }

    @Test
    void mapToItemDtoItemTest() {
        item.setId(100L);
        log.info("item > {} ", item);
        itemDto = mapper.mapToItemDtoResponse(item);
        item = mapper.mapToItemFromItemDto(itemDto);
        log.info("itemDto > {} , item > {} ", itemDto, item);
        assertThrows(NullPointerException.class, () -> mapper.toItemExtDto(item).setId(111L));
    }

    @Test
    void toItemExtDtoNoTest() {
        item.setId(100L);
        log.info("item > {} ", item);
        ItemDto itemDto = mapper.toItemExtDto(item);
        assertThat(item).hasFieldOrPropertyWithValue("id", 100L);
        assertThrows(NullPointerException.class, () -> mapper.toItemExtDto(item).setId(111L));

    }

}
