package ru.practicum.shareit.expections;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
    void validationExceptionTest() {
        ErrorResponse error = errorHandler.handleValidationException(new ValidationException("message"));
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

}