package ru.practicum.shareit.booking.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.shareit.service.MyConstants.SORT_DESC;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

@Transactional
@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    User user;
    User user2;
    User owner;
    Item item;
    ItemRequest request;
    Booking booking;
    Booking booking2;

    @BeforeAll
    public void beforeAll() {
        user = new User(1L, "user", "user@mail.ru");
        user2 = new User(3L, "user2", "user2@mail.ru");
        owner = new User(2L, "owner", "owner@mail.ru");
        userRepository.save(user);
        userRepository.save(owner);
        userRepository.save(user2);
        request = new ItemRequest(1L, "req1", user, LocalDateTime.now());
        itemRequestRepository.save(request);
        item = new Item(1L, "item", "desc", true, owner, request);
        itemRepository.save(item);
        booking = new Booking(
                1L,
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1),
                item,
                user,
                Status.WAITING);
        booking2 = new Booking(
                2L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                item,
                user2,
                Status.WAITING);
        bookingRepository.save(booking);
        bookingRepository.save(booking2);
    }

    @Test
    void isItemWasUsedByUser_true() {
        Boolean res = bookingRepository.isItemWasUsedByUser(item.getId(), user.getId(), LocalDateTime.now());
        assertEquals(res, true);
    }

    @Test
    void isItemWasUsedByUser_false() {
        Boolean res = bookingRepository.isItemWasUsedByUser(item.getId(), user2.getId(), LocalDateTime.now());
        assertEquals(res, false);
    }

    @Test
    void deleteInBatch() {
    }

    @Test
    void findAllByBooker_IdOrderByStartDesc() {
    }

    @Test
    void findAllByBooker_IdAndEndIsBefore() {
        User userSaved = userRepository.save(user);
        itemRepository.save(item);
        User userSaved2 = userRepository.save(user2);
        bookingRepository.save(booking);
        assertThat(bookingRepository.findAllByBooker_IdAndStartIsAfter(userSaved2.getId(),
                        LocalDateTime.now(), SORT_DESC).size(), equalTo(1));
    }

    @Test
    void findAllByBooker_IdAndStartIsAfter() {
    }

    @Test
    void findAllByBooker_IdAndStartIsBeforeAndEndIsAfter() {
    }

    @Test
    void findAllByBooker_IdAndStatus() {
    }

    @Test
    void findAllByItem_Owner_IdOrderByStartDesc() {
    }

    @Test
    void findAllByItem_Owner_IdAndStartIsAfter() {
    }

    @Test
    void findAllByItem_Owner_IdAndStartIsBeforeAndEndIsAfter() {
    }

    @Test
    void findAllByItem_Owner_IdAndEndIsBefore() {
    }

    @Test
    void findAllByItem_Owner_IdAndStatus() {
    }

    @Test
    void isItemWasUsedByUser() {
    }

    @Test
    void findFirstByItem_IdAndItem_Owner_IdAndStartIsBefore() {
    }

    @Test
    void findFirstByItem_IdInAndItem_Owner_IdAndStartIsBefore() {
    }

    @Test
    void findFirstByItem_IdAndItem_Owner_IdAndStartIsAfterAndStatusIsNotAndStatusIsNot() {
    }

    @Test
    void findFirstByItem_IdInAndItem_Owner_IdAndStartIsAfterAndStatusIsNotAndStatusIsNot() {
    }

}