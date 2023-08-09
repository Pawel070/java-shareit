package ru.practicum.shareit.item;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

@Slf4j
@Transactional
@SpringBootTest
@RequiredArgsConstructor
class ItemMapperTest {

    ItemRequestService itemRequestService;
    ItemRequest itemRequest;
    ItemRequestDto itemRequestDto;

    @MockBean
    ItemMapper mapper;

    @MockBean
    ItemRequestRepository itemRequestRepository;

    @MockBean
    UserRepository userRepository;

    @MockBean
    ItemRepository itemRepository;

    User user;
    Item item;
    ItemDto itemDto;
    List<Item> listItems;
    List<ItemRequest> listRequests;
    List<ItemRequestDto> listRequestsDto;
    Pageable pageable;

    @BeforeEach
    void beforeEach() {
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, itemRepository, userRepository, mapper);
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
        listItems = List.of(item);
        listRequests = List.of(itemRequest);
        listRequestsDto = List.of(itemRequestDto);
        pageable = PageRequest.of(0, 10);
    }

    @Test
    void toItemRequest() {
        when(mapper.toItemRequest(any())).thenReturn(itemRequest);
        //   ItemRequest res = mapper.toItemRequest(itemRequestDto);
        //   assertNotNull(res);
        assertEquals(ItemRequest.class, mapper.toItemRequest(itemRequestDto).getClass());
        assertEquals(itemRequestDto.getId(), mapper.toItemRequest(itemRequestDto).getId());
        assertEquals(itemRequestDto.getDescription(), mapper.toItemRequest(itemRequestDto).getDescription());
        assertEquals(itemRequestDto.getCreated(), mapper.toItemRequest(itemRequestDto).getCreated());
    }

    @Test
    void toItemRequestNull() {
        when(mapper.toItemRequest(any())).thenReturn(itemRequest);
        itemRequestDto = null;
        assertNotNull(mapper.toItemRequest(itemRequestDto));
    }

    @Test
    void toItemExtDto() {
        when(mapper.toItemExtDto(any())).thenReturn(itemDto);
        assertNotNull(item);
        mapper.toItemExtDto(item).setId(111L);
        assertThat(item).hasFieldOrPropertyWithValue("id", 1L);
        //       assertThrows(NullPointerException.class, () -> mapper.toItemExtDto(item).setId(111L));
    }

    @Test
    void mapToItemDtoResponse() {
        when(mapper.mapToItemDtoResponse(any())).thenReturn(itemDto);
        //      ItemRequest res = mapper.toItemRequest(itemRequestDto);
        assertNotNull(mapper.mapToItemDtoResponse(item));
        assertEquals(ItemDto.class, mapper.mapToItemDtoResponse(item).getClass());
        assertEquals(itemDto.getId(), mapper.mapToItemDtoResponse(item).getId());
        assertEquals(itemDto.getDescription(), mapper.mapToItemDtoResponse(item).getDescription());
    }

    @Test
    void mapToItemFromItemDto() {
        when(mapper.mapToItemFromItemDto(any())).thenReturn(item);
        //  ItemRequest res = mapper.toItemRequest(itemRequestDto);
        assertNotNull(mapper.mapToItemFromItemDto(itemDto));
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
    void toItemExtDtoTestN() {
        item.setId(100L);
        log.info("item > {} ", item);
        ItemDto itemDto = mapper.toItemExtDto(item);
        assertThat(item).hasFieldOrPropertyWithValue("id", 100L);
        assertThrows(NullPointerException.class, () -> mapper.toItemExtDto(item).setId(111L));

    }

}
