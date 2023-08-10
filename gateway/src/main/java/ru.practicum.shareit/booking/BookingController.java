package ru.practicum.shareit.booking;

import static ru.practicum.shareit.MyConstants.USER_ID;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

@Slf4j
@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestHeader(USER_ID) long userId,
                                              @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Получение бронирования с state {}, userId {}, from {}, size {} ", stateParam, userId, from, size);
        return bookingClient.getBookings(userId, state, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> addBooking(@RequestHeader(USER_ID) Long userId,
                                             @RequestBody @Valid BookItemRequestDto requestDto) {
        log.info("Создание бронирования {}, userId {} ", requestDto, userId);
        return bookingClient.addBooking(userId, requestDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@RequestHeader(USER_ID) Long userId,
                                                 @PathVariable("bookingId") Long bookingId,
                                                 @RequestParam Boolean approved) {
        log.info("Изменение бронирования {}, userId {}, status {} ", bookingId, userId, approved);
        return bookingClient.approvedBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader(USER_ID) long userId,
                                             @PathVariable Long bookingId) {
        log.info("Получение бронирования {}, userId {} ", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingsByUserId(@RequestHeader(USER_ID) long userId,
                                                      @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                                      @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                      @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Получение бронирования с state {}, userId {}, from {}, size {} ", stateParam, userId, from, size);
        return bookingClient.getAllBookingsByUserId(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsByOwnerId(@RequestHeader(USER_ID) Long userId,
                                                       @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                                       @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                                       Integer from,
                                                       @Positive @RequestParam(name = "size", defaultValue = "10")
                                                       Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        return bookingClient.getAllBookingsByOwnerId(userId, state, from, size);
    }

    @GetMapping("/item/{itemId}")
    public ResponseEntity<Object> getAllBookingsOfItem(@RequestHeader(USER_ID) long userId,
                                                       @PathVariable(name = "itemId") Long itemId) {
        log.info("Поиск бронирований itemId {}, userId {} ", itemId, userId);
        return bookingClient.getAllBookingsOfItem(itemId, userId);
    }
    
}
