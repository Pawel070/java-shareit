package ru.practicum.shareit.booking.service;

import static org.junit.jupiter.api.Assertions.*;

import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingModelDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exceptions.EntityNotAvailable;
import ru.practicum.shareit.expections.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

@Transactional
@SpringBootTest
class BookingServiceImplIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Test
    void create() {
        User booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@mail.ru");
        userRepository.save(booker);

        User owner = new User();
        owner.setName("owner");
        owner.setEmail("owner@mail.ru");
        User savedOwner = userRepository.save(owner);

        Item item = new Item();
        item.setName("item");
        item.setDescription("desc");
        item.setOwner(savedOwner);
        item.setAvailable(true);
        Item savedItem = itemRepository.save(item);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(savedItem.getId());
        bookingDto.setStart(LocalDateTime.now().plusHours(1));
        bookingDto.setEnd(LocalDateTime.now().plusHours(2));

        BookingModelDto result = bookingService.create(bookingDto, booker.getId());

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getBooker());
        assertNotNull(result.getBooker().getId());
        assertNotNull(result.getItem());
        assertNotNull(result.getItem().getId());
        assertEquals(result.getStart(), bookingDto.getStart());
        assertEquals(result.getEnd(), bookingDto.getEnd());
    }

    @Test
    void create_itemUnavailable() {
        User booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@mail.ru");
        userRepository.save(booker);

        User owner = new User();
        owner.setName("owner");
        owner.setEmail("owner@mail.ru");
        User savedOwner = userRepository.save(owner);

        Item item = new Item();
        item.setName("item");
        item.setDescription("desc");
        item.setOwner(savedOwner);
        item.setAvailable(false);
        Item savedItem = itemRepository.save(item);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(savedItem.getId());
        bookingDto.setStart(LocalDateTime.now().plusHours(1));
        bookingDto.setEnd(LocalDateTime.now().plusHours(2));

        assertThrows(EntityNotAvailable.class, () -> bookingService.create(bookingDto, booker.getId()));
    }

    @Test
    void create_startAfterEnd() {
        User booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@mail.ru");
        userRepository.save(booker);

        User owner = new User();
        owner.setName("owner");
        owner.setEmail("owner@mail.ru");
        User savedOwner = userRepository.save(owner);

        Item item = new Item();
        item.setName("item");
        item.setDescription("desc");
        item.setOwner(savedOwner);
        item.setAvailable(true);
        Item savedItem = itemRepository.save(item);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(savedItem.getId());
        bookingDto.setStart(LocalDateTime.now().plusHours(2));
        bookingDto.setEnd(LocalDateTime.now().plusHours(1));

        assertThrows(EntityNotAvailable.class, () -> bookingService.create(bookingDto, booker.getId()));
    }

    @Test
    void create_bookerIsOwner() {
        User owner = new User();
        owner.setName("owner");
        owner.setEmail("owner@mail.ru");
        User savedOwner = userRepository.save(owner);

        Item item = new Item();
        item.setName("item");
        item.setDescription("desc");
        item.setOwner(savedOwner);
        item.setAvailable(true);
        Item savedItem = itemRepository.save(item);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(savedItem.getId());
        bookingDto.setStart(LocalDateTime.now().plusHours(1));
        bookingDto.setEnd(LocalDateTime.now().plusHours(2));

        assertThrows(NotFoundException.class, () -> bookingService.create(bookingDto, owner.getId()));
    }

    @Test
    void confirmationBooking_updateStatusToApproved() {
        LocalDateTime now = LocalDateTime.now();
        User booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@mail.ru");
        User savedBooker = userRepository.save(booker);

        User owner = new User();
        owner.setName("owner");
        owner.setEmail("owner@mail.ru");
        User savedOwner = userRepository.save(owner);

        Item item = new Item();
        item.setName("item");
        item.setDescription("desc");
        item.setAvailable(true);
        item.setOwner(savedOwner);
        Item savedItem = itemRepository.save(item);

        Booking booking = new Booking();
        booking.setStart(now.plusHours(1));
        booking.setEnd(now.plusHours(2));
        booking.setStatus(Status.WAITING);
        booking.setItem(savedItem);
        booking.setBooker(savedBooker);
        Booking savedBooking = bookingRepository.save(booking);

        BookingModelDto updatedBooking = bookingService
                .update(savedBooking.getId(), savedOwner.getId(),true);

        assertEquals(updatedBooking.getStatus(), Status.APPROVED);
    }

    @Test
    void confirmationBooking_updateStatusToRejected() {
        LocalDateTime now = LocalDateTime.now();

        User booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@mail.ru");
        User savedBooker = userRepository.save(booker);

        User owner = new User();
        owner.setName("owner");
        owner.setEmail("owner@mail.ru");
        User savedOwner = userRepository.save(owner);

        Item item = new Item();
        item.setName("item");
        item.setDescription("desc");
        item.setAvailable(true);
        item.setOwner(savedOwner);
        Item savedItem = itemRepository.save(item);

        Booking booking = new Booking();
        booking.setStart(now.plusHours(1));
        booking.setEnd(now.plusHours(2));
        booking.setStatus(Status.WAITING);
        booking.setItem(savedItem);
        booking.setBooker(savedBooker);
        Booking savedBooking = bookingRepository.save(booking);

        BookingModelDto updatedBooking = bookingService
                .update(savedBooking.getId(), savedOwner.getId(), false);

        assertEquals(updatedBooking.getStatus(), Status.REJECTED);
    }


    @Test
    void confirmationBooking_wrongBookingId() {
        LocalDateTime now = LocalDateTime.now();

        User booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@mail.ru");
        User savedBooker = userRepository.save(booker);

        User owner = new User();
        owner.setName("owner");
        owner.setEmail("owner@mail.ru");
        User savedOwner = userRepository.save(owner);

        Item item = new Item();
        item.setName("item");
        item.setDescription("desc");
        item.setAvailable(true);
        item.setOwner(savedOwner);
        Item savedItem = itemRepository.save(item);

        Booking booking = new Booking();
        booking.setStart(now.plusHours(1));
        booking.setEnd(now.plusHours(2));
        booking.setStatus(Status.WAITING);
        booking.setItem(savedItem);
        booking.setBooker(savedBooker);
        bookingRepository.save(booking);

        assertThrows(NotFoundException.class,
                () -> bookingService.update(savedOwner.getId(), 99L, false));
    }

    @Test
    void confirmationBooking_notByOwner() {
        LocalDateTime now = LocalDateTime.now();

        User booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@mail.ru");
        User savedBooker = userRepository.save(booker);

        User owner = new User();
        owner.setName("owner");
        owner.setEmail("owner@mail.ru");
        User savedOwner = userRepository.save(owner);

        Item item = new Item();
        item.setName("item");
        item.setDescription("desc");
        item.setAvailable(true);
        item.setOwner(savedOwner);
        Item savedItem = itemRepository.save(item);

        Booking booking = new Booking();
        booking.setStart(now.plusHours(1));
        booking.setEnd(now.plusHours(2));
        booking.setStatus(Status.WAITING);
        booking.setItem(savedItem);
        booking.setBooker(savedBooker);
        Booking savedBooking = bookingRepository.save(booking);

        assertThrows(NotFoundException.class,
                () -> bookingService.update(savedBooker.getId(), savedBooking.getId(), false));
    }

    @Test
    void confirmationBooking_ifStatusConfirmed() {
        LocalDateTime now = LocalDateTime.now();

        User booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@mail.ru");
        User savedBooker = userRepository.save(booker);

        User owner = new User();
        owner.setName("owner");
        owner.setEmail("owner@mail.ru");
        User savedOwner = userRepository.save(owner);

        Item item = new Item();
        item.setName("item");
        item.setDescription("desc");
        item.setAvailable(true);
        item.setOwner(savedOwner);
        Item savedItem = itemRepository.save(item);

        Booking booking = new Booking();
        booking.setStart(now.plusHours(1));
        booking.setEnd(now.plusHours(2));
        booking.setStatus(Status.APPROVED);
        booking.setItem(savedItem);
        booking.setBooker(savedBooker);
        Booking savedBooking = bookingRepository.save(booking);

        assertThrows(NotFoundException.class,
                () -> bookingService.update(savedOwner.getId(), savedBooking.getId(), false));
    }

    @Test
    void getBookingById() {
        LocalDateTime now = LocalDateTime.now();

        User booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@mail.ru");
        User savedBooker = userRepository.save(booker);

        User owner = new User();
        owner.setName("owner");
        owner.setEmail("owner@mail.ru");
        User savedOwner = userRepository.save(owner);

        Item item = new Item();
        item.setName("item");
        item.setDescription("desc");
        item.setAvailable(true);
        item.setOwner(savedOwner);
        Item savedItem = itemRepository.save(item);

        Booking booking = new Booking();
        booking.setStart(now.plusHours(1));
        booking.setEnd(now.plusHours(2));
        booking.setStatus(Status.APPROVED);
        booking.setItem(savedItem);
        booking.setBooker(savedBooker);
        Booking savedBooking = bookingRepository.save(booking);

        BookingModelDto result = bookingService.getBookingById(savedBooking.getId(), savedBooker.getId());

        assertNotNull(result);
        assertEquals(result.getId(), savedBooking.getId());
    }

    @Test
    void getBookingById_byThirdUser() {
        LocalDateTime now = LocalDateTime.now();

        User booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@mail.ru");
        User savedBooker = userRepository.save(booker);

        User owner = new User();
        owner.setName("owner");
        owner.setEmail("owner@mail.ru");
        User savedOwner = userRepository.save(owner);

        Item item = new Item();
        item.setName("item");
        item.setDescription("desc");
        item.setAvailable(true);
        item.setOwner(savedOwner);
        Item savedItem = itemRepository.save(item);

        Booking booking = new Booking();
        booking.setStart(now.plusHours(1));
        booking.setEnd(now.plusHours(2));
        booking.setStatus(Status.APPROVED);
        booking.setItem(savedItem);
        booking.setBooker(savedBooker);
        bookingRepository.save(booking);

        assertThrows(NotFoundException.class,
                () -> bookingService.getBookingById(booking.getId(), 99L));
    }


    @Test
    void getAllBookingByUser() {
        LocalDateTime now = LocalDateTime.now();
        Pageable pageable =  PageRequest.of(0, 10);

        User booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@mail.ru");
        User savedBooker = userRepository.save(booker);

        User booker2 = new User();
        booker2.setName("booker2");
        booker2.setEmail("booker2@mail.ru");
        User savedBooker2 = userRepository.save(booker2);

        User owner = new User();
        owner.setName("owner");
        owner.setEmail("owner@mail.ru");
        User savedOwner = userRepository.save(owner);

        Item item = new Item();
        item.setName("item");
        item.setDescription("desc");
        item.setAvailable(true);
        item.setOwner(savedOwner);
        Item savedItem = itemRepository.save(item);

        Booking booking = new Booking();
        booking.setStart(now.plusHours(1));
        booking.setEnd(now.plusHours(2));
        booking.setStatus(Status.APPROVED);
        booking.setItem(savedItem);
        booking.setBooker(savedBooker);
        Booking sBooking = bookingRepository.save(booking);

        Booking booking2 = new Booking();
        booking2.setStart(now.plusHours(3));
        booking2.setEnd(now.plusHours(4));
        booking2.setStatus(Status.APPROVED);
        booking2.setItem(savedItem);
        booking2.setBooker(savedBooker2);
        bookingRepository.save(booking2);

        Booking booking3 = new Booking();
        booking3.setStart(now.plusHours(6));
        booking3.setEnd(now.plusHours(10));
        booking3.setStatus(Status.APPROVED);
        booking3.setItem(savedItem);
        booking3.setBooker(savedBooker);
        Booking sBooking3 = bookingRepository.save(booking3);

        List<BookingModelDto> actualBookingsUser = bookingService
                .getAllBookingByUser(savedBooker.getId(), "ALL", pageable);

        assertNotNull(actualBookingsUser);
        assertEquals(actualBookingsUser.size(), 2);
        assertEquals(actualBookingsUser.get(0).getId(), sBooking3.getId());
        assertEquals(actualBookingsUser.get(1).getId(), sBooking.getId());
    }

    @Test
    void getAllBookingByOwner() {
        LocalDateTime now = LocalDateTime.now();
        Pageable pageable =  PageRequest.of(0, 10);

        User booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@mail.ru");
        User savedBooker = userRepository.save(booker);

        User booker2 = new User();
        booker2.setName("booker2");
        booker2.setEmail("booker2@mail.ru");
        User savedBooker2 = userRepository.save(booker2);

        User owner = new User();
        owner.setName("owner");
        owner.setEmail("owner@mail.ru");
        User savedOwner = userRepository.save(owner);

        Item item = new Item();
        item.setName("item");
        item.setDescription("desc");
        item.setAvailable(true);
        item.setOwner(savedOwner);
        Item savedItem = itemRepository.save(item);

        Item item2 = new Item();
        item2.setName("item2");
        item2.setDescription("desc2");
        item2.setAvailable(true);
        item2.setOwner(booker2);
        Item savedItem2 = itemRepository.save(item2);

        Booking booking = new Booking();
        booking.setStart(now.plusHours(1));
        booking.setEnd(now.plusHours(2));
        booking.setStatus(Status.APPROVED);
        booking.setItem(savedItem);
        booking.setBooker(savedBooker);
        Booking sBooking = bookingRepository.save(booking);

        Booking booking2 = new Booking();
        booking2.setStart(now.plusHours(3));
        booking2.setEnd(now.plusHours(4));
        booking2.setStatus(Status.APPROVED);
        booking2.setItem(savedItem);
        booking2.setBooker(savedBooker2);
        Booking sBooking2 = bookingRepository.save(booking2);

        Booking booking3 = new Booking();
        booking3.setStart(now.plusHours(6));
        booking3.setEnd(now.plusHours(10));
        booking3.setStatus(Status.APPROVED);
        booking3.setItem(savedItem2);
        booking3.setBooker(savedBooker);
        bookingRepository.save(booking3);

        List<BookingModelDto> actualBookingsUser = bookingService
                .getAllBookingByOwner(savedOwner.getId(), "ALL", pageable);

        assertNotNull(actualBookingsUser);
        assertEquals(actualBookingsUser.size(), 2);
        assertEquals(actualBookingsUser.get(0).getId(), sBooking2.getId());
        assertEquals(actualBookingsUser.get(1).getId(), sBooking.getId());
    }

}