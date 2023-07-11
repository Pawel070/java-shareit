package ru.practicum.shareit.user.service;

import java.util.List;

import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {

    List<UserDto> getUsers();

    UserDto getUser(Long id);

    UserDto create(UserDto userDto);

    UserDto update(UserDto userDto, Long id);

    void delete(Long userId);

}