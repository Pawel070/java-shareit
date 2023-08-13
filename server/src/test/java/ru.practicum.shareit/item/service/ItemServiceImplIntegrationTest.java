package ru.practicum.shareit.item.service;

import static org.junit.jupiter.api.Assertions.*;

import javax.transaction.Transactional;

import java.time.LocalDateTime;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingRepository;
import ru.practicum.shareit.expections.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Slf4j
@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplIntegrationTest {

    private final ItemService itemService;
    private final ItemRequestService itemRequestService;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository requestRepository;

    Pageable pageable;
    User user;
    User user1;
    User user2;
    UserDto userDto;
    Item item;
    Item item1;
    Item item2;
    ItemDto itemDto;
    ItemRequest request1;
    ItemRequestDto itemRequestDto;
    ItemRequest itemRequest;

    @BeforeEach
    void beforeEach() {
        user = new User(10L, "user10", "mail10@mail.ru");
        user1 = new User(1L, "user1", "mail1@mail.ru");
        user2 = new User(2L, "user2", "mail2@mail.ru");
        userDto = new UserDto(1L, "user1", "mail1@mail.ru");
        request1 = new ItemRequest(1L, "req2", user2, LocalDateTime.now());
        item = new Item(10L, "item10", "des10", true, user, request1);
        item1 = new Item(1L, "item1", "des1", true, user1, request1);
        item2 = new Item(2L, "item2", "des2", true, user2, null);
        itemDto = new ItemDto(1L, "item1", "des1", true, user1, request1.getId());
    }

    @Test
    void create() {
        User savedUser = userRepository.save(user2);
        ItemRequestInfoDto requestInfoDto = itemRequestService.createItemRequest(1L, ItemRequestMapper.toItemRequestDto(request1));
        log.info("==================================== requestInfoDto {} ", requestInfoDto);
        log.info("==================================== user {} , saveUser {} ", user2, savedUser);
        ItemDto savedItem = itemService.create(itemDto, 1L);
        log.info("==================================== item {} , savedItem {} ", itemDto, savedItem);
        assertNotNull(itemDto);
        assertEquals(itemDto.getName(), savedItem.getName());
        assertEquals(itemDto.getDescription(), savedItem.getDescription());
        assertEquals(itemDto.getAvailable(), savedItem.getAvailable());
    }

    @Test
    void create_invalidItemRequest() {
        User savedUser = userRepository.save(user);
        assertThrows(NotFoundException.class, () -> itemService.create(itemDto, savedUser.getId()));
    }

    @Test
    void update_byWrongUser() {
        User user = new User();
        user.setName("Alex");
        user.setEmail("alex@mail.ru");
        User savedUser = userRepository.save(user);
        Item oldItem = new Item();
        oldItem.setName("item");
        oldItem.setDescription("desc");
        oldItem.setAvailable(true);
        oldItem.setOwner(savedUser);
        itemRepository.save(oldItem);
        ItemDto itemDto = new ItemDto();
        itemDto.setDescription("update");

        assertThrows(NotFoundException.class, () -> itemService.update(itemDto, 99L, oldItem.getId()));
    }

    @Test
    void update_wrongItemId() {
        User user = new User();
        user.setName("Alex");
        user.setEmail("alex@mail.ru");
        User savedUser = userRepository.save(user);
        Item oldItem = new Item();
        oldItem.setName("item");
        oldItem.setDescription("desc");
        oldItem.setAvailable(true);
        oldItem.setOwner(savedUser);
        itemRepository.save(oldItem);
        ItemDto itemDto = new ItemDto();
        itemDto.setDescription("update");

        assertThrows(NotFoundException.class, () -> itemService.update(itemDto, savedUser.getId(), 99L));
    }

    @Test
    void getItem() {
        LocalDateTime now = LocalDateTime.now();
        User booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@mail.ru");
        userRepository.save(booker);

        User owner = new User();
        owner.setName("owner");
        owner.setEmail("owner@mail.ru");
        userRepository.save(owner);

        Item item = new Item();
        item.setName("item");
        item.setOwner(owner);
        item.setDescription("desc");
        item.setAvailable(true);
        itemRepository.save(item);

        Booking lastBooking = new Booking();
        lastBooking.setBooker(booker);
        lastBooking.setStart(now.minusDays(2));
        lastBooking.setEnd(now.minusDays(1));
        lastBooking.setStatus(Status.APPROVED);
        lastBooking.setItem(item);
        bookingRepository.save(lastBooking);

        Booking nextBooking = new Booking();
        nextBooking.setBooker(booker);
        nextBooking.setStart(now.plusDays(1));
        nextBooking.setEnd(now.plusDays(2));
        nextBooking.setStatus(Status.APPROVED);
        nextBooking.setItem(item);
        bookingRepository.save(nextBooking);

        ItemInfoDto itemInfoDto = itemService.getItemById(item.getId(), owner.getId());

        assertNotNull(itemInfoDto);
        assertEquals(itemInfoDto.getName(), item.getName());
        assertEquals(itemInfoDto.getDescription(), item.getDescription());
        assertEquals(itemInfoDto.getAvailable(), item.getAvailable());
        assertEquals(itemInfoDto.getLastBooking().getStart(), lastBooking.getStart());
        assertEquals(itemInfoDto.getNextBooking().getStart(), nextBooking.getStart());
    }

    @Test
    void getAvailableItems() {
        User user = new User();
        user.setName("Alex");
        user.setEmail("alex@mail.ru");
        User savedUser = userRepository.save(user);

        Item item1 = new Item();
        item1.setName("item1");
        item1.setDescription("desc1");
        item1.setAvailable(true);
        item1.setOwner(savedUser);
        itemRepository.save(item1);

        Item item2 = new Item();
        item2.setName("item2-search");
        item2.setDescription("desc2");
        item2.setAvailable(true);
        item2.setOwner(savedUser);
        itemRepository.save(item2);

        Item item3 = new Item();
        item3.setName("item3-search");
        item3.setDescription("desc3");
        item3.setAvailable(false);
        item3.setOwner(savedUser);
        itemRepository.save(item3);

        List<ItemDto> items = itemService.getAvailableItems(user.getId(), "sea", pageable);

        assertNotNull(items);
        assertEquals(items.size(), 1);
        assertEquals(items.get(0).getName(), item2.getName());
        assertEquals(items.get(0).getAvailable(), true);
    }

    @Test
    void createComment() {
        LocalDateTime now = LocalDateTime.now();
        User user = new User();
        user.setName("Alex");
        user.setEmail("alex@mail.ru");
        User savedUser = userRepository.save(user);

        User owner = new User();
        owner.setName("Max");
        owner.setEmail("max@mail.ru");
        User savedOwner = userRepository.save(owner);

        Item item = new Item();
        item.setName("item1");
        item.setDescription("desc1");
        item.setAvailable(true);
        item.setOwner(savedOwner);
        Item savedItem = itemRepository.save(item);

        Booking booking = new Booking();
        booking.setBooker(savedUser);
        booking.setStart(now.minusHours(2));
        booking.setEnd(now.minusHours(1));
        booking.setStatus(Status.APPROVED);
        booking.setItem(savedItem);
        bookingRepository.save(booking);

        CommentDto commentDto = new CommentDto();
        commentDto.setText("comment");
        commentDto.setAuthorName(savedUser.getName());

        CommentDto savedComment = itemService.createComment(commentDto, item.getId(), savedUser.getId());

        assertNotNull(savedComment);
        assertEquals(savedComment.getText(), commentDto.getText());
        assertEquals(savedComment.getAuthorName(), user.getName());
    }

    @Test
    public void createComment_fromInvalidUser() {
        User user = new User();
        user.setName("Alex");
        user.setEmail("alex@mail.ru");
        User savedUser = userRepository.save(user);

        User owner = new User();
        owner.setName("Max");
        owner.setEmail("max@mail.ru");
        User savedOwner = userRepository.save(owner);

        Item item = new Item();
        item.setName("item1");
        item.setDescription("desc1");
        item.setAvailable(true);
        item.setOwner(savedOwner);
        itemRepository.save(item);

        CommentDto commentDto = new CommentDto();
        commentDto.setText("comment");
        commentDto.setAuthorName(savedUser.getName());

        assertThrows(NotFoundException.class,
                () -> itemService.createComment(commentDto, item.getId(), 99L));
    }

}