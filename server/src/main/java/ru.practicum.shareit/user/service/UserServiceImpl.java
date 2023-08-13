package ru.practicum.shareit.user.service;

import static java.util.stream.Collectors.toList;

import javax.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.stereotype.Service;

import ru.practicum.shareit.expections.ConflictException;
import ru.practicum.shareit.expections.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    public List<UserDto> getUsers() {
        log.info("UserServiceImpl: Получен GET-запрос '/users'");
        return repository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(toList());
    }

    @Override
    public UserDto getUser(Long id) {
        log.info("UserServiceImpl: Получен GET-запрос '/users/{}", id);
        return UserMapper.toUserDto(repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с УИН " + id + " не существует.")));
    }

    @Transactional
    @Override
    public UserDto create(UserDto userDto) {
        log.info("UserServiceImpl: Получен POST-запрос '/users' {} ", userDto);
        User user = UserMapper.toUser(userDto);
        try {
            UserDto userDto1 = UserMapper.toUserDto(repository.save(user));
            log.info("UserServiceImpl: Пользователь {}  с УИД : {} создан", userDto1, user.getId());
            return userDto1;
        } catch (ConstraintViolationException e) {
            throw new ConflictException("Mail " + userDto.getEmail() + " уже используется другим пользователем.");
        }
    }

    @Transactional
    @Override
    public UserDto update(UserDto userDto, Long id) {
        log.info("UserServiceImpl: Получен PUT-запрос на обновление пользователя с УИН {}", id);
        User oldUser = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with ID " + id + " does not exist"));
        if (!isCheckUsersMail(userDto) || userDto.getEmail().equals(oldUser.getEmail())) {
            userDto.setId(id);
            User user = UserMapper.updatedUser(userDto, oldUser);
            repository.save(user);
            log.info("User ID {} was updated", userDto.getId());
            return UserMapper.toUserDto(user);
        } else {
            throw new ConflictException("Mail " + userDto.getEmail() + " already used by another user");
        }
    }

    @Transactional
    @Override
    public void delete(Long userId) {
        log.info("UserServiceImpl: Получен DELETE-запрос на удаление пользователя с УИН {}", userId);
        repository.deleteById(userId);
    }

    @Override
    public User findUserById(Long id) {
        log.info("UserServiceImpl: Поиск пользователя с УИН {} ", id);
        return repository.findById(id).orElseThrow(() -> new NotFoundException("UserServiceImpl findUserById: Пользователь с УИН " + id + " не существует."));
    }

    @Override
    public void isCheckUserId(Long id) {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        String messageClass = "";
        if (stackTraceElements.length >= 3) {
            StackTraceElement element = stackTraceElements[2];
            messageClass = element.getClassName() + ":" + element.getMethodName();
        }
        log.info("Проверка наличия пользователя с УИН  {} вызов из > {} ", id, messageClass);
        User user = findUserById(id);
    }

    private boolean isCheckUsersMail(UserDto userDto) {
        List<UserDto> repeats = getUsers().stream()
                .filter(u -> u.getEmail().equals(userDto.getEmail()))
                .collect(Collectors.toList());
        return repeats.size() != 0;
    }

}
