package ru.practicum.shareit.booking.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingModelDto;

public interface BookingService {

    BookingModelDto create(BookingDto bookingDto, Long bookerId);

    BookingModelDto update(Long bookingId, Long userId, Boolean approved);

    BookingModelDto getBookingById(Long bookingId, Long userId);

    List<BookingModelDto> getAllBookingByUser(Long userId, String state, Pageable pageable);

    List<BookingModelDto> getAllBookingByOwner(Long userId, String state, Pageable pageable);

    void isCheckFromSize(int from, int size);

}
