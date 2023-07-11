package ru.practicum.shareit.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

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
        return userService.getUser(userId) != null;
    }

    public void deleteItemsByUser(Long userId) {
        itemService.deleteItemsByOwner(userId);
    }
}
