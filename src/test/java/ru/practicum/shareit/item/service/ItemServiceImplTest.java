package ru.practicum.shareit.item.service;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

import static ru.practicum.shareit.booking.model.Status.APPROVED;

import java.time.LocalDateTime;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingRepository;
import ru.practicum.shareit.exceptions.EntityNotAvailable;
import ru.practicum.shareit.expections.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

@Slf4j
@Transactional
@SpringBootTest
@RequiredArgsConstructor
class ItemServiceImplTest {

    ItemService itemService;

    @MockBean
    BookingRepository bookingRepository;

    @MockBean
    CommentRepository commentRepository;

    @MockBean
    ItemRepository itemRepository;

    @MockBean
    UserRepository userRepository;

    @MockBean
    ItemRequestRepository itemRequestRepository;

    @Autowired
    UserService userService;

    @Autowired
    ItemMapper mapper;

    @Autowired
    BookingMapper bookingMapper;

    @Autowired
    UserMapper userMapper;

    Pageable pageable;
    User user;
    User user1;
    User user2;
    UserDto userDto1;
    Item item1;
    Item item2;
    ItemDto itemDto;
    ItemDto itemDto1;
    ItemRequest request1;
    Booking booking1;
    Booking booking2;
    ItemRequestDto itemRequestDto;
    ItemRequest itemRequest;
    Item item;

