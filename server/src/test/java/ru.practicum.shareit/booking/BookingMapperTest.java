package ru.practicum.shareit.booking;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.shareit.booking.BookingMapper.bookingQuerytDto;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.dto.BookingModelDto;
import ru.practicum.shareit.booking.dto.BookingQueryDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public class BookingMapperTest {

    private Booking booking;
    private BookingModelDto bookingModelDto;
    private BookingInfoDto bookingInfolDto;
    private User user;
    private Item item;
    private UserDto userDto;
    private ItemDto itemDto;

    @BeforeEach
    public void setUp() {
        user = new User(1L, "Dydy", "user@mail.ru");
        item = new Item(1L, "itemName1", "itemDescription1", true, user, null);
        userDto = new UserDto(1L, "Dydy", "user@mail.ru");
        itemDto = new ItemDto(1L, "itemName1", "itemDescription1", true, user, null);
        booking = new Booking(1L, LocalDateTime.now().minusDays(10),
                LocalDateTime.now().minusDays(5), item, user, Status.APPROVED);
        bookingModelDto = new BookingModelDto(1L, LocalDateTime.now().minusDays(10),
                LocalDateTime.now().minusDays(5), itemDto, userDto, Status.APPROVED);
        bookingInfolDto = new BookingInfoDto(1L, 2L, LocalDateTime.now().minusDays(10),
                LocalDateTime.now().minusDays(5));

    }

    @Test
    public void toBookingResponseDtoTest() {
        BookingModelDto bookingModelDto = BookingMapper.toBookingModelDto(booking);

        assertEquals(bookingModelDto.getId(), booking.getId());
        assertEquals(bookingModelDto.getStart(), booking.getStart());
        assertEquals(bookingModelDto.getEnd(), booking.getEnd());

    }

    @Test
    public void toBookingRequestDtoTest() {
        BookingQueryDto requestDto = bookingQuerytDto(booking);

        assertEquals(requestDto.getId(), booking.getId());
        assertEquals(requestDto.getStart(), booking.getStart());
        assertEquals(requestDto.getEnd(), booking.getEnd());
        assertEquals(requestDto.getItemId(), booking.getItem().getId());
    }

    @Test
    public void toBooking() {
        Booking booking1 = BookingMapper.toBooking(bookingInfolDto, item, user);

        assertEquals(booking1.getId(), bookingInfolDto.getId());
        assertEquals(booking1.getStart(), bookingInfolDto.getStart());
        assertEquals(booking1.getEnd(), bookingInfolDto.getEnd());
        assertEquals(booking1.getItem().getId(), bookingInfolDto.getId());
    }

    @Test
    public void toBookingItemDto() {
        BookingInfoDto bookingInfoDto1 = BookingMapper.toBookingInfoDto(booking);

        assertEquals(bookingInfoDto1.getId(), booking.getId());
        assertEquals(bookingInfoDto1.getBookerId(), booking.getBooker().getId());
        assertEquals(bookingInfoDto1.getStart(), booking.getStart());
        assertEquals(bookingInfoDto1.getEnd(), booking.getEnd());
    }
}
