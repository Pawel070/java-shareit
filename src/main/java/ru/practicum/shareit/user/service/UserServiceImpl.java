package ru.practicum.shareit.user.service;

import static java.util.stream.Collectors.toList;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import ru.practicum.shareit.expections.ConstraintViolationException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserStorage userStorage;
    private UserMapper mapper;

    @Override
    public List<UserDto> getUsers() {
        log.info("UserServiceImpl: Получен GET-запрос '/users'");
        return userStorage.getAllUsers().stream()
                .map(mapper::toUserDto)
                .collect(toList());
    }

    @Override
    public UserDto getUser(Long id) {
        log.info("UserServiceImpl: Получен GET-запрос '/users/{}", id);
        return mapper.toUserDto(userStorage.getUser(id));
    }

    @Override
    public UserDto create(UserDto userDto) {
        log.info("UserServiceImpl: Получен POST-запрос '/users'");
        User user = mapper.toUser(userDto);
        if (userDto.getId() != null) {
            throw new IllegalArgumentException("УИН должен быть равен null.");
        }
        if (userDto.getName() == null || userDto.getName().isBlank()) {
            log.info("UserServiceImpl: Имя пользователя не может быть пустым.");
            throw new ConstraintViolationException("Имя пользователя не может быть пустым.");
        }
        if (userDto.getEmail() == null || userDto.getEmail().isBlank()) {
            log.info("UserServiceImpl: Email пользователя не может быть пустым.");
            throw new ConstraintViolationException("Email пользователя не может быть пустым.");
        }
        if (!userDto.getEmail().contains("@")) {
            log.info("UserServiceImpl: Email пользователя некорректен.");
            throw new ConstraintViolationException("Email пользователя некорректен.");
        }
        log.info("UserServiceImpl: Предварительно создан пользователь с УИД : {}", user.getId());
        UserDto userDto1 = mapper.toUserDto(userStorage.createUser(mapper.toUser(userDto)));
        log.info("UserServiceImpl: Пользователь с УИД : {} создан", userDto1.getId());
        return userDto1;
    }

    @Override
    public UserDto update(UserDto userDto, Long id) {
        log.info("UserServiceImpl: Получен PUT-запрос на обновление пользователя с УИН {}", id);
        UserDto userDto1 = getUser(id);
        UserDto dto = mapper.toUserDto(userStorage.update(mapper.toUser(userDto), id));
        log.info("UserServiceImpl: Получен PUT-запрос на обновление пользователя с УИН {}, старый {}, новый {}", id, userDto1, dto);
        return dto;
    }

    @Override
    public void delete(Long userId) {
        log.info("UserServiceImpl: Получен DELETE-запрос на удаление пользователя с УИН {}", userId);
        userStorage.deleteUser(userId);
    }

}
