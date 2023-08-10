package ru.practicum.shareit.expections;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerError extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ServerError(String message) { // 500
        super(message);
        log.error("ServerError: Error {} ", message);
    }
}