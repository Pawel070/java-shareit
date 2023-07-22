package ru.practicum.shareit.booking;

import static ru.practicum.shareit.service.MyConstants.USER_ID;

import javax.validation.Valid;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingQueryDto;
import ru.practicum.shareit.booking.service.BookingService;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService service;

    @Autowired
    public BookingController(BookingService bookingService) {
        service = bookingService;
    }

    @ResponseBody
    @PostMapping
    public BookingDto create(@Valid @RequestBody BookingQueryDto bookingQueryDto,
                             @RequestHeader(USER_ID) Long bookerId) {
        log.info("BookingController: Получен POST-запрос на бронирование от пользователя с УИН {}", bookerId);
        return service.create(bookingQueryDto, bookerId);
    }

    @ResponseBody
    @PatchMapping("/{bookingId}")
    public BookingDto update(@PathVariable Long bookingId,
                             @RequestHeader(USER_ID) Long userId,
                             @RequestParam Boolean approved) {
        log.info("BookingController: Получен PATCH-запрос на обновление статуса бронирования с УИН {}", bookingId);
        return service.update(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@PathVariable Long bookingId, @RequestHeader(USER_ID) Long userId) {
        log.info("BookingController: Получен GET-запрос на получение бронирования с УИН {}", bookingId);
        return service.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getBookings(@RequestParam(name = "state", defaultValue = "ALL") String state,
                                        @RequestHeader(USER_ID) Long userId) {
        log.info("BookingController: Получен GET-запрос с параметром STATE = {} списка бронирований " +
                "пользователя с УИН {}", state, userId);
        return service.getBookings(state, userId);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsOwner(@RequestParam(name = "state", defaultValue = "ALL") String state,
                                             @RequestHeader(USER_ID) Long userId) {
        log.info("BookingController: Получен GET-запрос с параметром STATE = {} на получение списка бронирований " +
                "вещей пользователя с УИН {}", state, userId);
        return service.getBookingsOwner(state, userId);
    }
}
