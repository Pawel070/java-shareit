package ru.practicum.shareit.user.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

@Slf4j
@Transactional
@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository repository;

    @BeforeEach
    void prepare() {
        User user1 = new User(1L, "user1", "mail1@mail.ru");
        User user2 = new User(2L, "user2", "mail2@mail.ru");
        repository.save(user1);
        repository.save(user2);
    }

    @Test
    void findAllWithEmptyRepository_shouldReturnEmpty() {
        List<User> users = repository.findAll();
        Assertions.assertEquals(3, users.size());
    }

    @Test
    void deleteById() {
        List<User> users = repository.findAll();
        long userId = users.get(1).getId();
        assertNotNull(users.get(1));
        log.info("userId: {}", userId);
        repository.deleteById(userId);
        User user = repository.findUserById(userId);
        log.info("user: {}", user);
        assertEquals(user, null);
    }

    @Test
    void findByEmail() {
        List<User> users = repository.findByEmail("mail2@mail.ru");
        Assertions.assertEquals(1, users.size());
    }

    @Test
    void findUserById() {
        long userId = repository.findAll().get(1).getId();
        User user = repository.findUserById(userId);
        Assertions.assertEquals(user.getName(), "user2");
    }

}