package ru.practicum.shareit.item.service;

import static java.util.stream.Collectors.toList;
import static ru.practicum.shareit.booking.BookingMapper.toBookingInfoDto;

import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingRepository;
import ru.practicum.shareit.expections.ConstraintViolationException;
import ru.practicum.shareit.expections.EntityNotAvailable;
import ru.practicum.shareit.expections.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository repository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final BookingRepository bookingRepository;

    @Transactional
    @Override
    public ItemDto create(ItemDto itemDto, Long id) {
        log.info("ItemServiceImpl: Получен POST-запрос на создание вещи");
        User owner = userRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Отсутствует пользователь c УИН " + id));
        ItemRequest request = null;
        if (itemDto.getRequestId() != 0) {
            request = itemRequestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException(
                            "ItemServiceImpl create: Запроса с УИН " + itemDto.getRequestId() + " не существует."));
        }
        Item item = ItemMapper.toItem(itemDto, owner, request);
        return ItemMapper.toItemDto(repository.save(item));
    }

    @Transactional
    @Override
    public CommentDto createComment(CommentDto commentDto, Long itemId, Long userId) {
        log.info("ItemServiceImpl: Получен POST-запрос на создание отзыва пользователем с УИН {}", userId);
        Item item = repository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("ItemServiceImpl createComment: Вещь с УИН " + itemId + " не существует."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("ItemServiceImpl createComment: Пльзаватель с УИН " + userId + " не существует."));
        if (bookingRepository.isItemWasUsedByUser(itemId, userId, LocalDateTime.now())) {
            Comment comment = new Comment(commentDto.getId(), commentDto.getText(), item, user, LocalDateTime.now());
            return CommentMapper.toCommentDto(commentRepository.save(comment));
        } else {
            throw new EntityNotAvailable("ItemServiceImpl createComment: Данный пользователь вещь не бронировал!");
        }
    }

    @Override
    public List<ItemInfoDto> getItemsByOwner(Long id, Pageable pageable) {
        if (id != null && id > 0) {
            userRepository.findById(id).orElseThrow(() -> new NotFoundException("Отсутствует пользователь c id " + id));
            List<Item> items = repository.findAllByOwnerId(id, pageable);
            return getItemDto(items);
        }
        throw new ConstraintViolationException("идентификатор пользователя отрицательный или отсутствует");
    }

    @Override
    public ItemInfoDto getItemById(Long itemId, Long userId) {
        log.info("ItemServiceImpl getItemById: Получен GET-запрос на получение вещи с УИН {}", itemId);
        if (!(userId > 0 && itemId > 0)) {
            throw new ConstraintViolationException("идентификатор пользователя отрицательный или отсутствует");
        }
        Item item = repository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("ItemServiceImpl getItemById: Вещь с УИН " + itemId + " не существует."));

        Booking lastBooking = bookingRepository.findPastOwnerBookings(item.getId(), userId, LocalDateTime.now())
                .stream()
                .min(Comparator.comparing(Booking::getEnd))
                .orElse(null);

        log.info("item {}, userId {} ", item, userId);
        List<Booking> bookings = bookingRepository.findPastOwnerBookings(item.getId(), userId, LocalDateTime.now());
        log.info("bookings {} ", bookings);
        Booking nextBooking = bookings.stream().max(Comparator.comparing(Booking::getStart)).orElse(null);
        ItemInfoDto itemInfoDto;
        itemInfoDto = ItemMapper.toItemInfoDto(item, null, null, Collections.emptyList());
        List<CommentDto> commentsDto = commentRepository.findAllByItem_Id(item.getId()).stream()
                .map(CommentMapper::toCommentDto).collect(Collectors.toList());
        if (lastBooking != null) {
            itemInfoDto.setLastBooking(toBookingInfoDto(lastBooking));
        }
        if (nextBooking != null) {
            itemInfoDto.setNextBooking(toBookingInfoDto(nextBooking));
        }
        if (commentsDto != null) {
            itemInfoDto.setComments(commentsDto);
        }
        return itemInfoDto;
    }

    private List<ItemInfoDto> getItemDto(List<Item> items) {
        List<ItemInfoDto> list = new ArrayList<>();
        ItemInfoDto itemInfoDto;
        for (Item item : items) {
            Booking lastBooking = bookingLast(item);
            Booking nextBooking = bookingNext(item);
            List<CommentDto> commentsDto = commentDto(item);
            itemInfoDto = ItemMapper.toItemInfoDto(item, null, null, Collections.emptyList());
            if (lastBooking != null) {
                itemInfoDto.setLastBooking(toBookingInfoDto(lastBooking));
            }
            if (nextBooking != null) {
                itemInfoDto.setNextBooking(toBookingInfoDto(nextBooking));
            }
            if (commentsDto != null) {
                itemInfoDto.setComments(commentsDto);
            }
            list.add(itemInfoDto);
        }
        return list;
    }

    private Booking bookingLast(Item item) {
        log.info("item {} ", item);
        List<Booking> bookings;
        Booking booking = null;
        try {
            bookings = bookingRepository.findAllByItem_IdAndStartBeforeOrderByStartDesc(item.getId(), LocalDateTime.now());
            log.info("bookings {} ", bookings);
            booking = bookings.stream().min(Comparator.comparing(Booking::getEnd)).orElse(null);
            log.info("booking {} ", booking);
        } catch (Exception exception) {
            log.info("Проблема запроса к базе bookingRepository.findAllByItem_IdAndStartBeforeOrderByStartDesc");
        }
        return booking;
    }

    private Booking bookingNext(Item item) {
        log.info("item {} ", item);
        List<Booking> bookings;
        Booking booking = null;
        try {
            bookings = bookingRepository.findAllByItem_IdAndStartAfterOrderByStartDesc(item.getId(), LocalDateTime.now());
            log.info("bookings {} ", bookings);
            booking = bookings.stream().max(Comparator.comparing(Booking::getStart)).orElse(null);
            log.info("booking {} ", booking);
        } catch (Exception exception) {
            log.info("Проблема запроса к базе bookingRepository.findAllByItem_IdAndStartAfterOrderByStartDesc");
        }
        return booking;
    }

    private List<CommentDto> commentDto(Item item) {
        log.info("item {} ", item);
        return commentRepository.findAllByItem_Id(item.getId())
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ItemDto update(ItemDto itemDto, Long userId, Long itemId) {
        log.info("ItemServiceImpl update: Получен PUT-запрос userId > {} на обновление вещи с УИН {}", userId, itemId);
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("ItemServiceImpl update: УИН пользователя неверный."));
        Item oldItem = repository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("ItemServiceImpl update: У пользователя нет такой вещи."));
        if (!oldItem.getOwner().getId().equals(owner.getId())) {
            throw new NotFoundException("ItemServiceImpl update: У пользователя нет такой вещи.");
        }
        itemDto.setId(itemId);
        Item item = ItemMapper.updatedItem(itemDto, oldItem);
        item.setOwner(owner);
        repository.save(item);
        log.info("ItemServiceImpl update: Вещь {} изменена пользователем с УИН {}", item, userId);
        return ItemMapper.toItemDto(item);
    }

    @Transactional
    @Override
    public void delete(Long itemId, Long id) {
        log.info("ItemServiceImpl delete: Получен DELETE-запрос на удаление вещи с УИН {}", itemId);
        Item item = repository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с УИН " + itemId + " не существует."));
        repository.deleteById(itemId);
    }

    @Transactional
    @Override
    public void deleteItemsByOwner(Long id) {
        log.info("ItemServiceImpl deleteItemsByOwner: Получен DELETE-запрос на удаление всех вещей пользователя с УИН {}", id);
        repository.deleteById(id);
    }

    @Override
    public List<ItemDto> getAvailableItems(Long userId, String text, Pageable pageable) {
        if (text.isBlank()) {
            return new ArrayList<>();
        } else {
            return repository.searchAvailableItems("%" + text + "%", pageable)
                    .stream()
                    .map(ItemMapper::toItemDto)
                    .collect(toList());
        }
    }

    @Override
    public List<CommentDto> getCommentsByItemId(Long itemId) {
        log.info("ItemServiceImpl getCommentsByItemId: Получен GET-запрос на получение отзывов вещи с УИН {}", itemId);
        return commentRepository.findAllByItem_Id(itemId,
                        Sort.by(Sort.Direction.DESC, "created")).stream()
                .map(CommentMapper::toCommentDto)
                .collect(toList());
    }

    @Override
    public Item findItemById(Long id) {
        log.info("ItemServiceImpl findItemById: Получен GET-запрос на получение вещи с УИН {}", id);
        return repository.findById(id).orElseThrow(() ->
                new NotFoundException("ItemServiceImpl findItemById: Не существует вещь с УИН " + id));
    }

    @Transactional
    @Override
    public void deleteItemsByUser(Long userId) {
        log.info("ItemServiceImpl: Удаление запасов пользователя с УИН {}", userId);
        deleteItemsByOwner(userId);
    }

    @Override
    public boolean isCheckItemOwner(Long itemId, Long userId) {
        log.info("ItemServiceImpl deleteItemsByUser: Проверка является ли пользователь {} владельцем вещи с УИН {}", userId, itemId);
        boolean boo = false;
        if (userId.equals(findItemById(itemId).getOwner().getId())) {
            boo = true;
        }
        return boo;
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
