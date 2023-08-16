package ru.practicum.shareit.expections;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND) //404
    public ErrorResponse handleNotFoundException(final NotFoundException exception) {
        log.debug("handleNotFoundException Получен статус 404 Not found {}", exception.getMessage(), exception);
        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 400
    public ErrorResponse handleValidationException(final ValidationException exception) {
        log.debug("handleValidationException Получен статус 400 Bad request {}", exception.getMessage(), exception);
        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT) // 409
    public ErrorResponse handleUserAlreadyExistException(final UserAlreadyExistsException exception) {
        log.debug("handleUserAlreadyExistException Получен статус 409 Conflict {}", exception.getMessage(), exception);
        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT) // 409
    public ErrorResponse conflictException(final ConflictException exception) {
        return new ErrorResponse(exception.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 400
    public ErrorResponse handleConstraintViolationException(final Exception exception) {
        log.debug("handleConstraintViolationException Получен статус 400 {}", exception.getMessage(), exception);
        return new ErrorResponse("http:400 Искомый объект не найден при первичной проверке.", exception.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 400
    public ErrorResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException exception) {
        log.debug("handleMethodArgumentNotValidException Искомый объект не найден. Возврат код 400 {}", exception.getMessage());
        return new ErrorResponse("http:400 Искомый объект не найден при первичной проверке.", exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 400
    public ErrorResponse entityNotAvailable(final EntityNotAvailable exception) {
        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleException(BadRequestException exception) {
        log.error("Unknown state: UNSUPPORTED_STATUS", exception);
        return new ErrorResponse(exception.getMessage());
    }


}
