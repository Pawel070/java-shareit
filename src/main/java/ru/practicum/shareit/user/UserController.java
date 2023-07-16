package ru.practicum.shareit.user;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import ru.practicum.shareit.service.CheckEntity;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

@Validated
@Slf4j
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;
    private final CheckEntity checker;

    @Autowired
    public UserController(UserService userService, CheckEntity checkEntity) {
        this.userService = userService;
        checker = checkEntity;
    }

    @GetMapping
    public List<UserDto> getUsers() {
        log.info("UserController: Получен GET-запрос на получение списка всех пользователеез");
        return userService.getUsers();
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable Long userId) {
        log.info("UserController: Получен GET-запрос на получение пользователя с УИН {}", userId);
        return userService.getUser(userId);
    }

    @ResponseBody
    @PostMapping
    public UserDto create(@RequestBody UserDto userDto) {
        log.info("UserController: Получен POST-запрос на добавление пользователя {} ", userDto);
        return userService.create(userDto);
    }

    @ResponseBody
    @PatchMapping("/{userId}")
    public UserDto update(@RequestBody UserDto userDto, @PathVariable Long userId) {
        log.info("UserController: Получен PATCH-запрос на обновление пользователя с УИН {}", userId);
        return userService.update(userDto, userId);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        log.info("UserController: Получен DELETE-запрос на удаление пользователя с УИН {}", userId);
        checker.deleteItemsByUser(userId);
        userService.delete(userId);
    }

}