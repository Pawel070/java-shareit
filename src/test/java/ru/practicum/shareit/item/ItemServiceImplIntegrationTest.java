package ru.practicum.shareit.item;

import static org.junit.jupiter.api.Assertions.*;

import javax.transaction.Transactional;

import java.time.LocalDateTime;

import java.util.List;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingRepository;
import ru.practicum.shareit.expections.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.CommentRepository;
import ru.practicum.shareit.item.service.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

@Transactional
@SpringBootTest
class ItemServiceImplIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void create() {
        User user = new User();
        user.setName("Alex");
        user.setEmail("alex@ya.ru");
        User savedUser = userRepository.save(user);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("item");
        itemDto.setDescription("desc");
        itemDto.setAvailable(true);

        ItemDto savedItem = itemService.create(itemDto, savedUser.getId());

        assertNotNull(itemDto);
        assertEquals(itemDto.getName(), savedItem.getName());
        assertEquals(itemDto.getDescription(), savedItem.getDescription());
        assertEquals(itemDto.getAvailable(), savedItem.getAvailable());

    }

    @Test
    void create_invalidItemRequest() {
        User user = new User();
        user.setName("Alex");
        user.setEmail("alex@ya.ru");
        User savedUser = userRepository.save(user);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("item");
        itemDto.setDescription("desc");
        itemDto.setAvailable(true);
        itemDto.setRequestId(1L);

        assertThrows(NotFoundException.class, () -> itemService.create(itemDto, savedUser.getId()));
    }

    @Test
    void update() {
        User user = new User();
        user.setName("Alex");
        user.setEmail("alex@ya.ru");
        User savedUser = userRepository.save(user);

        Item oldItem = new Item();
        oldItem.setName("item");
        oldItem.setDescription("desc");
        oldItem.setAvailable(true);
        oldItem.setOwner(savedUser);
        itemRepository.save(oldItem);

        ItemDto newItemDto = new ItemDto();
        newItemDto.setName("newItem");

        ItemDto updatedItemDto = itemService.update(newItemDto, user.getId(), oldItem.getId());

        assertEquals(updatedItemDto.getName(), newItemDto.getName());
        assertEquals(updatedItemDto.getDescription(), oldItem.getDescription());
        assertEquals(updatedItemDto.getAvailable(), oldItem.getAvailable());
    }

    @Test
    void update_byWrongUser() {
        User user = new User();
        user.setName("Alex");
        user.setEmail("alex@ya.ru");
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
        user.setEmail("alex@ya.ru");
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
        booker.setEmail("booker@ya.ru");
        userRepository.save(booker);

        User owner = new User();
        owner.setName("owner");
        owner.setEmail("owner@ya.ru");
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
    void getItemsByUser() {
        User user = new User();
        user.setName("Alex");
        user.setEmail("alex@ya.ru");
        User savedUser = userRepository.save(user);

        Item item1 = new Item();
        item1.setName("item1");
        item1.setDescription("desc1");
        item1.setAvailable(true);
        item1.setOwner(savedUser);
        Item savedItem1 = itemRepository.save(item1);

        Item item2 = new Item();
        item2.setName("item2");
        item2.setDescription("desc2");
        item2.setAvailable(true);
        item2.setOwner(savedUser);
        itemRepository.save(item2);

        Comment comment = new Comment();
        comment.setAuthor(savedUser);
        comment.setText("comment");
        comment.setItem(savedItem1);
        commentRepository.save(comment);

        Pageable pageable =  PageRequest.of(0, 10);
        List<ItemInfoDto> items = itemService.getItemsByOwner(user.getId(), pageable);

        assertNotNull(items);
        assertEquals(items.size(), 2);
        assertEquals(items.get(0).getName(), item1.getName());
        assertEquals(items.get(0).getComments().get(0).getText(), comment.getText());
        assertEquals(items.get(1).getName(), item2.getName());
        assertEquals(items.get(1).getComments().size(), 0);
    }

    @Test
    void getAvailableItems() {
        User user = new User();
        user.setName("Alex");
        user.setEmail("alex@ya.ru");
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

        Pageable pageable =  PageRequest.of(0, 10);
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
        user.setEmail("alex@ya.ru");
        User savedUser = userRepository.save(user);

        User owner = new User();
        owner.setName("Max");
        owner.setEmail("max@ya.ru");
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
        user.setEmail("alex@ya.ru");
        User savedUser = userRepository.save(user);

        User owner = new User();
        owner.setName("Max");
        owner.setEmail("max@ya.ru");
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