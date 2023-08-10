package ru.practicum.shareit.expections;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NotFoundException extends IllegalArgumentException {

    private static final long serialVersionUID = 1L;

    public NotFoundException(String message) { // 400
        super(message);
        log.error("NotFoundException Error {} ", message);
    }
}
