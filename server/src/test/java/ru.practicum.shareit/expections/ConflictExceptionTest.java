package ru.practicum.shareit.expections;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ConflictExceptionTest {

    @Test
    void setConflictException() {
        ConflictException conflictException =
                new ConflictException("http:400 Искомый объект не найден при первичной проверке.");
    }

}