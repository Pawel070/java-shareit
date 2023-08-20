package ru.practicum.shareit.expections;

import lombok.RequiredArgsConstructor;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ConstraintViolationExceptionTest {

    @Test
    void constraintViolationException() {
        ConstraintViolationException constraintViolationException =
                new ConstraintViolationException("http:500 Ошибка на сервере.");
    }

}
