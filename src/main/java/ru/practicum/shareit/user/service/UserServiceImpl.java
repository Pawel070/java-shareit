package ru.practicum.shareit.user.service;


import static java.util.stream.Collectors.toList;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.storage.UserStorage;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserStorage userStorage;
    private UserMapper mapper;

    @Override
    public List<UserDto> getUsers() {
        log.info("Получен GET-запрос '/users'");
        return userStorage.getAllUsers().stream()
                .map(mapper::toUserDto)
                .collect(toList());
    }

    @Override
    public UserDto getUser(Long id) {
        log.info("Получен GET-запрос '/users/{}", id);
        return mapper.toUserDto(userStorage.getUser(id));
    }

    @Override
    public UserDto create(UserDto userDto) {
        log.info("Получен POST-запрос '/users'");
        if (userDto.getId() != null) {
            throw new IllegalArgumentException("УИН должен быть равен null.");
        }
        if (userStorage.getUser(userDto.getId()) != null) {
            throw new IllegalArgumentException("Пользователь с таким УИН уже зарезистрирован.");
        }
        if (userDto.getName() == null || userDto.getName().isBlank()) {
            throw new IllegalArgumentException("Имя пользователя не может быть пустым.");
        }
        if (userDto.getEmail() == null || userDto.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email пользователя не может быть пустым.");
        }
        if (!userDto.getEmail().contains("@")) {
            throw new IllegalArgumentException("Email пользователя некорректен.");
        }
        return mapper.toUserDto(userStorage.createUser(mapper.toUser(userDto)));
    }

    @Override
    public UserDto update(UserDto userDto, Long id) {
        log.info("Получен PUT-запрос на обновление пользователя с УИН {}", id);
        if (userDto.getId() == null) {
            userDto.setId(id);
        }
        return userDto;
    }

    @Override
    public void delete(Long userId) {
        log.info("Получен DELETE-запрос на удаление пользователя с УИН {}", userId);
        userStorage.deleteUser(userId);
    }

}
