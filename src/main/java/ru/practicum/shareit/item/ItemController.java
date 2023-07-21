package ru.practicum.shareit.item;

import javax.validation.Valid;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {

    private static final String OWNER = "X-Sharer-User-Id";
    private final ItemService itemService;


    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Long itemId, @RequestHeader(OWNER) Long ownerId) {
        log.info("ItemController: Получен GET-запрос на получение вещи с УИН {}", itemId);
        return itemService.getItemById(itemId, ownerId);
    }

    @ResponseBody
    @PostMapping
    public ItemDto create(@Valid @RequestBody ItemDto itemDto, @RequestHeader(OWNER) Long id) {
        log.info("ItemController: Получен POST-запрос на добавление вещи владельцем с УИН {}", id);
        return itemService.create(itemDto, id);
    }

    @ResponseBody
    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@Valid @RequestBody CommentDto commentDto, @RequestHeader(OWNER) Long userId,
                                    @PathVariable Long itemId) {
        log.info("ItemController: Получен POST-запрос на добавление отзыва пользователем с УИН {}", userId);
        return itemService.createComment(commentDto, itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getItemsByOwner(@RequestHeader(OWNER) Long id) {
        log.info("ItemController: Получен GET-запрос на получение всех вещей владельца с УИН {}", id);
        return itemService.getItemsByOwner(id);
    }

    @ResponseBody
    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestBody ItemDto itemDto, @PathVariable Long itemId,
                          @RequestHeader(OWNER) Long id) {
        log.info("ItemController: Получен PATCH-запрос на обновление вещи с УИН {}", itemId);
        return itemService.update(itemDto, id, itemId);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@PathVariable Long itemId, @RequestHeader(OWNER) Long id) {
        log.info("ItemController: Получен DELETE-запрос на удаление вещи с УИН {}", itemId);
        itemService.delete(itemId, id);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemsBySearchQuery(@RequestParam String text) {
        log.info("ItemController: Получен GET-запрос на поиск вещи : {}", text);
        return itemService.getItemsBySearchQuery(text);
    }
}
