package ru.practicum.shareit.user;

import javax.validation.Valid;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.userClient;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserClient userClient;

    @GetMapping
    public List<UserDto> getUsers() {
        log.info("UserClient: Получен GET-запрос на получение списка всех пользователеез");
        return userClient.getAllUsers();
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable Long userId) {
        log.info("UserClient: Получен GET-запрос на получение пользователя с УИН {}", userId);
        return userClient.getUserById(userId);
    }

    @PostMapping
    public UserDto create(@RequestBody @Valid UserDto userDto) {
        log.info("UserClient: Получен POST-запрос на добавление пользователя {} ", userDto);
        return userClient.addUser(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@RequestBody UserDto userDto, @PathVariable Long userId) {
        log.info("UserClient: Получен PATCH-запрос на обновление пользователя с УИН {}", userId);
        return userClient.updateUser(userDto, userId);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        log.info("UserClient: Получен DELETE-запрос на удаление пользователя с УИН {}", userId);
        userClient.deleteUser(userId);
    }

}