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
    private final UserService userService;
    private final ItemService itemService;
    private final BookingService bookingService;

    @Autowired
    public CheckEntity(UserService userService, ItemService itemService, BookingService bookingService) {
        this.userService = userService;
        this.itemService = itemService;
        this.bookingService = bookingService;
    }

    public boolean isExistUser(Long userId) {
        log.info("CheckEntity: Проверка наличия пользователя с УИН {}", userId);
        return userService.getUser(userId) != null;
    }

    public void deleteItemsByUser(Long userId) {
        log.info("CheckEntity: Удаление запасов пользователя с УИН {}", userId);
        itemService.deleteItemsByOwner(userId);
    }

    public boolean isCheckAvailableItem(Long itemId) {
        log.info("CheckEntity: Проверка добавления запасов вещи с УИН {}", itemId);
        return itemService.findItemById(itemId).getAvailable();
    }

    public boolean isCheckItemOwner(Long itemId, Long userId) {
        log.info("CheckEntity: Проверка является ли пользователь {} владельцем вещи с УИН {}", userId, itemId);
        return itemService.getItemsByOwner(userId).stream()
                .anyMatch(i -> i.getId().equals(itemId));
    }

    public User findUserById(Long userId) {
        log.info("CheckEntity: Проверка наличия пользователя с УИН {}", userId);
        return userService.findUserById(userId);
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

    public List<CommentDto> getCommentsByItemId(Long itemId) {
        log.info("CheckEntity: Проверка наличия комментариев по вещи УИН {}", itemId);
        return itemService.getCommentsByItemId(itemId);
    }

}
