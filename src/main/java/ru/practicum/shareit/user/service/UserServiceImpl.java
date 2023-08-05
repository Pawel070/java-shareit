package ru.practicum.shareit.user.service;

import static java.util.stream.Collectors.toList;

import javax.transaction.Transactional;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.stereotype.Service;

import ru.practicum.shareit.expections.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final UserMapper mapper;

    @Override
    public List<UserDto> getUsers() {
        log.info("UserServiceImpl: Получен GET-запрос '/users'");
        return repository.findAll().stream()
                .map(mapper::toUserDto)
                .collect(toList());
    }

    @Override
    public UserDto getUser(Long id) {
        log.info("UserServiceImpl: Получен GET-запрос '/users/{}", id);
        return mapper.toUserDto(repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с УИН " + id + " не существует.")));
    }

    @Transactional
    @Override
    public UserDto create(UserDto userDto) {
        log.info("UserServiceImpl: Получен POST-запрос '/users' {} ", userDto);
        User user = mapper.toUser(userDto);
        try {
            UserDto userDto1 = mapper.toUserDto(repository.save(user));
            log.info("UserServiceImpl: Пользователь {}  с УИД : {} создан", mapper.toUser(userDto1), userDto1.getId());
            return userDto1;
        } catch (ConstraintViolationException e) {
            throw new ru.practicum.shareit.exceptions.ConflictException("Mail " + userDto.getEmail() + " уже используется другим пользователем.");
        }
    }

    @Transactional
    @Override
    public UserDto update(UserDto userDto, Long id) {
        log.info("UserServiceImpl: Получен PUT-запрос на обновление пользователя с УИН {}", id);
 //       UserDto userDto1 = getUser(id);
 //       if (userDto.getId() == null) {
//            userDto.setId(id);
//        }
        User user = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("UserServiceImpl: Пользователь с УИН " + id + " не существует."));
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null && !userDto.getEmail().equals(user.getEmail())) {
            if (repository.findByEmail(userDto.getEmail())
                    .stream()
                    .filter(us -> us.getEmail().equals(userDto.getEmail()))
                    .allMatch(us -> us.getId().equals(userDto.getId()))) {
                user.setEmail(userDto.getEmail());
            } else {
                throw new ru.practicum.shareit.exceptions.ConflictException("UserServiceImpl: Пользователь с E-mail=" + user.getEmail() + " уже существует.");
            }
        }
        UserDto dto = mapper.toUserDto(repository.save(user));
        log.info("UserServiceImpl: Получен PUT-запрос на обновление пользователя с УИН {}, старый {}, новый {}", id, user, dto);
        return dto;
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

}
