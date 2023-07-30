package ru.practicum.shareit.booking.service;


import static ru.practicum.shareit.service.MyConstants.SORT;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.dto.BookingModelDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.expections.NotFoundException;
import ru.practicum.shareit.expections.ServerError;
import ru.practicum.shareit.expections.UnsupportedState;
import ru.practicum.shareit.expections.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.service.State;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

@Slf4j
@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {

    private BookingRepository repository;
    private ItemRepository itemRepository;
    private UserRepository userRepository;
    private BookingMapper mapper;
    private UserService userService;
    private ItemService itemService;

    @Override
    public BookingModelDto create(BookingDto bookingDto, Long bookerId) {
        log.info("BookingServiceImpl: isExistUser - create {} - {} ", bookingDto, bookerId);
        Long itemId = bookingDto.getItemId();
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("BookingServiceImpl: Вещь с УИН " + itemId + " не существует."));
        if (!item.getAvailable()) {
            throw new ValidationException("BookingServiceImpl: Вещь с УИН " + bookingDto.getItemId() + " не может быть забронирована.");
        }
        if (bookingDto.getEnd() == null || bookingDto.getStart() == null) {
            throw new ValidationException("BookingServiceImpl: Неопределённое время бронирования.");
        }
        if (!bookingDto.getEnd().isAfter(bookingDto.getStart())) {
            throw new ValidationException("BookingServiceImpl: Время окончания бронирования не может быть раньше времени начала бронирования.");
        }
        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new ServerError("BookingServiceImpl: Пользователь с УИН " + itemId + " не существует."));
        if (booker.getId().equals(item.getOwner().getId())) {
            throw new NotFoundException("BookingServiceImpl: Владелец не может забронировать свою вещь с УИН " + bookingDto.getItemId());
        }
        log.info("BookingServiceImpl: Dto {}, Вещь {} , УИН брoнирующего {} , user {} ", bookingDto, item, bookerId, booker);
        Booking booking = mapper.toBooking(bookingDto);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(Status.WAITING);
        return mapper.toBookingModelDto(repository.save(booking));
    }

    @Override
    public BookingModelDto update(Long bookingId, Long userId, Boolean accepted) {
        log.info("BookingServiceImpl: isExistUser - update");
        userService.isExistUser(userId);
        Booking booking = repository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("BookingServiceImpl: Такого бронирования УИН " + bookingId + " нет."));
        if (booking.getEnd().isBefore(LocalDateTime.now())) {
            throw new ValidationException("BookingServiceImpl: Закончилось время бронирования.");
        }
        if (booking.getBooker().getId().equals(userId)) {
            if (accepted) {
                throw new NotFoundException("BookingServiceImpl: Бронирование подтверждает владелец.");
            } else {
                booking.setStatus(Status.CANCELED);
                log.info("BookingServiceImpl: Пользователь с УИН  {} отменил бронирование УИН {}", userId, bookingId);
            }
        } else if (itemService.isCheckItemOwner(booking.getItem().getId(), userId) &&
                booking.getStatus() != Status.CANCELED) {
            if (booking.getStatus() != Status.WAITING) {
                throw new ValidationException("BookingServiceImpl: Бронирование подтверждено.");
            }
            if (accepted) {
                booking.setStatus(Status.APPROVED);
                log.info("BookingServiceImpl: Пользователь с УИН {} подтвердил бронирование УИН {}", userId, bookingId);
            } else {
                booking.setStatus(Status.REJECTED);
                log.info("BookingServiceImpl: Пользователь с УИН {} отклонил бронирование УИН {}", userId, bookingId);
            }
        } else {
            if (booking.getStatus() == Status.CANCELED) {
                throw new ValidationException("BookingServiceImpl: Отмена бронирования");
            } else {
                throw new ValidationException("BookingServiceImpl: Бронирование подтверждает владелец.");
            }
        }
        return mapper.toBookingModelDto(repository.save(booking));
    }

    @Override
    public BookingModelDto getBookingById(Long bookingId, Long userId) {
        log.info("BookingServiceImpl: isExistUser - getBookingById");
        userService.isExistUser(userId);
        Booking booking = repository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("BookingServiceImpl: Такого бронирования УИН " + bookingId + " нет."));
        if (booking.getBooker().getId().equals(userId) || itemService.isCheckItemOwner(booking.getItem().getId(), userId)) {
            return mapper.toBookingModelDto(booking);
        } else {
            throw new NotFoundException("BookingServiceImpl: Данные бронирования доступны владельцу и бронирующему.");
        }
    }

    @Override
    public List<BookingModelDto> getBookings(String state, Long userId) {
        State stateS = getState(state);
        log.info("BookingServiceImpl: isExistUser - getBookings {} ", stateS);
        userService.isExistUser(userId);
        List<Booking> bookings = new ArrayList<>();
        Sort sortByStartDesc = Sort.by(Sort.Direction.DESC, "start");
        switch (stateS) {
            case ALL:
                bookings = repository.findAllByBooker_IdOrderByStartDesc(userId);
                break;
            case PAST:
                bookings = repository.findAllByBooker_IdAndEndIsBefore(userId, LocalDateTime.now(), SORT);
                break;
            case FUTURE:
                bookings = repository.findAllByBooker_IdAndStartIsAfter(userId, LocalDateTime.now(), SORT);
                break;
            case CURRENT:
                bookings = repository.findAllByBooker_IdAndStartIsBeforeAndEndIsAfter(
                        userId, LocalDateTime.now(), LocalDateTime.now(), SORT);
                break;
            case WAITING:
                bookings = repository.findAllByBooker_IdAndStatus(userId, Status.WAITING);
                break;
            case REJECTED:
                bookings = repository.findAllByBooker_IdAndStatus(userId, Status.REJECTED);
                break;

        }
        return bookings.isEmpty() ? Collections.emptyList() : bookings.stream()
                .map(mapper::toBookingModelDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingModelDto> getBookingsOwner(String state, Long userId) {
        State stateS = getState(state);
        log.info("BookingServiceImpl: isExistUser - getBookingsOwner {} ", stateS);
        userService.isExistUser(userId);
        List<Booking> bookings = new ArrayList<>();
        Sort sortByStartDesc = Sort.by(Sort.Direction.DESC, "start");
        switch (stateS) {
            case ALL:
                bookings = repository.findAllByItem_Owner_IdOrderByStartDesc(userId);
                break;
            case PAST:
                bookings = repository.findAllByItem_Owner_IdAndEndIsBefore(userId, LocalDateTime.now(), SORT);
                break;
            case FUTURE:
                bookings = repository.findAllByItem_Owner_IdAndStartIsAfter(userId, LocalDateTime.now(), SORT);
                break;
            case CURRENT:
                bookings = repository.findAllByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(
                        userId, LocalDateTime.now(), LocalDateTime.now(), SORT);
                break;
            case WAITING:
                bookings = repository.findAllByItem_Owner_IdAndStatus(userId, Status.WAITING);
                break;
            case REJECTED:
                bookings = repository.findAllByItem_Owner_IdAndStatus(userId, Status.REJECTED);
                break;
        }
        return bookings.stream()
                .map(mapper::toBookingModelDto)
                .collect(Collectors.toList());
    }

    @Override
    public BookingInfoDto getLastBooking(Long itemId) {
        return mapper.toBookingInfoDto(repository.findFirstByItem_IdAndEndBeforeOrderByEndDesc(itemId,
                LocalDateTime.now()));
    }

    @Override
    public BookingInfoDto getNextBooking(Long itemId) {
        return mapper.toBookingInfoDto(repository.findFirstByItem_IdAndStartAfterOrderByStartAsc(itemId,
                LocalDateTime.now()));
    }

    @Override
    public Booking getBookingWithUserBookedItem(Long itemId, Long userId) {
        return repository.findFirstByItem_IdAndBooker_IdAndEndIsBeforeAndStatus(itemId,
                userId, LocalDateTime.now(), Status.APPROVED);
    }


    public BookingInfoDto getBookingLast(Long itemId) {
        log.info("CheckEntity: Проверка последнего бронирования с УИН {}", itemId);
        return getLastBooking(itemId);
    }

    public BookingInfoDto getBookingNext(Long itemId) {
        log.info("CheckEntity: Проверка следующего бронирования с УИН {}", itemId);
        return getNextBooking(itemId);
    }

    public Booking getUserBookingBookedItem(Long itemId, Long userId) {
        log.info("CheckEntity: Проверка наличия бронирования {} -> {} ", itemId, userId);
        return getBookingWithUserBookedItem(itemId, userId);
    }

    private State getState(String state) {
        State stateS;
        try {
            stateS = State.valueOf(state);
        } catch (IllegalArgumentException exception) {
            throw new UnsupportedState("Unknown state: " + state);
        }
        return stateS;
    }

}
