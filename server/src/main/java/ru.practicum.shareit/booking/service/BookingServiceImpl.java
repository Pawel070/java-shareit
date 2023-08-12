package ru.practicum.shareit.booking.service;

import static ru.practicum.shareit.Constants.SORT;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingModelDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.expections.EntityNotAvailable;
import ru.practicum.shareit.expections.NotFoundException;
import ru.practicum.shareit.expections.ServerError;
import ru.practicum.shareit.expections.UnsupportedState;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemRepository;
import ru.practicum.shareit.State;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

@Slf4j
@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository repository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingMapper mapper;

    @Transactional
    @Override
    public BookingModelDto create(BookingDto bookingDto, Long bookerId) {
        log.info("BookingServiceImpl: isExistUser - create {} - {} ", bookingDto, bookerId);
        Long itemId = bookingDto.getItemId();
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("BookingServiceImpl: Вещь с УИН " + itemId + " не существует."));
        if (!item.getAvailable()) {
            throw new EntityNotAvailable("BookingServiceImpl: Вещь с УИН " +
                    bookingDto.getItemId() + " не может быть забронирована.");
        }
        if (bookingDto.getEnd() == null || bookingDto.getStart() == null) {
            throw new EntityNotAvailable("BookingServiceImpl: Неопределённое время бронирования.");
        }
        if (!bookingDto.getEnd().isAfter(bookingDto.getStart())) {
            throw new EntityNotAvailable("BookingServiceImpl: Время окончания бронирования не может быть раньше времени начала бронирования.");
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

    @Transactional
    @Override
    public BookingModelDto update(Long bookingId, Long userId, Boolean accepted) {
        log.info("BookingServiceImpl: isExistUser - update");
        Booking booking = repository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("BookingServiceImpl: Такого бронирования УИН " + bookingId + " нет."));
        Item item = booking.getItem();
        if (!userId.equals(item.getOwner().getId())) {
            throw new NotFoundException("BookingServiceImpl: Пользователь с УИН  " + userId + " не является владельцем вещи " + bookingId);
        }
        if (booking.getStatus().equals(Status.APPROVED) || booking.getStatus().equals(Status.REJECTED)) {
            throw new EntityNotAvailable("BookingServiceImpl: Бронирование подтверждено: " + booking.getStatus());
        }
        if (accepted != null) {
            booking.setStatus(accepted ? Status.APPROVED : Status.REJECTED);
        }
        booking = repository.save(booking);
        return mapper.toBookingModelDto(booking);
    }

    @Override
    public BookingModelDto getBookingById(Long bookingId, Long userId) {
        log.info("BookingServiceImpl: isExistUser - getBookingById");

        Booking booking = repository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("BookingServiceImpl: Такого бронирования УИН " + bookingId + " нет."));
        Item item = booking.getItem();
        if (!userId.equals(item.getOwner().getId()) && !userId.equals(booking.getBooker().getId())) {
            throw new NotFoundException("BookingServiceImpl: Данные бронирования доступны владельцу и бронирующему.");
        }
        return mapper.toBookingModelDto(booking);
    }

    @Override
    public List<BookingModelDto> getAllBookingByUser(Long userId, String ofProcess, Pageable pageable) {
        State state = getState(ofProcess);
        log.info("BookingServiceImpl: isExistUser - getBookings статус {} userId {} ", state, userId);
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("UserServiceImpl findUserById: Пользователь с УИН " + userId + " не существует.");
        }
        List<Booking> bookings = new ArrayList<>();
        switch (state) {
            case ALL:
                bookings = repository.findAllByBooker_IdOrderByStartDesc(userId, pageable);
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
        List<BookingModelDto> bookingModelDtos;
        if (bookings.isEmpty()) bookingModelDtos = Collections.emptyList();
        else bookingModelDtos = bookings.stream()
                .map(BookingMapper::toBookingModelDto)
                .collect(Collectors.toList());
        log.info("==> {} в статусе {} ", bookingModelDtos, state);
        return bookingModelDtos;
    }

    @Override
    public List<BookingModelDto> getAllBookingByOwner(Long userId, String ofProcess, Pageable pageable) {
        State state = getState(ofProcess);
        log.info("BookingServiceImpl: isExistUser - getBookingsOwner {} ", state);
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("UserServiceImpl findUserById: Пользователь с УИН " + userId + " не существует.");
        }
        List<Booking> bookings = new ArrayList<>();
        Sort sortByStartDesc = Sort.by(Sort.Direction.DESC, "start");
        switch (state) {
            case ALL:
                bookings = repository.findAllByItem_Owner_IdOrderByStartDesc(userId, pageable);
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
        List<BookingModelDto> collect = bookings.stream()
                .map(BookingMapper::toBookingModelDto)
                .collect(Collectors.toList());
        log.info("==> {} в статусе {} ", collect, state);
        return collect;
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

    @Override
    public void isCheckFromSize(int from, int size) {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        String messageClass = "";
        if (stackTraceElements.length >= 3) {
            StackTraceElement element = stackTraceElements[2];
            messageClass = element.getClassName() + ":" + element.getMethodName();
        }
        log.info("Проверка from {} и size {} вызов из > {} ", from, size, messageClass);
        if (from < 0 || size < 1) {
            throw new EntityNotAvailable("Ошибочный параметр \"size\" или \"from\"");
        }
    }
}
