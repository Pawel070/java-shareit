package ru.practicum.shareit.expections;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserNotFoundException extends IllegalArgumentException {

    private static final long serialVersionUID = 1L;

    public UserNotFoundException(String message) {
        super(message);
        log.error("Error {} в {} ", message, message.getClass());
    }
}
