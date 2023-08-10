package ru.practicum.shareit.booking;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class BookingMapperTest {

    private Booking booking;
    private BookingInfoDto bookingInfoDto;
    private User user;
    private Item item;

    @BeforeEach
    public void setUp() {
        user = new User(1L, "Katya", "katya@user.com");
        item = new Item(1L, "itemNameOne", "itemDescriptionOne", true, user, null);
        booking = new Booking(1L, LocalDateTime.now().minusDays(10),
                LocalDateTime.now().minusDays(5), item, user, Status.APPROVED);
        bookingInfoDto = new bookingInfoDto(1L, LocalDateTime.now().minusDays(10),
                LocalDateTime.now().minusDays(5), item.getId());
    }

    @Test
    public void toBookingResponseDtoTest() {
        BookingInfoDto responseDto = BookingMapper.toBookingResponseDto(booking);

        assertEquals(responseDto.getId(), booking.getId());
        assertEquals(responseDto.getStart(), booking.getStart());
        assertEquals(responseDto.getEnd(), booking.getEnd());
        assertEquals(responseDto.getItem(), booking.getItem());
        assertEquals(responseDto.getBooker(), booking.getBooker());
        assertEquals(responseDto.getStatus(), booking.getStatus());
    }

    @Test
    public void toBookingRequestDtoTest() {
        BookingInfoDto requestDto = BookingMapper.bookingRequestDto(booking);

        assertEquals(requestDto.getId(), booking.getId());
        assertEquals(requestDto.getStart(), booking.getStart());
        assertEquals(requestDto.getEnd(), booking.getEnd());
        assertEquals(requestDto.getItemId(), booking.getItem().getId());
    }

    @Test
    public void toBooking() {
        Booking booking1 = BookingMapper.toBooking(bookingInfoDto, item, user);

        assertEquals(booking1.getId(), bookingInfoDto.getId());
        assertEquals(booking1.getStart(), bookingInfoDto.getStart());
        assertEquals(booking1.getEnd(), bookingInfoDto.getEnd());
        assertEquals(booking1.getItem().getId(), bookingInfoDto.getItemId());
    }

    @Test
    public void toBookingItemDto() {
        BookingItemDto bookingItemDto1 = BookingMapper.toBookingItemDto(booking);

        assertEquals(bookingItemDto1.getId(), booking.getId());
        assertEquals(bookingItemDto1.getBookerId(), booking.getBooker().getId());
        assertEquals(bookingItemDto1.getStart(), booking.getStart());
        assertEquals(bookingItemDto1.getEnd(), booking.getEnd());
        assertEquals(bookingItemDto1.getStatus(), booking.getStatus());
    }
}
