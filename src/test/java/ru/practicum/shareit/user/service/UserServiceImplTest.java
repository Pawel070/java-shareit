package ru.practicum.shareit.user.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.Rollback;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;


@SpringBootTest
@RequiredArgsConstructor
class UserServiceImplTest {

    UserService userService;

    @Autowired
    UserMapper mapper;

    @MockBean
    UserRepository userRepository;

    User user;
    UserDto userDto;

    @BeforeEach
    void beforeEach() {
        userRepository.deleteAll();
        userService = new UserServiceImpl(userRepository, mapper);
        user = new User(1L, "user", "mail@mail.ru");
        userDto = new UserDto(1L, "user", "mail@mail.ru");
    }

    @Test
    @Rollback(false)
    void createUser() {
        when(userRepository.save(any())).thenReturn(user);

        UserDto res = userService.create(userDto);

        assertNotNull(res);
        assertEquals(UserDto.class, res.getClass());
        assertEquals(userDto.getId(), res.getId());
        assertEquals(userDto.getName(), res.getName());
        assertEquals(userDto.getEmail(), res.getEmail());
    }

    @Test
    @Rollback(false)
    void createUser_WithDuplicateEmail() {
        when(userRepository.save(any())).thenThrow(new ConstraintViolationException("", null, ""));

        assertThrows(ConflictException.class, () -> userService.create(userDto));
    }

    @Test
    @Rollback(false)
    void updateUser() {
        UserDto newUserDto = new UserDto(null, "user2", "mail2@mail.ru");
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userRepository.save(any())).thenReturn(user);

        UserDto res = userService.update(newUserDto, userDto.getId());

        assertNotNull(res);
        assertEquals(UserDto.class, res.getClass());
        assertEquals(userDto.getId(), res.getId());
        assertEquals(newUserDto.getName(), res.getName());
        assertEquals(newUserDto.getEmail(), res.getEmail());
    }

    @Test
    @Rollback(false)
    void updateUser_UpdateName() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userRepository.save(any())).thenReturn(user);
        UserDto newUserDto = new UserDto(null, "user2", null);

        UserDto res = userService.update(newUserDto, userDto.getId());

        assertNotNull(res);
        assertEquals(UserDto.class, res.getClass());
        assertEquals(userDto.getId(), res.getId());
        assertEquals(newUserDto.getName(), res.getName());
        assertEquals(userDto.getEmail(), res.getEmail());
    }

    @Test
    @Rollback(false)
    void updateUser_UpdateMail() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userRepository.save(any())).thenReturn(user);
        UserDto newUserDto = new UserDto(null, null, "mail2@mail.ru");

        UserDto res = userService.update(newUserDto, userDto.getId());

        assertNotNull(res);
        assertEquals(UserDto.class, res.getClass());
        assertEquals(userDto.getId(), res.getId());
        assertEquals(userDto.getName(), res.getName());
        assertEquals(newUserDto.getEmail(), res.getEmail());
    }

    @Test
    @Rollback(false)
    void updateUser_MailWasUsedByAnotherUser() {
        User user2 = new User(2L, "user2", "mail2@mail.ru");
        UserDto newUserDto = new UserDto(null, null, "mail2@mail.ru");
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.findAll()).thenReturn(List.of(user2));
        when(userRepository.save(any())).thenReturn(user);

        assertThrows(ConflictException.class, () -> userService.update(newUserDto, userDto.getId()));
    }

    @Test
    @Rollback(false)
    void getUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        UserDto res = userService.getUser(userDto.getId());
        assertNotNull(res);
        assertEquals(UserDto.class, res.getClass());
        assertEquals(userDto.getId(), res.getId());
        assertEquals(userDto.getName(), res.getName());
        assertEquals(userDto.getEmail(), res.getEmail());
    }

    @Test
    @Rollback(false)
    void testGetUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        UserDto res = userService.getUser(userDto.getId());
        assertNotNull(res);
        assertEquals(UserDto.class, res.getClass());
        assertEquals(userDto.getId(), res.getId());
        assertEquals(userDto.getName(), res.getName());
        assertEquals(userDto.getEmail(), res.getEmail());
    }

    @Test
    @Rollback(false)
    void findUserById() {
        when(userRepository.findById(11L)).thenReturn(Optional.of(mapper.toUser(userDto)));
        UserDto newUserDto = userService.getUser(11L);
        assertThat(newUserDto.getId(), equalTo(userDto.getId()));
        assertThat(newUserDto.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    @Rollback(false)
    void isCheckUserId() {
    }
}