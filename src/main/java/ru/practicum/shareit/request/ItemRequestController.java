package ru.practicum.shareit.request;

import static ru.practicum.shareit.service.MyConstants.USER_ID;

import javax.validation.Valid;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.service.EntityCheckImpl;

@Slf4j
@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;
    private final EntityCheckImpl emplyTesting;

    @PostMapping
    public ItemRequestInfoDto createItemRequest(@RequestHeader(USER_ID) Long userId,
                                                @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("ItemRequestController: Получен POST - запрос : пользователь УИН {} создает запрос на вещь {}", userId, itemRequestDto);
        return itemRequestService.createItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestInfoDto> getUsersItemRequests(@RequestHeader(USER_ID) Long userId) {
        log.info("ItemRequestController: Получен GET - запрос : получить список запросов пользователя с УИН {}", userId);
        return itemRequestService.getUsersItemRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestInfoDto> getItemRequests(
            @RequestHeader(USER_ID) Long userId,
            @RequestParam(value = "from", defaultValue = "0") int from,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        emplyTesting.isCheckFromSize(from, size);
        log.info("ItemRequestController: Получен GET - запрос : Получить список запросов from {} и size {} пользователя с УИД {} ", from, size, userId);
        return itemRequestService.getItemRequests(userId, PageRequest.of(from / size, size));
    }

    @GetMapping("/{requestId}")
    public ItemRequestInfoDto getItemRequest(@PathVariable("requestId") Long requestId,
                                             @RequestHeader(USER_ID) Long userId) {
        log.info("ItemRequestController: Получен GET - запрос : Получить запрос {} по УИН {} пользователя.", requestId, userId);
        return itemRequestService.getItemRequestById(requestId, userId);
    }
}
