package ru.practicum.shareit.expections;

public class UnsupportedState extends RuntimeException {
    public UnsupportedState(final String message) {
        super(message);
    }
}