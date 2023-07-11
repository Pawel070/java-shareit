package ru.practicum.shareit.expections;

public class ErrorResponse {
    private final String errResponse;

    public ErrorResponse(String errResponse) {
        this.errResponse = errResponse;
    }

    public String getError() {
        return errResponse;
    }
}