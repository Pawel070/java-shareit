package ru.practicum.shareit.user.service;

import static org.junit.jupiter.api.Assertions.*;

import javax.transaction.Transactional;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.expections.ConflictException;
import ru.practicum.shareit.expections.NotFoundException;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Transactional
@SpringBootTest
class UserServiceImplIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }


    @Test
    void create() {
        UserDto userDto = new UserDto();
        userDto.setName("Alex");
        userDto.setEmail("alex@mail.ru");

        UserDto userDtoRespond = userService.create(userDto);
        User userRespond = userRepository.findById(userDtoRespond.getId()).orElse(null);

        assertEquals(userDto.getName(), userDtoRespond.getName());
        assertEquals(userDto.getEmail(), userDtoRespond.getEmail());

        assertNotNull(userRespond);
        assertEquals(userDto.getName(), userRespond.getName());
        assertEquals(userDto.getEmail(), userRespond.getEmail());
    }

    @Test
    public void create_duplicateEmail() {
        String email = "alex@mail.ru";

        UserDto firstUserDto = new UserDto();
        firstUserDto.setName("Alex");
        firstUserDto.setEmail(email);
        userService.create(firstUserDto);

        UserDto secondUserDto = new UserDto();
        secondUserDto.setName("Max");
        secondUserDto.setEmail(email);

        assertThrows(DataIntegrityViolationException.class, () -> userService.create(secondUserDto));
    }

    @Test
    void update() {
        User user = new User();
        user.setName("Alex");
        user.setEmail("alex@mail.ru");
        User oldUser = userRepository.save(user);

        UserDto newUserDto = new UserDto();
        newUserDto.setId(oldUser.getId());
        newUserDto.setName("Max");
        newUserDto.setEmail("max@mail.ru");

        UserDto updatedUser = userService.update(newUserDto, oldUser.getId());

        assertNotNull(updatedUser);
        assertEquals(updatedUser.getId(), newUserDto.getId());
        assertEquals(updatedUser.getName(), newUserDto.getName());
        assertEquals(updatedUser.getEmail(), newUserDto.getEmail());
    }

    @Test
    public void update_userNotInBase() {
        UserDto newUserDto = new UserDto();
        newUserDto.setName("Max");
        newUserDto.setEmail("max@mail.ru");

        assertThrows(NotFoundException.class, () -> userService.update(newUserDto, 99L));
    }

    @Test
    public void update_duplicateEmail() {
        User user = new User();
        user.setName("Alex");
        user.setEmail("alex@mail.ru");
        User savedUser = userRepository.save(user);

        User user2 = new User();
        user2.setName("Max");
        user2.setEmail("max@mail.ru");
        userRepository.save(user2);

        UserDto userDto = new UserDto();
        userDto.setName("Vlad");
        userDto.setEmail(user2.getEmail());

        assertThrows(ConflictException.class, () -> userService.update(userDto, savedUser.getId()));
    }

    @Test
    void getUser() {
        User user = new User();
        user.setName("Alex");
        user.setEmail("alex@mail.ru");
        User savedUser = userRepository.save(user);

        UserDto userFromDb = userService.getUser(savedUser.getId());
        assertEquals(userFromDb.getEmail(), user.getEmail());
    }

    @Test
    void getUser_beforeSaveUser() {
        assertThrows(NotFoundException.class, () -> userService.getUser(99L));
    }

    @Test
    void getAllUsers() {
        User user1 = new User();
        user1.setName("Alex");
        user1.setEmail("alex@mail.ru");
        userRepository.save(user1);

        User user2 = new User();
        user2.setName("Max");
        user2.setEmail("max@mail.ru");
        userRepository.save(user2);

        List<UserDto> usersDtoRespond = userService.getUsers();

        assertEquals(usersDtoRespond.size(), 2);
        assertEquals(usersDtoRespond.get(0).getName(), user1.getName());
        assertEquals(usersDtoRespond.get(0).getEmail(), user1.getEmail());
        assertEquals(usersDtoRespond.get(1).getName(), user2.getName());
        assertEquals(usersDtoRespond.get(1).getEmail(), user2.getEmail());
    }

    @Test
    void getAllUsers_beforeAddUsers() {
        List<UserDto> usersDtoRespond = userService.getUsers();

        assertEquals(usersDtoRespond.size(), 0);
    }

    @Test
    void deleteUser() {
        User user = new User();
        user.setName("Alex");
        user.setEmail("max@mail.ru");
        User savedUser = userRepository.save(user);

        userService.delete(savedUser.getId());
        Boolean rez = userRepository.existsById(savedUser.getId());

        assertEquals(rez, false);
    }
}