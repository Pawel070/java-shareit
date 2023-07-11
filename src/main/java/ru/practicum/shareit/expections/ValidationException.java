package ru.practicum.shareit.expections;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ValidationException extends IllegalArgumentException {

    private static final long serialVersionUID = 1L;

    public ValidationException(String message) {
        super(message);
        log.error("Error {} в {} ", message, message.getClass());
    }
}

