package ru.practicum.shareit.expections;

public class ConflictException extends RuntimeException {
    public ConflictException(final String m) {
        super(m);
    }
}