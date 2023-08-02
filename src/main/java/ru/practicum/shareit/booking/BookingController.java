package ru.practicum.shareit.booking;

import static ru.practicum.shareit.service.MyConstants.USER_ID;

import javax.validation.Valid;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingModelDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.service.EntityCheckImpl;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService service;
    private final EntityCheckImpl emplyTesting;

    @PostMapping
    public BookingModelDto create(
            @RequestHeader(USER_ID) Long booker,
            @Valid @RequestBody BookingDto bookingDto) {
        log.info("BookingController: Получен POST-запрос на бронирование от пользователя с УИН {} --> {} ", booker, bookingDto);
        return service.create(bookingDto, booker);
    }

    @PatchMapping("/{bookingId}")
    public BookingModelDto update(@PathVariable Long bookingId,
                                  @RequestHeader(USER_ID) Long userId,
                                  @RequestParam Boolean approved) {
        log.info("BookingController: Получен PATCH-запрос на обновление статуса бронирования с УИН {}", bookingId);
        return service.update(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingModelDto getBookingById(@PathVariable Long bookingId, @RequestHeader(USER_ID) Long userId) {
        log.info("BookingController: Получен GET-запрос на получение бронирования с УИН {}", bookingId);
        return service.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingModelDto> getBookingsOwner(
            @RequestHeader(USER_ID) Long userId,
            @RequestParam(value = "state", defaultValue = "ALL", required = false) String state,
            @RequestParam(value = "from", defaultValue = "0", required = false) int from,
            @RequestParam(value = "size", defaultValue = "10", required = false) int size) {
        emplyTesting.isCheckFromSize(from, size);
        log.info("BookingController getBookings: Получен GET-запрос с параметром STATE = {} from = {} size = {} " +
                "списка бронирований пользователя с УИН {}", state, from, size, userId);
        return service.getAllBookingByUser(userId, state, PageRequest.of(from / size, size));
    }

    @GetMapping("/owner")
    public List<BookingModelDto> getAllBookingByOwner(
            @RequestHeader(USER_ID) Long userId,
            @RequestParam(value = "state", defaultValue = "ALL", required = false) String state,
            @RequestParam(value = "from", defaultValue = "0", required = false) int from,
            @RequestParam(value = "size", defaultValue = "10", required = false) int size) {
        emplyTesting.isCheckFromSize(from, size);
        log.info("BookingController getBookingsOwner: Получен GET-запрос с параметром STATE = {} from = {} size = {} на получение списка " +
                "бронирований вещей пользователя с УИН {}", state, from, size, userId);
        return service.getAllBookingByOwner(userId, state, PageRequest.of(from / size, size));
    }

}
