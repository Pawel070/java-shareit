package ru.practicum.shareit.expections;

public class StatusErrorException extends RuntimeException {
    public StatusErrorException(final String message) {
        super(message);
    }
}