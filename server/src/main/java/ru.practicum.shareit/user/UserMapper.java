package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public final class UserMapper {

    public UserDto toUserDto(User user) { // преобразование Юзера в ДТО
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static User toUser(UserDto userDto) { //преобразование ДТО в Юзера
        return new User(
                userDto.getId(),
                userDto.getName(),
                userDto.getEmail()
        );
    }
    public User updatedUser(UserDto userDto, User user) {
        return User.builder()
                .id(userDto.getId())
                .name(userDto.getName() == null ? user.getName() : userDto.getName())
                .email(userDto.getEmail() == null ? user.getEmail() : userDto.getEmail())
                .build();
    }

}
