package ru.practicum.shareit.request;

import static ru.practicum.shareit.Constants.USER_ID;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import ru.practicum.shareit.request.dto.ItemRequestDto;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> addRequest(@RequestHeader(USER_ID) Long userId,
                                             @RequestBody @Valid ItemRequestDto itemRequestDto) {
        log.info("Создание нового запроса с УИН {} ", itemRequestDto.getId());
        return itemRequestClient.addRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllRequestsByUserId(@RequestHeader(USER_ID) Long userId) {
        log.info("Получение всех запросов рользователя {} ", userId);
        return itemRequestClient.getAllRequestsByUserId(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader(USER_ID) Long userId,
                                                 @RequestParam(name = "from", defaultValue = "0")
                                                 @PositiveOrZero Integer from,
                                                 @RequestParam(name = "size", defaultValue = "10")
                                                 @Positive Integer size) {
        log.info("Получение всех запросов.");
        return itemRequestClient.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader(USER_ID) Long userId,
                                                 @PathVariable Long requestId) {
        log.info("Получение запроса по УИН {} ", requestId);
        return itemRequestClient.getRequestById(userId, requestId);
    }

}

