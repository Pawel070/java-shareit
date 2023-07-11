package ru.practicum.shareit.expections;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserAlreadyExistsException extends IllegalArgumentException {

    private static final long serialVersionUID = 1L;

    public UserAlreadyExistsException(String message) {
        super(message);
        log.error("Error {} в {} ", message, message.getClass());
    }
}

