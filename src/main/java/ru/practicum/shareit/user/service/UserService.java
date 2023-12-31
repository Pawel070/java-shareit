package ru.practicum.shareit.user.service;

import java.util.List;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Component
public interface UserService {

    List<UserDto> getUsers();

    UserDto getUser(Long id);

    UserDto create(UserDto userDto);

    UserDto update(UserDto userDto, Long id);

    void delete(Long userId);

    User findUserById(Long id);

    void isCheckUserId(Long id);
}