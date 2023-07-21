package ru.practicum.shareit.booking.service;


import java.time.LocalDateTime;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.dto.BookingQueryDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.expections.NotFoundException;
import ru.practicum.shareit.expections.ValidationException;
import ru.practicum.shareit.service.CheckEntity;

@Slf4j
@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository repository;
    private final BookingMapper mapper;
    private final CheckEntity checker;

    @Autowired
    @Lazy
    public BookingServiceImpl(BookingRepository bookingRepository, BookingMapper bookingMapper,
                              CheckEntity checkEntity) {
        repository = bookingRepository;
        mapper = bookingMapper;
        checker = checkEntity;
    }

    @Override
    public BookingDto create(BookingQueryDto bookingQueryDto, Long bookerId) {
        log.info("BookingServiceImpl: isExistUser - create");
        checker.isExistUser(bookerId);
        if (!checker.isCheckAvailableItem(bookingQueryDto.getItemId())) {
            throw new ValidationException("BookingServiceImpl: Вещь с УИН " + bookingQueryDto.getItemId() + " не может быть забронирована.");
        }
        Booking booking = mapper.toBooking(bookingQueryDto, bookerId);
        if (Objects.equals(bookerId, booking.getItem().getId())) {
            throw new NotFoundException("BookingServiceImpl: Владелец не может забронировать свою вещь с УИН " + bookingQueryDto.getItemId());
        }
        return mapper.toBookingDto(repository.save(booking));
    }

    @Override
    public BookingDto update(Long bookingId, Long userId, Boolean approved) {
        log.info("BookingServiceImpl: isExistUser - update");
        checker.isExistUser(userId);
        Booking booking = repository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("BookingServiceImpl: Такого бронирования УИН " + bookingId + " нет."));
        if (booking.getEnd().isBefore(LocalDateTime.now())) {
            throw new ValidationException("BookingServiceImpl: Закончилось время бронирования.");
        }

        if (booking.getBooker().getId().equals(userId)) {
            if (approved) {
                throw new NotFoundException("BookingServiceImpl: Бронирование подтверждает владелец.");
            } else {
                booking.setStatus(Status.CANCELED);
                log.info("BookingServiceImpl: Пользователь с УИН  {} отменил бронирование УИН {}", userId, bookingId);
            }
        } else if (checker.isCheckItemOwner(booking.getItem().getId(), userId) &&
                booking.getStatus() != Status.CANCELED) {
            if (booking.getStatus() != Status.WAITING) {
                throw new ValidationException("BookingServiceImpl: Бронирование подтверждено.");
            }
            if (approved) {
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

        return mapper.toBookingDto(repository.save(booking));
    }

    @Override
    public BookingDto getBookingById(Long bookingId, Long userId) {
        log.info("BookingServiceImpl: isExistUser - getBookingById");
        checker.isExistUser(userId);
        Booking booking = repository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("BookingServiceImpl: Такого бронирования УИН " + bookingId + " нет."));
        if (booking.getBooker().getId().equals(userId) || checker.isCheckItemOwner(booking.getItem().getId(), userId)) {
            return mapper.toBookingDto(booking);
        } else {
            throw new NotFoundException("BookingServiceImpl: Данные бронирования доступны владельцу и бронирующему.");
        }
    }

    @Override
    public List<BookingDto> getBookings(String state, Long userId) {
        log.info("BookingServiceImpl: isExistUser - getBookings");
        checker.isExistUser(userId);
        List<Booking> bookings;
        Sort sortByStartDesc = Sort.by(Sort.Direction.DESC, "start");
        switch (state) {
            case "ALL":
                bookings = repository.findByBookerId(userId, sortByStartDesc);
                break;
            case "CURRENT":
                bookings = repository.findByBookerIdAndStartIsBeforeAndEndIsAfter(userId,
                        LocalDateTime.now(), LocalDateTime.now(), sortByStartDesc);
                break;
            case "PAST":
                bookings = repository.findByBookerIdAndEndIsBefore(userId, LocalDateTime.now(), sortByStartDesc);
                break;
            case "FUTURE":
                bookings = repository.findByBookerIdAndStartIsAfter(userId, LocalDateTime.now(), sortByStartDesc);
                break;
            case "WAITING":
                bookings = repository.findByBookerIdAndStatus(userId, Status.WAITING, sortByStartDesc);
                break;
            case "REJECTED":
                bookings = repository.findByBookerIdAndStatus(userId, Status.REJECTED, sortByStartDesc);
                break;
            default:
                throw new ValidationException("BookingServiceImpl: Неизвестный статус: " + state);
        }
        return bookings.stream()
                .map(mapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getBookingsOwner(String state, Long userId) {
        List<Booking> bookings;
        Sort sortByStartDesc = Sort.by(Sort.Direction.DESC, "start");
        switch (state) {
            case "ALL":
                bookings = repository.findByItem_Owner_Id(userId, sortByStartDesc);
                break;
            case "CURRENT":
                bookings = repository.findByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(userId, LocalDateTime.now(),
                        LocalDateTime.now(), sortByStartDesc);
                break;
            case "PAST":
                bookings = repository.findByItem_Owner_IdAndEndIsBefore(userId, LocalDateTime.now(), sortByStartDesc);
                break;
            case "FUTURE":
                bookings = repository.findByItem_Owner_IdAndStartIsAfter(userId, LocalDateTime.now(),
                        sortByStartDesc);
                break;
            case "WAITING":
                bookings = repository.findByItem_Owner_IdAndStatus(userId, Status.WAITING, sortByStartDesc);
                break;
            case "REJECTED":
                bookings = repository.findByItem_Owner_IdAndStatus(userId, Status.REJECTED, sortByStartDesc);
                break;
            default:
                throw new ValidationException("BookingServiceImpl: Неизвестный статус: " + state);
        }
        return bookings.stream()
                .map(mapper::toBookingDto)
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
}
