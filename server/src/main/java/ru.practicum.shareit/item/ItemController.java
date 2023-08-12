package ru.practicum.shareit.item;

import static ru.practicum.shareit.service.MyConstants.USER_ID;

import javax.validation.Valid;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.service.ItemService;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/{itemId}")
    public ItemInfoDto getItemById(@RequestHeader(USER_ID) Long ownerId, @PathVariable Long itemId) {
        log.info("ItemController: Получен GET-запрос на получение вещи с УИН {}", itemId);
        return itemService.getItemById(itemId, ownerId);
    }

    @PostMapping
    public ItemDto create(@Valid @RequestBody ItemDto itemDto, @RequestHeader(USER_ID) Long id) {
        log.info("ItemController: Получен POST-запрос на добавление вещи владельцем с УИН {}", id);
        return itemService.create(itemDto, id);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@Valid @RequestBody CommentDto commentDto, @RequestHeader(USER_ID) Long userId,
                                    @PathVariable Long itemId) {
        log.info("ItemController: Получен POST-запрос на добавление отзыва пользователем с УИН {}", userId);
        return itemService.createComment(commentDto, itemId, userId);
    }

    @GetMapping
    public List<ItemInfoDto> getItemsByOwner(
            @RequestHeader(USER_ID) Long id,
            @RequestParam(value = "from", defaultValue = "0") int from,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        itemService.isCheckFromSize(from, size);
        log.info("ItemController: Получен GET-запрос на получение всех вещей владельца с УИН {} с from {} и size {}", id, from, size);
        return itemService.getItemsByOwner(id, PageRequest.of(from / size, size));
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable("itemId") Long itemId,
                              @RequestBody ItemDto itemDto,
                              @RequestHeader(USER_ID) Long id) {
        log.info("ItemController: Получен PATCH-запрос {} на обновление вещи с УИН пользователя, --> {} --> {} ", itemId, id, itemDto);
        return itemService.update(itemDto, id, itemId);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@PathVariable Long itemId, @RequestHeader(USER_ID) Long id) {
        log.info("ItemController: Получен DELETE-запрос на удаление вещи с УИН {}", itemId);
        itemService.delete(itemId, id);
    }

    @GetMapping("/search")
    public List<ItemDto> getUsersAvailableItems(
            @RequestHeader(USER_ID) Long userId,
            @RequestParam String text,
            @RequestParam(value = "from", defaultValue = "0") int from,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        itemService.isCheckFromSize(from, size);
        log.info("ItemController: Получен GET-запрос на поиск вещи : {} с from {} и size {} от {} ", text, from, size, userId);
        return itemService.getAvailableItems(userId, text, PageRequest.of(from / size, size));
    }

}

