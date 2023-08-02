package ru.practicum.shareit.user.service;

import static java.util.stream.Collectors.toList;

import javax.transaction.Transactional;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.expections.MethodArgumentNotValidException;
import ru.practicum.shareit.expections.NotFoundException;
import ru.practicum.shareit.expections.UserAlreadyExistsException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserRepository repository;
    private UserMapper mapper;

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
            throw new MethodArgumentNotValidException("Mail " + userDto.getEmail() + " уже используется другим пользователем.");
        }
    }


    /*
        @Override
        public UserDto create(UserDto userDto) {
            log.info("UserServiceImpl: Получен POST-запрос '/users' {} ", userDto);
            User user = mapper.toUser(userDto);
            if (userDto.getId() != null) {
                throw new IllegalArgumentException("УИН должен быть равен null.");
            }
            if (userDto.getName() == null || userDto.getName().isBlank()) {
                log.info("UserServiceImpl: Имя пользователя не может быть пустым.");
                throw new ConstraintViolationException("Имя пользователя не может быть пустым.");
            }
            if (userDto.getEmail() == null || userDto.getEmail().isBlank()) {
                log.info("UserServiceImpl: Email пользователя не может быть пустым.");
                throw new ConstraintViolationException("Email пользователя не может быть пустым.");
            }
            if (!userDto.getEmail().contains("@")) {
                log.info("UserServiceImpl: Email пользователя некорректен.");
                throw new ConstraintViolationException("Email пользователя некорректен.");
            }
            log.info("UserServiceImpl: Предварительно создан пользователь с УИН : {}", user.getId());
            UserDto userDto1 = mapper.toUserDto(repository.save(mapper.toUser(userDto)));
            log.info("UserServiceImpl: Пользователь {}  с УИД : {} создан", userDto1, userDto1.getId());
            return userDto1;
        }
    */
    @Override
    public UserDto update(UserDto userDto, Long id) {
        log.info("UserServiceImpl: Получен PUT-запрос на обновление пользователя с УИН {}", id);
        UserDto userDto1 = getUser(id);
        if (userDto.getId() == null) {
            userDto.setId(id);
        }
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
                throw new UserAlreadyExistsException("UserServiceImpl: Пользователь с E-mail=" + user.getEmail() + " уже существует.");
            }
        }
        UserDto dto = mapper.toUserDto(repository.save(user));
        log.info("UserServiceImpl: Получен PUT-запрос на обновление пользователя с УИН {}, старый {}, новый {}", id, userDto1, dto);
        return dto;
    }

    @Override
    public void delete(Long userId) {
        log.info("UserServiceImpl: Получен DELETE-запрос на удаление пользователя с УИН {}", userId);
        repository.deleteById(userId);
    }

    @Override
    public User findUserById(Long id) {
        log.info("UserServiceImpl: Поиск пользователя с УИН {} вызов из > {} ", id, this.getClass().getSimpleName());
        return repository.findById(id).orElseThrow(() -> new NotFoundException("UserServiceImpl findUserById: Пользователь с УИН " + id + " не существует."));
    }

}
