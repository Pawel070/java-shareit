package ru.practicum.shareit.item;

import static ru.practicum.shareit.service.MyConstants.USER_ID;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

//    public ItemController(ItemService itemService) {
//        this.itemService = itemService;
//    }

    //@Autowired
    //public ItemController(ItemService itemService) {
    //    this.itemService = itemService;
    //}

    @GetMapping("/{itemId}") //3
    public ItemDto getItemById(@RequestHeader(USER_ID) Long ownerId, @PathVariable Long itemId) {
        log.info("ItemController: Получен GET-запрос на получение вещи с УИН {}", itemId);
        return itemService.getItemById(itemId, ownerId);
    }

    @PostMapping//1
    public ItemDto create(@Valid @RequestBody ItemDto itemDto, @RequestHeader(USER_ID) Long id) {
        log.info("ItemController: Получен POST-запрос на добавление вещи владельцем с УИН {}", id);
        return itemService.create(itemDto, id);
    }

    @PostMapping("/{itemId}/comment")//6
    public CommentDto createComment(@Valid @RequestBody CommentDto commentDto, @RequestHeader(USER_ID) Long userId,
                                    @PathVariable Long itemId) {
        log.info("ItemController: Получен POST-запрос на добавление отзыва пользователем с УИН {}", userId);
        return itemService.createComment(commentDto, itemId, userId);
    }

    @GetMapping//4
    public List<ItemDto> getItemsByOwner(@RequestHeader(USER_ID) Long id) {
        log.info("ItemController: Получен GET-запрос на получение всех вещей владельца с УИН {}", id);
        return itemService.getItemsByOwner(id);
    }

    @PatchMapping("/{itemId}")//2
    public ItemDto update(@RequestBody ItemDto itemDto, @PathVariable Long itemId,
                          @RequestHeader(USER_ID) Long id) {
        log.info("ItemController: Получен PATCH-запрос на обновление вещи с УИН {}", itemId);
        return itemService.update(itemDto, id, itemId);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@PathVariable Long itemId, @RequestHeader(USER_ID) Long id) {
        log.info("ItemController: Получен DELETE-запрос на удаление вещи с УИН {}", itemId);
        itemService.delete(itemId, id);
    }

    @GetMapping("/search")//5
    public List<ItemDto> getItemsBySearchQuery(@RequestParam String text) {
        log.info("ItemController: Получен GET-запрос на поиск вещи : {}", text);
        return itemService.getItemsBySearchQuery(text);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItemsByText(@RequestParam(value = "text", required = false) String text,
                                                 @RequestParam(name = "from", defaultValue = "0") int from,
                                                 @RequestParam(name = "size", defaultValue = "10") int size) {
        return itemService.searchItemsByText(text, from, size);
    }

}
