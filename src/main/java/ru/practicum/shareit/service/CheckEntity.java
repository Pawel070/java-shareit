package ru.practicum.shareit.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

@Slf4j
@Service
public class CheckEntity {
    private final UserService userService;
    private final ItemService itemService;

    @Autowired
    public CheckEntity(UserService userService, ItemService itemService) {
        this.userService = userService;
        this.itemService = itemService;
    }

    public boolean isExistUser(Long userId) {
        log.info("Проверка наличия пользователя с УИН {}", userId);
        return userService.getUser(userId) != null;
    }

    public void deleteItemsByUser(Long userId) {
        log.info("Удаление запасов пользователя с УИН {}", userId);
        itemService.deleteItemsByOwner(userId);
    }
}
