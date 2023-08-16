package ru.practicum.shareit.expections;

public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}