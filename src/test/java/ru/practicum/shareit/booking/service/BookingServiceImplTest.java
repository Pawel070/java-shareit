package ru.practicum.shareit.booking.service;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import javax.persistence.EntityNotFoundException;

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
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingModelDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.expections.UnsupportedState;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.service.EntityCheck;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplTest {

    BookingService bookingService;
    final ItemService itemService;
    final EntityCheck entityCheck;
    BookingModelDto bookingModelDto;
    BookingDto bookingDto;
    Pageable pageable;

    @MockBean
    UserRepository userRepository;

    @MockBean
    ItemRepository itemRepository;

    @MockBean
    BookingRepository bookingRepository;

    @Autowired
    ItemMapper mapper;

    @Autowired
    BookingMapper bookingMapper;

    User user;
    User owner;
    Item item;
    Booking booking;
    UserDto userDto;
    ItemDto itemDto;

    @BeforeEach
    void beforeEach() {
        pageable = PageRequest.of(0, 10);
        bookingService = new BookingServiceImpl(bookingRepository, itemRepository, userRepository, bookingMapper, itemService, entityCheck);
        user = new User(1L, "user", "user@mail.ru");
        owner = new User(2L, "owner", "owner@mail.ru");
        item = new Item(1L, "item", "desc", true, owner, null);
        userDto = new UserDto(1L, "user", "user@mail.ru");
        itemDto = new ItemDto(1L, "item", "desc", true, owner, 0);
        booking = new Booking(
                1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                item,
                user,
                Status.WAITING);
        bookingModelDto = new BookingModelDto(
                1L,
                booking.getStart(),
                booking.getEnd(),
                itemDto,
                userDto,
                Status.WAITING);
        bookingDto = new BookingDto(
                1L,
                booking.getStart(),
                booking.getEnd(),
                item.getId(),
                user.getId(),
                Status.WAITING);
    }

    @Test
    void create() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingModelDto res = bookingService.create(bookingDto, user.getId());

        assertNotNull(res);
        assertEquals(BookingModelDto.class, res.getClass());
        assertEquals(res.getId(), bookingModelDto.getId());
        assertEquals(res.getStart(), bookingModelDto.getStart());
        assertEquals(res.getEnd(), bookingModelDto.getEnd());
        assertEquals(res.getItem().getId(), bookingModelDto.getItem().getId());
        assertEquals(res.getBooker().getId(), bookingModelDto.getBooker().getId());
        assertEquals(res.getStatus(), bookingModelDto.getStatus());
    }

    @Test
    void create_whenItemUnavailable() {
        item.setAvailable(false);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        assertThrows(ru.practicum.shareit.exceptions.EntityNotAvailable.class, () -> bookingService.create(bookingDto, user.getId()));
    }

    @Test
    void create_whenStartAfterEnd() {
        bookingDto.setStart(bookingDto.getEnd().plusDays(2));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        assertThrows(ru.practicum.shareit.exceptions.EntityNotAvailable.class, () -> bookingService.create(bookingDto, user.getId()));
    }

    @Test
    void create_whenBookerIsOwner() {
        item.setOwner(user);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        assertThrows(EntityNotFoundException.class, () -> bookingService.create(bookingDto, user.getId()));
    }

    @Test
    void update() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingModelDto res = bookingService.update(owner.getId(), booking.getId(), true);
        bookingModelDto.setStatus(Status.APPROVED);

        assertNotNull(res);
        assertEquals(BookingModelDto.class, res.getClass());
        assertEquals(res.getId(), bookingModelDto.getId());
        assertEquals(res.getStart(), bookingModelDto.getStart());
        assertEquals(res.getEnd(), bookingModelDto.getEnd());
        assertEquals(res.getItem().getId(), bookingModelDto.getItem().getId());
        assertEquals(res.getBooker().getId(), bookingModelDto.getBooker().getId());
        assertEquals(res.getStatus(), bookingModelDto.getStatus());
    }

    @Test
    void update_whenUserIsNotOwner() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(EntityNotFoundException.class,
                () -> bookingService.update(99L, booking.getId(), true));
    }

    @Test
    void update_whenStatusConfirmed() {
        booking.setStatus(Status.REJECTED);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(ru.practicum.shareit.exceptions.EntityNotAvailable.class,
                () -> bookingService.update(owner.getId(), booking.getId(), true));
    }

    @Test
    void getBookingById_byOwner() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        BookingModelDto res = bookingService.getBookingById(owner.getId(), booking.getId());

        assertNotNull(res);
        assertEquals(BookingModelDto.class, res.getClass());
        assertEquals(res.getId(), bookingModelDto.getId());
        assertEquals(res.getStart(), bookingModelDto.getStart());
        assertEquals(res.getEnd(), bookingModelDto.getEnd());
        assertEquals(res.getItem().getId(), bookingModelDto.getItem().getId());
        assertEquals(res.getBooker().getId(), bookingModelDto.getBooker().getId());
        assertEquals(res.getStatus(), bookingModelDto.getStatus());
    }

    @Test
    void getBookingById_byBooker() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        BookingModelDto res = bookingService.getBookingById(user.getId(), booking.getId());

        assertNotNull(res);
        assertEquals(BookingModelDto.class, res.getClass());
        assertEquals(res.getId(), bookingModelDto.getId());
        assertEquals(res.getStart(), bookingModelDto.getStart());
        assertEquals(res.getEnd(), bookingModelDto.getEnd());
        assertEquals(res.getItem().getId(), bookingModelDto.getItem().getId());
        assertEquals(res.getBooker().getId(), bookingModelDto.getBooker().getId());
        assertEquals(res.getStatus(), bookingModelDto.getStatus());
    }

    @Test
    void getBookingById_byAnotherUser() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(EntityNotFoundException.class, () -> bookingService.getBookingById(5L, booking.getId()));
    }

    @Test
    void getAllBookingByUser_statusIsAll() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findAllByBooker_IdOrderByStartDesc(anyLong(), any()))
                .thenReturn(List.of(booking));

        List<BookingModelDto> res = bookingService.getAllBookingByUser(user.getId(), "ALL", pageable);

        assertNotNull(res);
        assertEquals(res.size(), 1);

        assertEquals(res.get(0).getId(), bookingModelDto.getId());
        assertEquals(res.get(0).getStart(), bookingModelDto.getStart());
        assertEquals(res.get(0).getEnd(), bookingModelDto.getEnd());
        assertEquals(res.get(0).getItem().getId(), bookingModelDto.getItem().getId());
        assertEquals(res.get(0).getBooker().getId(), bookingModelDto.getBooker().getId());
        assertEquals(res.get(0).getStatus(), bookingModelDto.getStatus());
    }

    @Test
    void getAllBookingByUser_statusIsPast() {
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        bookingModelDto.setStart(booking.getStart());
        bookingModelDto.setEnd(booking.getEnd());
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findAllByBooker_IdAndEndIsBefore(anyLong(), any(), any()))
                .thenReturn(List.of(booking));

        List<BookingModelDto> res = bookingService.getAllBookingByUser(user.getId(), "PAST", pageable);

        assertNotNull(res);
        assertEquals(res.size(), 1);

        assertEquals(res.get(0).getId(), bookingModelDto.getId());
        assertEquals(res.get(0).getStart(), bookingModelDto.getStart());
        assertEquals(res.get(0).getEnd(), bookingModelDto.getEnd());
        assertEquals(res.get(0).getItem().getId(), bookingModelDto.getItem().getId());
        assertEquals(res.get(0).getBooker().getId(), bookingModelDto.getBooker().getId());
        assertEquals(res.get(0).getStatus(), bookingModelDto.getStatus());
    }

    @Test
    void getAllBookingByUser_statusIsFuture() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findAllByBooker_IdAndStartIsAfter(anyLong(), any(), any()))
                .thenReturn(List.of(booking));

        List<BookingModelDto> res = bookingService.getAllBookingByUser(user.getId(), "FUTURE", pageable);

        assertNotNull(res);
        assertEquals(res.size(), 1);

        assertEquals(res.get(0).getId(), bookingModelDto.getId());
        assertEquals(res.get(0).getStart(), bookingModelDto.getStart());
        assertEquals(res.get(0).getEnd(), bookingModelDto.getEnd());
        assertEquals(res.get(0).getItem().getId(), bookingModelDto.getItem().getId());
        assertEquals(res.get(0).getBooker().getId(), bookingModelDto.getBooker().getId());
        assertEquals(res.get(0).getStatus(), bookingModelDto.getStatus());
    }

    @Test
    void getAllBookingByUser_statusIsCurrent() {
        booking.setStart(LocalDateTime.now().minusDays(2));
        bookingModelDto.setStart(booking.getStart());
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findAllByBooker_IdAndStartIsBeforeAndEndIsAfter(anyLong(), any(), any(), any()))
                .thenReturn(List.of(booking));

        List<BookingModelDto> res = bookingService.getAllBookingByUser(user.getId(), "CURRENT", pageable);

        assertNotNull(res);
        assertEquals(res.size(), 1);

        assertEquals(res.get(0).getId(), bookingModelDto.getId());
        assertEquals(res.get(0).getStart(), bookingModelDto.getStart());
        assertEquals(res.get(0).getEnd(), bookingModelDto.getEnd());
        assertEquals(res.get(0).getItem().getId(), bookingModelDto.getItem().getId());
        assertEquals(res.get(0).getBooker().getId(), bookingModelDto.getBooker().getId());
        assertEquals(res.get(0).getStatus(), bookingModelDto.getStatus());
    }

    @Test
    void getAllBookingByUser_statusIsWaiting() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findAllByBooker_IdAndStatus(anyLong(), any()))
                .thenReturn(List.of(booking));

        List<BookingModelDto> res = bookingService.getAllBookingByUser(user.getId(), "WAITING", pageable);

        assertNotNull(res);
        assertEquals(res.size(), 1);

        assertEquals(res.get(0).getId(), bookingModelDto.getId());
        assertEquals(res.get(0).getStart(), bookingModelDto.getStart());
        assertEquals(res.get(0).getEnd(), bookingModelDto.getEnd());
        assertEquals(res.get(0).getItem().getId(), bookingModelDto.getItem().getId());
        assertEquals(res.get(0).getBooker().getId(), bookingModelDto.getBooker().getId());
        assertEquals(res.get(0).getStatus(), bookingModelDto.getStatus());
    }

    @Test
    void getAllBookingByUser_statusIsRejected() {
        booking.setStatus(Status.REJECTED);
        bookingModelDto.setStatus(Status.REJECTED);
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findAllByBooker_IdAndStatus(anyLong(), any()))
                .thenReturn(List.of(booking));

        List<BookingModelDto> res = bookingService.getAllBookingByUser(user.getId(), "REJECTED", pageable);

        assertNotNull(res);
        assertEquals(res.size(), 1);

        assertEquals(res.get(0).getId(), bookingModelDto.getId());
        assertEquals(res.get(0).getStart(), bookingModelDto.getStart());
        assertEquals(res.get(0).getEnd(), bookingModelDto.getEnd());
        assertEquals(res.get(0).getItem().getId(), bookingModelDto.getItem().getId());
        assertEquals(res.get(0).getBooker().getId(), bookingModelDto.getBooker().getId());
        assertEquals(res.get(0).getStatus(), bookingModelDto.getStatus());
    }

    @Test
    void getAllBookingByUser_wrongUser() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(EntityNotFoundException.class,
                () -> bookingService.getAllBookingByUser(owner.getId(), "ALL", pageable));
    }

    @Test
    void getAllBookingByOwner_statusIsAll() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findAllByItem_Owner_IdOrderByStartDesc(anyLong(), any()))
                .thenReturn(List.of(booking));

        List<BookingModelDto> res = bookingService.getAllBookingByOwner(owner.getId(), "ALL", pageable);

        assertNotNull(res);
        assertEquals(res.size(), 1);

        assertEquals(res.get(0).getId(), bookingModelDto.getId());
        assertEquals(res.get(0).getStart(), bookingModelDto.getStart());
        assertEquals(res.get(0).getEnd(), bookingModelDto.getEnd());
        assertEquals(res.get(0).getItem().getId(), bookingModelDto.getItem().getId());
        assertEquals(res.get(0).getBooker().getId(), bookingModelDto.getBooker().getId());
        assertEquals(res.get(0).getStatus(), bookingModelDto.getStatus());
    }

    @Test
    void getAllBookingByOwner_statusIsPast() {
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        bookingModelDto.setStart(booking.getStart());
        bookingModelDto.setEnd(booking.getEnd());
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findAllByItem_Owner_IdAndEndIsBefore(anyLong(), any(), any()))
                .thenReturn(List.of(booking));

        List<BookingModelDto> res = bookingService.getAllBookingByOwner(owner.getId(), "PAST", pageable);

        assertNotNull(res);
        assertEquals(res.size(), 1);

        assertEquals(res.get(0).getId(), bookingModelDto.getId());
        assertEquals(res.get(0).getStart(), bookingModelDto.getStart());
        assertEquals(res.get(0).getEnd(), bookingModelDto.getEnd());
        assertEquals(res.get(0).getItem().getId(), bookingModelDto.getItem().getId());
        assertEquals(res.get(0).getBooker().getId(), bookingModelDto.getBooker().getId());
        assertEquals(res.get(0).getStatus(), bookingModelDto.getStatus());
    }

    @Test
    void getAllBookingByOwner_statusIsFuture() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findAllByItem_Owner_IdAndStartIsAfter(anyLong(), any(), any()))
                .thenReturn(List.of(booking));

        List<BookingModelDto> res = bookingService.getAllBookingByOwner(owner.getId(), "FUTURE", pageable);

        assertNotNull(res);
        assertEquals(res.size(), 1);

        assertEquals(res.get(0).getId(), bookingModelDto.getId());
        assertEquals(res.get(0).getStart(), bookingModelDto.getStart());
        assertEquals(res.get(0).getEnd(), bookingModelDto.getEnd());
        assertEquals(res.get(0).getItem().getId(), bookingModelDto.getItem().getId());
        assertEquals(res.get(0).getBooker().getId(), bookingModelDto.getBooker().getId());
        assertEquals(res.get(0).getStatus(), bookingModelDto.getStatus());
    }

    @Test
    void getAllBookingByOwner_statusIsCurrent() {
        booking.setStart(LocalDateTime.now().minusDays(2));
        bookingModelDto.setStart(booking.getStart());
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findAllByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(anyLong(), any(), any(), any()))
                .thenReturn(List.of(booking));

        List<BookingModelDto> res = bookingService.getAllBookingByOwner(owner.getId(), "CURRENT", pageable);

        assertNotNull(res);
        assertEquals(res.size(), 1);

        assertEquals(res.get(0).getId(), bookingModelDto.getId());
        assertEquals(res.get(0).getStart(), bookingModelDto.getStart());
        assertEquals(res.get(0).getEnd(), bookingModelDto.getEnd());
        assertEquals(res.get(0).getItem().getId(), bookingModelDto.getItem().getId());
        assertEquals(res.get(0).getBooker().getId(), bookingModelDto.getBooker().getId());
        assertEquals(res.get(0).getStatus(), bookingModelDto.getStatus());
    }

    @Test
    void getAllBookingByOwner_statusIsWaiting() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findAllByItem_Owner_IdAndStatus(anyLong(), any()))
                .thenReturn(List.of(booking));

        List<BookingModelDto> res = bookingService.getAllBookingByOwner(owner.getId(), "WAITING", pageable);

        assertNotNull(res);
        assertEquals(res.size(), 1);

        assertEquals(res.get(0).getId(), bookingModelDto.getId());
        assertEquals(res.get(0).getStart(), bookingModelDto.getStart());
        assertEquals(res.get(0).getEnd(), bookingModelDto.getEnd());
        assertEquals(res.get(0).getItem().getId(), bookingModelDto.getItem().getId());
        assertEquals(res.get(0).getBooker().getId(), bookingModelDto.getBooker().getId());
        assertEquals(res.get(0).getStatus(), bookingModelDto.getStatus());
    }

    @Test
    void getAllBookingByOwner_statusIsRejected() {
        booking.setStatus(Status.REJECTED);
        bookingModelDto.setStatus(Status.REJECTED);
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findAllByItem_Owner_IdAndStatus(anyLong(), any()))
                .thenReturn(List.of(booking));

        List<BookingModelDto> res = bookingService.getAllBookingByOwner(owner.getId(), "REJECTED", pageable);

        assertNotNull(res);
        assertEquals(res.size(), 1);

        assertEquals(res.get(0).getId(), bookingModelDto.getId());
        assertEquals(res.get(0).getStart(), bookingModelDto.getStart());
        assertEquals(res.get(0).getEnd(), bookingModelDto.getEnd());
        assertEquals(res.get(0).getItem().getId(), bookingModelDto.getItem().getId());
        assertEquals(res.get(0).getBooker().getId(), bookingModelDto.getBooker().getId());
        assertEquals(res.get(0).getStatus(), bookingModelDto.getStatus());
    }

    @Test
    void getAllBookingByOwner_wrongUser() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(EntityNotFoundException.class,
                () -> bookingService.getAllBookingByOwner(owner.getId(), "ALL", pageable));
    }

    @Test
    void getAllBookingByOwner_withWrongDataForCalcPageNumber() {
        when(userRepository.existsById(anyLong())).thenReturn(true);

        assertThrows(ru.practicum.shareit.exceptions.EntityNotAvailable.class,
                () -> bookingService.getAllBookingByOwner(owner.getId(), "ALL", pageable));
    }

    @Test
    void getAllBookingByOwner_wrongState() {
        assertThrows(UnsupportedState.class,
                () -> bookingService.getAllBookingByOwner(owner.getId(), "MEOW", pageable));
    }

}