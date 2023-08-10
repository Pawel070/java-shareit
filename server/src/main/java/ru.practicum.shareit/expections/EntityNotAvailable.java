package ru.practicum.shareit.expections;

public class EntityNotAvailable extends RuntimeException {
    public EntityNotAvailable(final String m) {
        super(m);
    }
}