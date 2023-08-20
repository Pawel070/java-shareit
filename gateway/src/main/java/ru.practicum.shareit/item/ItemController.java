package ru.practicum.shareit.item;

import static ru.practicum.shareit.Constants.USER_ID;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader(USER_ID) Long userId,
                                          @RequestBody @Valid ItemDto itemDto) {
        log.info("Создание item {} ", itemDto.getName());
        return itemClient.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(USER_ID) Long userId,
                                             @PathVariable long itemId,
                                             @RequestBody ItemDto itemDto) {
        log.info("Изменение item {} ", itemDto.getName());
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable Long itemId,
                                              @RequestHeader(USER_ID) Long userId) {
        log.info("Получение item {} ", itemId);
        return itemClient.getItemById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsByUser(@RequestHeader(USER_ID) Long userId,
                                                    @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                                                    @Positive @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("Получение предметов из userId {} ", userId);
        return itemClient.getAllItemsByUser(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestHeader(USER_ID) Long userId,
                                             @RequestParam(value = "text", defaultValue = "-") @NotBlank String text,
                                             @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
                                             @RequestParam(name = "size", defaultValue = "10") @Positive int size) {
        log.info("Поиск элементов, содержащих {} ", text);
        return itemClient.searchItem(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@PathVariable Long itemId,
                                             @RequestHeader(USER_ID) Long userId,
                                             @RequestBody @Valid CommentDto commentDto) {
        log.info("User {} добавил комментарий к item {} ", userId, itemId);
        return itemClient.addComment(itemId, userId, commentDto);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteItem(@RequestHeader(USER_ID) Long userId,
                                             @PathVariable Long itemId) {
        log.info("Удаление item {} ", itemId);
        return itemClient.deleteItem(userId, itemId);
    }

}
