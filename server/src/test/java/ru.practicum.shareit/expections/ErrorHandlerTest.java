package ru.practicum.shareit.expections;

import lombok.RequiredArgsConstructor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ErrorHandlerTest {

    private final ErrorHandler errorHandler;

    @Test
    void notFoundExceptionTest() {
        ErrorResponse error = errorHandler.handleNotFoundException(new NotFoundException("message"));
        Assertions.assertEquals(error.getError(), "message");
    }

    @Test
    void userAlreadyExistsExceptionTest() {
        ErrorResponse error = errorHandler.handleUserAlreadyExistException(new UserAlreadyExistsException("message"));
        Assertions.assertEquals(error.getError(), "message");
    }

    @Test
    void serverErrorTest() {
        ErrorResponse error = errorHandler.handleServerError(new Exception("message"));
        Assertions.assertEquals(error.getError(), "http:500 Ошибка на сервере.");
    }

    @Test
    void unsupportedStateTest() {
        ErrorResponse error = errorHandler.unsupportedState(new UnsupportedState("message"));
        Assertions.assertEquals(error.getError(), "message");
    }

    @Test
    void constraintViolationExceptionTest() {
        ErrorResponse error = errorHandler.handleConstraintViolationException(new Exception("message"));
        Assertions.assertEquals(error.getError(), "http:400 Искомый объект не найден при первичной проверке.");
    }

    @Test
    void conflictException() {
        ErrorResponse error = errorHandler.conflictException(new ConflictException("message"));
        Assertions.assertEquals(error.getError(), "message");
    }

    @Test
    void entityNotAvailable() {
        ErrorResponse error = errorHandler.entityNotAvailable(new EntityNotAvailable("message"));
        Assertions.assertEquals(error.getError(), "message");
    }

    @Test
    void handleException() {
        ErrorResponse error = errorHandler.handleException(new BadRequestException("message"));
        Assertions.assertEquals(error.getError(), "message");
    }

    @Test
    void handleStatusErrorException() {
        ErrorResponse error = errorHandler.handleStatusErrorException(new StatusErrorException("message"));
        Assertions.assertEquals(error.getError(), "message");
    }

}