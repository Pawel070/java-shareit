package ru.practicum.shareit.expections;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ConflictExceptionTest {

    @Test
    void setConflictException() {
        ru.practicum.shareit.exceptions.ConflictException conflictException =
                new ru.practicum.shareit.exceptions.ConflictException("http:400 Искомый объект не найден при первичной проверке.");
    }

}