package ru.practicum.shareit.item;

import javax.validation.Valid;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.service.CheckEntity;

@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {

    private static final String OWNER = "X-Sharer-User-Id";
    private final ItemService itemService;
    private final CheckEntity checkEntity;

    @Autowired
    public ItemController(ItemService itemService, CheckEntity checkEntity) {
        this.itemService = itemService;
        this.checkEntity = checkEntity;
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Long itemId) {
        log.info("Получен GET-запрос на получение вещи с УИН {}", itemId);
        return itemService.getItemById(itemId);
    }

    @ResponseBody
    @PostMapping
    public ItemDto create(@Valid @RequestBody ItemDto itemDto, @RequestHeader(OWNER) Long ownerId) {
        log.info("Получен POST-запрос на добавление вещи владельцем с УИН {}", ownerId);
        ItemDto newItemDto = null;
        if (checkEntity.isExistUser(ownerId)) {
            newItemDto = itemService.create(itemDto, ownerId);
        }
        return newItemDto;
    }

    @GetMapping
    public List<ItemDto> getItemsByOwner(@RequestHeader(OWNER) Long ownerId) {
        log.info("Получен GET-запрос на получение всех вещей владельца с УИН {}", ownerId);
        return itemService.getItemsByOwner(ownerId);
    }

    @ResponseBody
    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestBody ItemDto itemDto, @PathVariable Long itemId,
                          @RequestHeader(OWNER) Long ownerId) {
        log.info("Получен PATCH-запрос на обновление вещи с УИН {}", itemId);
        ItemDto newItemDto = null;
        if (checkEntity.isExistUser(ownerId)) {
            newItemDto = itemService.update(itemDto, ownerId, itemId);
        }
        return newItemDto;
    }

    @DeleteMapping("/{itemId}")
    public ItemDto delete(@PathVariable Long itemId, @RequestHeader(OWNER) Long ownerId) {
        log.info("Получен DELETE-запрос на удаление вещи с УИН {}", itemId);
        return itemService.delete(itemId, ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemsBySearchQuery(@RequestParam String text) {
        log.info("Получен GET-запрос на поиск вещи : {}", text);
        return itemService.getItemsBySearchQuery(text);
    }
}
