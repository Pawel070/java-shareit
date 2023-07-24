package ru.practicum.shareit.service;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

@Slf4j
@Service
public class CheckEntity {
//    private UserService userService;
    private ItemService itemService;
    private BookingService bookingService;

    @Autowired
    public CheckEntity(UserService userService, BookingService bookingService) {
//        this.userService = userService;
        this.bookingService = bookingService;
//        this.itemService = itemService;
    }

    public BookingInfoDto getBookingLast(Long itemId) {
        log.info("CheckEntity: Проверка последнего бронирования с УИН {}", itemId);
        return bookingService.getLastBooking(itemId);
    }

    public BookingInfoDto getBookingNext(Long itemId) {
        log.info("CheckEntity: Проверка следующего бронирования с УИН {}", itemId);
        return bookingService.getNextBooking(itemId);
    }

    public Booking getUserBookingBookedItem(Long itemId, Long userId) {
        log.info("CheckEntity: Проверка наличия бронирования {} -> {} ", itemId, userId);
        return bookingService.getBookingWithUserBookedItem(itemId, userId);
    }

}
