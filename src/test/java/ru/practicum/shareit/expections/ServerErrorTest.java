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
class ServerErrorTest {

    private final ErrorHandler errorHandler;

    @Test
    void serverErrorTests() {
        ErrorResponse error = errorHandler.handleServerError(new ServerError("message"));
        Assertions.assertEquals(error.getError(), "http:500 Ошибка на сервере.");
    }
}