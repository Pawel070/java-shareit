package ru.practicum.shareit.expections;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserAlreadyExistsException extends IllegalArgumentException {

    private static final long serialVersionUID = 1L;

    public UserAlreadyExistsException(String message) {    // 409
        super(message);
        log.error("UserAlreadyExistsException Error {} в {} ", message, message.getClass());
    }
}