    @BeforeEach
    void beforeEach() {

        itemService = new ItemServiceImpl(mapper, bookingMapper,
                userMapper, itemRepository, userRepository,
                commentRepository, userService, itemRequestRepository,
                bookingRepository);
        user = new User(10L, "user10", "mail10@mail.ru");
        user1 = new User(1L, "user1", "mail1@mail.ru");
        user2 = new User(2L, "user2", "mail2@mail.ru");
        userDto1 = new UserDto(1L, "user1", "mail1@mail.ru");
        request1 = new ItemRequest(2L, "req2", user2, LocalDateTime.now());
        item = new Item(10L, "item10", "des10", true, user, request1);
        item1 = new Item(1L, "item1", "des1", true, user1, request1);
        item2 = new Item(2L, "item2", "des2", true, user2, null);
        itemDto1 = new ItemDto(1L, "item1", "des1", true, user1, request1.getId());
        booking1 = new Booking(1L, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1),
                item1, user2, APPROVED);
        booking2 = new Booking(2L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                item1, user2, APPROVED);
        pageable = PageRequest.of(0, 10);
    }

    @Test
    void createItem() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(request1));
        when(itemRepository.save(any())).thenReturn(item1);
        ItemDto res = itemService.create(itemDto1, user1.getId());
        assertNotNull(res);
        assertEquals(ItemDto.class, res.getClass());
        assertEquals(res.getId(), itemDto1.getId());
        assertEquals(res.getName(), itemDto1.getName());
        assertEquals(res.getDescription(), itemDto1.getDescription());
        assertEquals(res.getAvailable(), itemDto1.getAvailable());
        assertEquals(res.getOwner().toString(), itemDto1.getOwner().toString());
        assertEquals(res.getRequestId(), itemDto1.getRequestId());
    }

    @Test
    void createItem_withWrongItemRequestId() {
        itemDto1.setRequestId(99L);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemService.create(itemDto1, user1.getId()));
    }

    @Test
    void updateItem() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));
        when(itemRepository.save(any())).thenReturn(item1);
        ItemDto newItemDto = new ItemDto(0, "upd", "upd", false, user1, request1.getId());
        ItemDto res = itemService.update(newItemDto, user1.getId(), itemDto1.getId());
        assertNotNull(res);
        assertEquals(ItemDto.class, res.getClass());
        assertEquals(res.getId(), itemDto1.getId());
        assertEquals(res.getName(), newItemDto.getName());
        assertEquals(res.getDescription(), newItemDto.getDescription());
        assertEquals(res.getAvailable(), newItemDto.getAvailable());
        assertEquals(res.getOwner().toString(), itemDto1.getOwner().toString());
    }

    @Test
    void updateItem_itemDoesNotBelongToUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));
        ItemDto newItemDto = new ItemDto(0, "upd", "upd", false, user1, request1.getId());
        assertThrows(NotFoundException.class,
                () -> itemService.update(newItemDto, user1.getId(), itemDto1.getId()));
    }

    @Test
    void getItem() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));
        when(commentRepository.findAllByItem_Id((anyLong()))).thenReturn(List.of());
        when(bookingRepository.findFirstByItem_IdAndItem_Owner_IdAndStartIsBefore(anyLong(), anyLong(), any(), any()))
                .thenReturn(booking1);
        when(bookingRepository.findFirstByItem_IdAndItem_Owner_IdAndStartIsAfterAndStatusIsNotAndStatusIsNot(
                anyLong(), anyLong(), any(), any(), any(), any())).thenReturn(booking2);
        ItemInfoDto res = itemService.getItemById(itemDto1.getId(), user1.getId());
        assertNotNull(res);
        assertEquals(ItemInfoDto.class, res.getClass());
        assertEquals(res.getId(), itemDto1.getId());
        assertEquals(res.getName(), itemDto1.getName());
        assertEquals(res.getDescription(), itemDto1.getDescription());
        assertEquals(res.getAvailable(), itemDto1.getAvailable());
        assertEquals(res.getOwner().toString(), userDto1.toString());
        assertEquals(res.getLastBooking().toString(), bookingMapper.toBookingInfoDto(booking1).toString());
        assertEquals(res.getNextBooking().toString(), bookingMapper.toBookingInfoDto(booking2).toString());
        assertEquals(res.getComments().size(), 0);
    }

    @Test
    void getItemsByUser() {
        List<Item> items = List.of(item1, item2);
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findByOwner_Id(anyLong(), any())).thenReturn(items);
        when(commentRepository.findAllByItemsId(any())).thenReturn(List.of());
        when(bookingRepository.findFirstByItem_IdInAndItem_Owner_IdAndStartIsBefore(any(), anyLong(), any(), any()))
                .thenReturn(List.of(booking1));
        when(bookingRepository.findFirstByItem_IdInAndItem_Owner_IdAndStartIsAfterAndStatusIsNotAndStatusIsNot(
                any(), anyLong(), any(), any(), any(), any())).thenReturn(List.of(booking2));
        List<ItemInfoDto> res = itemService.getItemsByOwner(user1.getId(), pageable);
        assertEquals(res.size(), 2);
        assertEquals(ItemInfoDto.class, res.get(0).getClass());
        assertEquals(res.get(0).getId(), item1.getId());
        assertEquals(res.get(0).getName(), item1.getName());
        assertEquals(res.get(0).getDescription(), item1.getDescription());
        assertEquals(res.get(0).getAvailable(), item1.getAvailable());
        assertEquals(res.get(0).getOwner().toString(), mapper.toUserDto(user1).toString());
        assertEquals(res.get(0).getRequestId(), item1.getRequest().getId());
        assertEquals(res.get(0).getLastBooking().toString(), bookingMapper.toBookingInfoDto(booking1).toString());
        assertEquals(res.get(0).getNextBooking().toString(), bookingMapper.toBookingInfoDto(booking2).toString());
        assertEquals(res.get(0).getComments().size(), 0);
        assertEquals(ItemInfoDto.class, res.get(1).getClass());
        assertEquals(res.get(1).getId(), item2.getId());
        assertEquals(res.get(1).getName(), item2.getName());
        assertEquals(res.get(1).getDescription(), item2.getDescription());
        assertEquals(res.get(1).getAvailable(), item2.getAvailable());
        assertEquals(res.get(1).getOwner().toString(), mapper.toUserDto(user2).toString());
        assertNull(res.get(1).getRequestId());
        assertNull(res.get(1).getLastBooking());
        assertNull(res.get(1).getNextBooking());
        assertEquals(res.get(1).getComments().size(), 0);
    }

    @Test
    void getItemsByUser_wrongUser() {
        when(userRepository.existsById(anyLong())).thenReturn(false);
        assertThrows(NotFoundException.class, () -> itemService.getItemsByOwner(99L, pageable));
    }

    @Test
    void getAvailableItems() {
        when(itemRepository.searchAvailableItems(anyString(), any())).thenReturn(List.of(item1, item2));
        List<ItemDto> res = itemService.getAvailableItems(user1.getId(), "item", pageable);
        assertEquals(res.size(), 2);
        assertEquals(ItemDto.class, res.get(0).getClass());
        assertEquals(res.get(0).getId(), item1.getId());
        assertEquals(res.get(0).getName(), item1.getName());
        assertEquals(res.get(0).getDescription(), item1.getDescription());
        assertEquals(res.get(0).getAvailable(), item1.getAvailable());
        assertEquals(res.get(0).getOwner().toString(), user1.toString());
        assertEquals(res.get(0).getRequestId(), item1.getRequest().getId());
        assertEquals(ItemDto.class, res.get(1).getClass());
        assertEquals(res.get(1).getId(), item2.getId());
        assertEquals(res.get(1).getName(), item2.getName());
        assertEquals(res.get(1).getDescription(), item2.getDescription());
        assertEquals(res.get(1).getAvailable(), item2.getAvailable());
        assertEquals(res.get(1).getOwner().toString(), user2.toString());
        assertEquals(res.get(1).getRequestId(), 0);
    }

    @Test
    void getCommentsByItemId() {
        List<CommentDto> res = itemService.getCommentsByItemId(1L);
        assertEquals(res.size(), 0);
    }

    @Test
    void getAvailableItems_withBlankText() {
        List<ItemDto> res = itemService.getAvailableItems(user1.getId(), "       ", pageable);
        assertEquals(res.size(), 0);
    }

    @Test
    void createComment() {
        Comment comment = new Comment(1L, "comment1", item1, user2, LocalDateTime.now());
        CommentDto commentDto = new CommentDto(1L, "comment1", user2.getName(), comment.getCreated());
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));
        when(bookingRepository.isItemWasUsedByUser(anyLong(), anyLong(), any())).thenReturn(true);
        when(commentRepository.save(any())).thenReturn(comment);
        CommentDto res = itemService.createComment(commentDto, item1.getId(), user1.getId());
        assertNotNull(res);
        assertEquals(CommentDto.class, res.getClass());
        assertEquals(res.getId(), commentDto.getId());
        assertEquals(res.getText(), commentDto.getText());
        assertEquals(res.getAuthorName(), commentDto.getAuthorName());
        assertEquals(res.getCreated().toString(), commentDto.getCreated().toString());
    }

    @Test
    void createComment_whenUserDoesNotUseItem() {
        Comment comment = new Comment(1L, "comment1", item1, user2, LocalDateTime.now());
        CommentDto commentDto = new CommentDto(1L, "comment1", user2.getName(), comment.getCreated());
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));
        when(bookingRepository.isItemWasUsedByUser(anyLong(), anyLong(), any())).thenReturn(false);
        assertThrows(EntityNotAvailable.class,
                () -> itemService.createComment(commentDto, item1.getId(), user1.getId()));
    }

    @Test
    void findItemById() {
        when(itemRepository.existsById(anyLong())).thenReturn(false);
        assertThrows(NotFoundException.class, () -> itemService.getItemsByOwner(99L, pageable));
    }

    @Test
    void findItemById1() {
        when(itemRepository.existsById(anyLong())).thenReturn(true);
        assertThrows(NotFoundException.class, () -> itemService.findItemById(99L));
    }

    @Test
    void findItemById2() {
        when(itemRepository.existsById(anyLong())).thenReturn(true);
        assertThrows(NotFoundException.class, () -> itemService.findItemById(1L));
    }

    @Test
    void findItemByIdTest() {
        assertThrows(NotFoundException.class, () -> itemService.findItemById(99L));
    }

    @Test
    void deleteItemsByUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(request1));
        when(itemRepository.save(any())).thenReturn(item1);
        ItemDto res = itemService.create(itemDto1, user1.getId());
        log.info("res > {}", res);
        assertNotNull(res);
        itemService.deleteItemsByUser(res.getOwner().getId());
        assertThrows(NotFoundException.class,
                () -> itemService.getItemsByOwner(res.getOwner().getId(), pageable));
    }

    @Test
    void deleteTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(request1));
        when(itemRepository.save(any())).thenReturn(item1);
        ItemDto res = itemService.create(itemDto1, user1.getId());
        log.info("res > {}", res);
        assertNotNull(res);
        assertThrows(NotFoundException.class,
                () -> itemService.delete(user1.getId(), res.getOwner().getId()));
    }
/*
    @Test
    void deleteTest1() {
//        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        log.info("item > {} ", item);
        assertThrows(NotFoundException.class,
                () -> itemService.delete(item.getId(), item.getOwner().getId()));

    }
*/
    @Test
    void isCheckFromSizeNoFromTest() {
        assertThrows(Exception.class,
                () -> itemService.isCheckFromSize(-2, 10));
    }

    @Test
    void isCheckFromSizeNoSizeTest() {
        assertThrows(Exception.class,
                () -> itemService.isCheckFromSize(0, 0));
    }

    @Test
    void isCheckFromSizeNoFromSizeTest() {
        assertThrows(Exception.class,
                () -> itemService.isCheckFromSize(-2, 0));
    }

    @Test
    void isCheckItemOwner() {
        assertThrows(Exception.class,
                () -> itemService.isCheckItemOwner(100L, 200L));
    }

}
