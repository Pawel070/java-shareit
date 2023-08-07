package ru.practicum.shareit.expections;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class MethodArgumentNotValidExceptionTest {

    private final ErrorHandler errorHandler;

    @Test
    void methodArgumentNotValidExceptionTests() {
        ErrorResponse error = errorHandler.handleMethodArgumentNotValidException(new MethodArgumentNotValidException("message"));
        Assertions.assertEquals(error.getError(), "500 INTERNAL_SERVER_ERROR");
    }

}