package ru.practicum.shareit.booking;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.dto.BookingModelDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

@Mapper(componentModel = "spring", uses = BookingMapper.class, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface BookingMapper {

    Booking toBooking(BookingDto bookingDto);

    @Mapping(target = "bookerId", source = "booker.id")
    BookingInfoDto toBookingInfoDto(Booking booking);

    default BookingModelDto toBookingModelDto(Booking booking) {
        return BookingModelDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(ItemDto.builder()
                        .id(booking.getItem().getId())
                        .name(booking.getItem().getName())
                        .build())
                .booker(UserDto.builder()
                        .id(booking.getBooker().getId())
                        .build())
                .status(booking.getStatus())
                .build();
    }

}
