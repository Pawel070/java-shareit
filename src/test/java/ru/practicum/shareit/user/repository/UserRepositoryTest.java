package ru.practicum.shareit.user.repository;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

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
        repository.deleteById(userId);
        Assertions.assertThrows(DataIntegrityViolationException.class, () -> repository.findUserById(userId));
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