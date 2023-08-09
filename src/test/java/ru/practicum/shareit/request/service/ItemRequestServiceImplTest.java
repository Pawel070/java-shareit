package ru.practicum.shareit.request.service;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.expections.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

@Transactional
@SpringBootTest
@RequiredArgsConstructor
class ItemRequestServiceImplTest {

    ItemRequestService itemRequestService;
    ItemService itemService;
    ItemRequest itemRequest;
    ItemRequestDto itemRequestDto;

    @Autowired
    ItemMapper mapper;

    @MockBean
    ItemRequestRepository itemRequestRepository;

    @MockBean
    UserRepository userRepository;

    @MockBean
    ItemRepository itemRepository;

    User user;
    Item item;
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
    void createItemRequest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any())).thenReturn(itemRequest);

        ItemRequestInfoDto res = itemRequestService.createItemRequest(user.getId(), itemRequestDto);

        assertNotNull(res);
        assertEquals(ItemRequestInfoDto.class, res.getClass());
        assertEquals(itemRequest.getId(), res.getId());
        assertEquals(itemRequest.getDescription(), res.getDescription());
        assertEquals(itemRequest.getCreated(), res.getCreated());
    }

    @Test
    void createItemRequest_WithWrongUserId() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(itemRequestRepository.save(any())).thenReturn(itemRequest);

        assertThrows(NotFoundException.class,
                () -> itemRequestService.createItemRequest(999L, itemRequestDto));
    }

    @Test
    void getUsersItemRequests() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(anyLong())).thenReturn(listRequests);
        when(itemRepository.findAllByRequest_IdOrderByIdDesc(anyLong())).thenReturn(listItems);

        List<ItemRequestInfoDto> res = itemRequestService.getUsersItemRequests(1L);

        assertNotNull(res);
        assertEquals(1, res.size());
        assertEquals(ItemRequestInfoDto.class, res.get(0).getClass());
        assertEquals(itemRequest.getId(), res.get(0).getId());
        assertEquals(itemRequest.getDescription(), res.get(0).getDescription());
        assertEquals(itemRequest.getCreated(), res.get(0).getCreated());
    }

    @Test
    void getUsersItemRequests_WithWrongUserId() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(NotFoundException.class,
                () -> itemRequestService.getUsersItemRequests(999L));
    }

    @Test
    void getItemRequests() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRequestRepository.findAllByRequesterIdNot(anyLong(), any())).thenReturn(listRequests);
        when(itemRepository.findAllByRequest_IdOrderByIdDesc(anyLong())).thenReturn(listItems);

        List<ItemRequestInfoDto> res = itemRequestService.getItemRequests(1L, pageable);

        assertNotNull(res);
        assertEquals(1, res.size());
        assertEquals(ItemRequestInfoDto.class, res.get(0).getClass());
        assertEquals(itemRequest.getId(), res.get(0).getId());
        assertEquals(itemRequest.getDescription(), res.get(0).getDescription());
        assertEquals(itemRequest.getCreated(), res.get(0).getCreated());
    }

    @Test
    void getItemRequests_WithWrongUserId() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(NotFoundException.class,
                () -> itemRequestService.getItemRequests(999L, pageable));
    }

    @Test
    void getItemRequestById() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findAllByRequest_IdOrderByIdDesc(anyLong())).thenReturn(listItems);

        ItemRequestInfoDto res = itemRequestService.getItemRequestById(itemRequest.getId(), user.getId());

        assertNotNull(res);
        assertEquals(ItemRequestInfoDto.class, res.getClass());
        assertEquals(1, res.getItems().size());
        assertEquals(itemRequest.getId(), res.getId());
        assertEquals(itemRequest.getDescription(), res.getDescription());
        assertEquals(itemRequest.getCreated(), res.getCreated());
    }

    @Test
    void getItemRequestById_WithWrongUserId() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(NotFoundException.class,
                () -> itemRequestService.getItemRequestById(itemRequest.getId(), 999L));
    }

    @Test
    void isCheckFromSizeNoFromTest() {
        assertThrows(Exception.class,
                () -> itemRequestService.isCheckFromSize(-2, 10));
    }

    @Test
    void isCheckFromSizeNoSizeTest() {
        assertThrows(Exception.class,
                () -> itemRequestService.isCheckFromSize(0, 0));
    }

    @Test
    void isCheckFromSizeNoFromSizeTest() {
        assertThrows(Exception.class,
                () -> itemRequestService.isCheckFromSize(-2, 0));
    }

}