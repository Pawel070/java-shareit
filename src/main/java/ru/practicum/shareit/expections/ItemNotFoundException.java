package ru.practicum.shareit.expections;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ItemNotFoundException extends IllegalArgumentException {

    private static final long serialVersionUID = 1L;

    public ItemNotFoundException(String message) {
        super(message);
        log.error("Error {} в {} ", message, message.getClass());
    }
}
