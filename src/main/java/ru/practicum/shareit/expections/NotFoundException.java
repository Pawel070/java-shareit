package ru.practicum.shareit.expections;

public class NotFoundException extends IllegalArgumentException {

    private static final long serialVersionUID = 1L;

    public NotFoundException(String message) {
        super(message);
   }
}
