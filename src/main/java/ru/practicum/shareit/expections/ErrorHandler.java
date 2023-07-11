package ru.practicum.shareit.expections;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFoundException(final UserNotFoundException exception) {
        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleItemNotFoundException(final ItemNotFoundException exception) {
        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final ValidationException exception) {
        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleUserAlreadyExistException(final UserAlreadyExistsException exception) {
        return new ErrorResponse(exception.getMessage());
    }
}
