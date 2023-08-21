package ru.practicum.shareit.item.service;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

import static ru.practicum.shareit.booking.model.Status.APPROVED;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.Collections;
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
import ru.practicum.shareit.expections.EntityNotAvailable;
import ru.practicum.shareit.expections.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
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
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplTest {

    ItemServiceImpl itemService;
    UserService userService;
    ItemMapper mapper;
    BookingMapper bookingMapper;
    UserMapper userMapper;
    CommentMapper commentMapper;

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
    Comment comment;

    @BeforeEach
    void beforeEach() {
        pageable = PageRequest.of(0, 10);
        itemService = new ItemServiceImpl(itemRepository, userRepository,
                commentRepository, itemRequestRepository, bookingRepository);
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
        comment = new Comment(1L, "text", item, user2, LocalDateTime.now());
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
    public void updateItemTest() {

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        item.setName("newItemName");
        item.setDescription("newItemDescription");
        log.info("item > {} ", item);
        log.info("user > {} ", user);
        ItemDto itemDtoBuf = itemService.update(ItemMapper.toItemDto(item), user.getId(), item.getId());
        assertNotNull(itemDtoBuf);
        assertEquals("newItemName", itemDtoBuf.getName());
        assertEquals("newItemDescription", itemDtoBuf.getDescription());
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
        when(commentRepository.findAllByItemId((anyLong()))).thenReturn(List.of());
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
        //       assertEquals(res.getComments().size(), 0);
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
        assertEquals(res.get(1).getRequestId(), null);
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
        CommentDto commentDto = new CommentDto(1L, "comment1", user2.getName(), comment.getCreated(), 1L);
        when(itemRepository.save(any())).thenReturn(item1);
        when(userRepository.save(any())).thenReturn(user1);
        when(userRepository.save(any())).thenReturn(user2);
        booking1.setItem(item1);
        booking1.setBooker(user2);
        Booking booking = null;
        when(bookingRepository.save(any())).thenReturn(booking1);
        // when(commentRepository.save(any())).thenReturn(comment);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));
        when(bookingRepository.findFirstByBooker_IdAndItem_Id(1L, 1L)).thenReturn(booking);
        //when(bookingRepository.isItemWasUsedByUser(anyLong(), anyLong(), any())).thenReturn(true);
        log.info("\nitem -------- > {} ,  \nuser1 =====> {} ,  \nuser2 =====> {} , \ncommentDto =====> {} ",
                item1, user1, user2, commentDto.getText());
        //commentDto = itemService.createComment(commentDto, item1.getId(), user1.getId());
        assertNotNull(commentDto);
        assertEquals(CommentDto.class, commentDto.getClass());
        assertEquals(commentDto.getId(), commentDto.getId());
        assertEquals(commentDto.getText(), commentDto.getText());
        assertEquals(commentDto.getAuthorName(), commentDto.getAuthorName());
        assertEquals(commentDto.getCreated().toString(), commentDto.getCreated().toString());
    }

    @Test
    void createComment_whenUserDoesNotUseItem() {
        Comment comment = new Comment(1L, "comment1", item1, user2, LocalDateTime.now());
        CommentDto commentDto = new CommentDto(1L, "comment1", user2.getName(), comment.getCreated(), 1L);
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
        log.info(" > {}", itemService.getItemsByOwner(user1.getId(), pageable));
        assertEquals(itemService.getItemsByOwner(user1.getId(), pageable).toString(), "[]");
    }

    @Test
    void deleteTest1() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        item.setId(10L);
        log.info("item > {} ", item);
        itemService.delete(10L, item.getOwner().getId());
        assertNotNull(item);
    }

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
        //assertFalse(itemService.isCheckItemOwner(100L, 200L));
    }

    @Test
    void isCheckItemOwnerMin() {
        assertThrows(Exception.class,
                () -> itemService.isCheckItemOwner(0L, 0L));
    }

    @Test
    void isCheckItemOwnerMax() {
        assertThrows(Exception.class,
                () -> itemService.isCheckItemOwner(-1L, -1L));
    }

    @Test
    public void getAllItemsByUserTest() {
        List<Item> items = new ArrayList<>();
        when(itemRepository.save(any())).thenReturn(item1);
        when(userRepository.save(any())).thenReturn(user1);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));
        when(itemRepository.findAllByOwnerIdOrderByIdAsc(anyLong(), any(Pageable.class))).thenReturn(items);
        log.info("\nitems -------- > {} \nitem =====> {},  \nuser =====> {} ", items, item1, user1);
        List<ItemInfoDto> itemsDto = itemService.getItemsByOwner(1L, pageable);
        log.info("itemsDto -------- > {} ", itemsDto);
        ItemInfoDto itemTest;
        itemTest = ItemMapper.toItemInfoDto(item1, null, null, Collections.emptyList());
        assertNotNull(items);
        log.info(" \nitems {} ======= > {} ", items, itemTest);
        assertEquals(items.size(), 0);
        assertEquals(itemTest.getId(), 1L);
        assertEquals(itemTest.getName(), "item1");
        assertEquals(itemTest.getDescription(), "des1");
        assertEquals(itemTest.getAvailable(), true);
    }

    @Test
    void getItemDto() {
        // assertThrows(Exception.class, () -> itemService.getItemDto(Collections.emptyList());
        assertEquals(itemService.getItemDto(Collections.emptyList()).size(), 0);
    }

    @Test
    void bookingLast() {
//        assertThrows(Exception.class, () -> itemService.bookingLast(item, LocalDateTime.now()));
        assertEquals(itemService.bookingLast(item, LocalDateTime.now()), null);
    }

    @Test
    void bookingNext() {
//        assertThrows(Exception.class, () -> itemService.bookingNext(item, LocalDateTime.now()));
        assertEquals(itemService.bookingNext(item, LocalDateTime.now()), null);
    }

    @Test
    void commentDto() {
        // assertThrows(Exception.class, () -> itemService.commentDto(item));
        assertEquals(itemService.commentDto(item).size(), 0);
    }

}
