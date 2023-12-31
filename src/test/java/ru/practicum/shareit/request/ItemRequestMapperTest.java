package ru.practicum.shareit.request;

import java.time.LocalDateTime;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.BeforeEach;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.transaction.annotation.Transactional;

import ru.practicum.shareit.item.ItemMapper;
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
class ItemRequestMapperTest {
    ItemRequestService itemRequestService;
    ItemRequest itemRequest;
    ItemRequestDto itemRequestDto;

    @MockBean
    ItemMapper mapper;

    @MockBean
    ItemRequestMapper requestMapper;

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

}