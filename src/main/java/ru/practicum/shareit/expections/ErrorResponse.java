package ru.practicum.shareit.expections;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ErrorResponse {
    private final String errResponse;

    public ErrorResponse(String errResponse) {
        log.error("ErrorResponse Error {} ", errResponse);
        this.errResponse = errResponse;
    }

    public ErrorResponse(String errResponse, String message) {
        log.error("ErrorResponse Error {} - {} ", errResponse, message);
        this.errResponse = errResponse;
    }

    public String getError() {
        log.error("ErrorResponse getError Error {} ", errResponse);
        return errResponse;
    }
}