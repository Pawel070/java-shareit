package ru.practicum.shareit.item.service;

import static java.util.stream.Collectors.toList;
import static ru.practicum.shareit.service.MyConstants.SORT_ASC;
import static ru.practicum.shareit.service.MyConstants.SORT_DESC;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingRepository;
import ru.practicum.shareit.expections.NotFoundException;
import ru.practicum.shareit.expections.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

@Slf4j
@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private ItemMapper mapper;
    private BookingMapper bookingMapper;
    private ItemRepository repository;
    private UserRepository userRepository;
    private CommentRepository commentRepository;
    private UserService userService;
    private ItemRequestRepository itemRequestRepository;
    private BookingRepository bookingRepository;

    @Override
    public ItemDto create(ItemDto itemDto, Long id) {
        log.info("ItemServiceImpl: Получен POST-запрос на создание вещи");
        User user = userService.findUserById(id);
        Item item = mapper.mapToItemFromItemDto(itemDto);
        item.setOwner(user);
        if (itemDto.getRequestId() != 0) {
            item.setRequest(itemRequestRepository.findById(itemDto.getRequestId()));
        }
        return mapper.toItemDto(repository.save(item));
    }

    @Override
    public CommentDto createComment(CommentDto commentDto, Long itemId, Long userId) {
        log.info("ItemServiceImpl: Получен POST-запрос на создание отзыва пользователем с УИН {}", userId);
        Item item = repository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с УИН " + itemId + " не существует."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пльзаватель с УИН " + userId + " не существует."));
        if (bookingRepository.isItemWasUsedByUser(itemId, userId, LocalDateTime.now())) {
            Comment comment = new Comment(commentDto.getId(), commentDto.getText(), item, user, LocalDateTime.now());
            return mapper.toCommentDto(commentRepository.save(comment));
        } else {
            throw new ValidationException("Данный пользователь вещь не бронировал!");
        }
    }

    @Override
    public List<ItemInfoDto> getItemsByOwner(Long id) {
        Map<Long, Booking> lastBookings = new HashMap<>();
        Map<Long, Booking> nextBookings = new HashMap<>();
        Map<Long, List<Comment>> comments = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        log.info("ItemServiceImpl: Получен GET-запрос на получение списка вещей владельца с УИН {}", id);
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("Пльзаватель с УИН " + id + " не существует.");
        }
        List<Item> items = repository.findByOwner_Id(id);
        List<Long> itemsId = items
                .stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        List<Booking> allLastBookings = bookingRepository
                .findFirstByItem_IdInAndItem_Owner_IdAndStartIsBefore(
                        itemsId,
                        id,
                        now,
                        SORT_DESC);
        List<Booking> allNextBookings = bookingRepository
                .findFirstByItem_IdInAndItem_Owner_IdAndStartIsAfterAndStatusIsNotAndStatusIsNot(
                        itemsId,
                        id,
                        now,
                        Status.CANCELED,
                        Status.REJECTED,
                        SORT_ASC);
        itemsId.forEach(signature -> {
            allLastBookings.stream()
                    .filter(booking -> booking.getItem().getId().equals(signature))
                    .findFirst()
                    .ifPresent(booking -> {
                        lastBookings.put(signature, booking);
                    });
            allNextBookings.stream()
                    .filter(booking -> {
                        return booking.getItem().getId().equals(signature);
                    })
                    .findFirst()
                    .ifPresent((Booking booking) -> {
                        nextBookings.put(signature, booking);
                    });
            List<Comment> allComments = commentRepository.findAllByItemsId(itemsId);
            List<Comment> valueOfComments = allComments.stream()
                    .filter(comment -> Objects.equals(comment.getAuthor().getId(), signature) && signature != null)
                    .collect(toList());
            comments.put(signature, valueOfComments);
        });
        List<ItemInfoDto> collect = items.stream()
                .map(item -> mapper.toItemInfoDto(
                        item,
                        bookingMapper.toBookingInfoDto(lastBookings.get(item.getId())),
                        bookingMapper.toBookingInfoDto(nextBookings.get(item.getId())),
                        comments.get(item.getId())
                                .stream()
                                .map(mapper::toCommentDto)
                                .collect(toList())))
                .collect(toList());
        return collect;
    }

    @Override
    public ItemInfoDto getItemById(Long id, Long userId) {
        log.info("ItemServiceImpl: Получен GET-запрос на получение вещи с УИН {}", id);
        Item item = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("ItemServiceImpl: Вещь с УИН " + id + " не существует."));
        LocalDateTime now = LocalDateTime.now();
        List<CommentDto> comments = commentRepository.findAllByItem_Id(item.getId())
                .stream()
                .map(mapper::toCommentDto)
                .collect(toList());
        BookingInfoDto lastBooking = bookingMapper.toBookingInfoDto(bookingRepository
                .findFirstByItem_IdAndItem_Owner_IdAndStartIsBefore(
                        id,
                        userId,
                        now,
                        SORT_DESC));
        BookingInfoDto nextBooking = bookingMapper.toBookingInfoDto(bookingRepository
                .findFirstByItem_IdAndItem_Owner_IdAndStartIsAfterAndStatusIsNotAndStatusIsNot(
                        id,
                        userId,
                        now,
                        Status.CANCELED,
                        Status.REJECTED,
                        SORT_ASC));
        return mapper.toItemInfoDto(item, lastBooking, nextBooking, comments);
    }

    @Override
    @Transactional
    public ItemDto update(ItemDto itemDto, Long userId, Long itemId) {
        log.info("ItemServiceImpl: Получен PUT-запрос на обновление вещи с УИН {}", itemId);
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("ItemServiceImpl: УИН пользователя неверный."));
        Item oldItem = repository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("ItemServiceImpl: У пользователя нет такой вещи."));
        if (oldItem.getOwner().getId().equals(owner.getId())) {
            itemDto.setId(itemId);
            Item item = mapper.updatedItem(itemDto, oldItem);
            item.setOwner(owner);
            repository.save(item);
            log.info("Вещь с УИН {} изменена пользователем с УИН {}", itemId, userId);
            return mapper.toItemDto(item);
        } else {
            throw new NotFoundException("ItemServiceImpl: У пользователя нет такой вещи.");
        }
    }

    @Override
    public void delete(Long itemId, Long id) {
        log.info("ItemServiceImpl: Получен DELETE-запрос на удаление вещи с УИН {}", itemId);
        Item item = repository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с УИН " + itemId + " не существует."));
        repository.deleteById(itemId);
    }

    @Override
    public void deleteItemsByOwner(Long id) {
        log.info("ItemServiceImpl: Получен DELETE-запрос на удаление всех вещеепользователя с УИН {}", id);
        repository.deleteById(id);
    }

    @Override
    public List<ItemDto> getAvailableItems(Long userId, String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        } else {
            return repository.searchAvailableItems("%" + text + "%")
                    .stream()
                    .map(mapper::toItemDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public List<CommentDto> getCommentsByItemId(Long itemId) {
        log.info("ItemServiceImpl: Получен GET-запрос на получение отзывов вещи с УИН {}", itemId);
        return commentRepository.findAllByItem_Id(itemId,
                        Sort.by(Sort.Direction.DESC, "created")).stream()
                .map(mapper::toCommentDto)
                .collect(toList());
    }

    @Override
    public Item findItemById(Long id) {
        log.info("ItemServiceImpl: Получен GET-запрос на получение вещи с УИН {}", id);
        return repository.findById(id).orElseThrow(() ->
                new NotFoundException("ItemServiceImpl: Не существует вещь с УИН " + id));
    }

    @Override
    public void deleteItemsByUser(Long userId) {
        log.info("ItemServiceImpl: Удаление запасов пользователя с УИН {}", userId);
        deleteItemsByOwner(userId);
    }

    @Override
    public boolean isCheckItemOwner(Long itemId, Long userId) {
        log.info("ItemServiceImpl: Проверка является ли пользователь {} владельцем вещи с УИН {}", userId, itemId);
        boolean boo = false;
        if (userId.equals(findItemById(itemId).getOwner().getId())) {
           boo = true;
        }
        return boo;
    }

}
