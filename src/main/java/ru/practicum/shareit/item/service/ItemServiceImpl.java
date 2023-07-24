package ru.practicum.shareit.item.service;

import static java.util.stream.Collectors.toList;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.expections.NotFoundException;
import ru.practicum.shareit.expections.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.service.CheckEntity;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {

    private ItemMapper mapper;
    private CheckEntity checker;
    private ItemRepository repository;
    private CommentRepository commentRepository;
    private UserService userService;
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository repository,
                           CheckEntity checkerService,
                           CommentRepository commentRepository,
                           UserService userService,
                           ItemRequestRepository itemRequestRepository,
                           ItemMapper itemMapper) {
        this.repository = repository;
        this.checker = checkerService;
        this.commentRepository = commentRepository;
        this.userService = userService;
        this.mapper = itemMapper;
        this.itemRequestRepository = itemRequestRepository;
    }

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

/*        Item newItem = mapper.mapToItemFromItemDto(itemDto);
        if (itemDto.getRequest() != null) {
            ItemRepository itemRepository = repository.findById(itemDto.getRequest())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            String.format("Запроса с id=%s нет", itemDto.getRequest())));
            newItem.setRequest(itemRepository);
        }
        newItem.setOwner(users.findById(userId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Пользователя с id=%s нет", userId))));
        return mapper.mapToItemDtoResponse(items.save(newItem));

    }




        userService.isExistUser(id);
        UserDto owner = userService.getUser(id);
        itemDto.setOwner(mapper.toUser(owner));

        return mapper.toItemDto(repository.save(toItem(itemDto, id)));
    }
*/
    @Override
    public CommentDto createComment(CommentDto commentDto, Long itemId, Long userId) {
        log.info("ItemServiceImpl: Получен POST-запрос на создание отзыва пользователем с УИН {}", userId);
        userService.isExistUser(userId);
        Comment comment = new Comment();
        Booking booking = checker.getUserBookingBookedItem(itemId, userId);
        if (booking != null) {
            comment.setCreated(LocalDateTime.now());
            comment.setItem(booking.getItem());
            comment.setAuthor(booking.getBooker());
            comment.setText(commentDto.getText());
        } else {
            throw new ValidationException("Данный пользователь вещь не бронировал!");
        }
        return mapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public List<ItemDto> getItemsByOwner(Long id) {
        log.info("ItemServiceImpl: Получен GET-запрос на получение списка вещей владельца с УИН {}", id);
        userService.isExistUser(id);
        return repository.findByOwnerId(id).stream()
                .map(mapper::toItemDto)
                .sorted(Comparator.comparing(ItemDto::getId))
                .collect(toList());
    }

    @Override
    public ItemDto getItemById(Long id, Long userId) {
        log.info("ItemServiceImpl: Получен GET-запрос на получение вещи с УИН {}", id);
        ItemDto itemDto;
        Item item = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("ItemServiceImpl: Вещь с УИН " + id + " не существует."));
        if (userId.equals(item.getId())) {
            itemDto = mapper.toItemExtDto(item);
        } else {
            itemDto = mapper.toItemDto(item);
        }
        return itemDto;
    }

    @Override
    @Transactional
    public ItemDto update(ItemDto itemDto, Long id, Long itemId) {
        log.info("ItemServiceImpl: Получен PUT-запрос на обновление вещи с УИН {}", itemId);
        userService.isExistUser(id);
        Item item = repository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("ItemServiceImpl: У пользователя нет такой вещи."));
        if (!item.getId().equals(id)) {
            throw new NotFoundException("ItemServiceImpl: У пользователя нет такой вещи.");
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return mapper.toItemDto(repository.save(item));
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

    /*
        @Override
        public List<ItemDto> getItemsBySearchQuery(String text) {
            log.info("ItemServiceImpl: Получен GET-запрос на получение вещей по тексту={}", text);
            String lowerCase = text.toLowerCase(Locale.ROOT);
            return repository.getItemsBySearchQuery(lowerCase).stream()
                    .map(mapper::toItemDto)
                    .collect(toList());
        }
    */

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
    public boolean isCheckAvailableItem(Long itemId) {
        log.info("ItemServiceImpl: Проверка добавления запасов вещи с УИН {}", itemId);
        return findItemById(itemId).getAvailable();
    }

    @Override
    public boolean isCheckItemOwner(Long itemId, Long userId) {
        log.info("ItemServiceImpl: Проверка является ли пользователь {} владельцем вещи с УИН {}", userId, itemId);
        return getItemsByOwner(userId).stream()
                .anyMatch(i -> i.getId() ==  (itemId));
    }

/*
    @Override
    public List<CommentDto> getCommentsByItemId(Long itemId) {
        log.info("ItemServiceImpl: Проверка наличия комментариев по вещи УИН {}", itemId);
        return getCommentsByItemId(itemId);
    }
*/
}
