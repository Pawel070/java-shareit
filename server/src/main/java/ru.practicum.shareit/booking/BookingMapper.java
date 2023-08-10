package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.dto.BookingModelDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BookingMapper {

    public static BookingModelDto tobookingModelDto(Booking booking) {
        return new BookingModelDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem(),
                booking.getBooker(),
                booking.getStatus()
        );
    }

    public static BookingInfoDto bookingRequestDto(Booking booking) {
        return new BookingInfoDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem().getId()
        );
    }

    public static Booking toBooking(BookingInfoDto bookingInfoDto, Item item, User user) {
        return Booking.builder()
                .id(bookingInfoDto.getId())
                .start(bookingInfoDto.getStart())
                .end(bookingInfoDto.getEnd())
                .item(item)
                .booker(user)
                .status(Status.WAITING)
                .build();
    }

    public static BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .build();
    }
}
