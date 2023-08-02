package ru.practicum.shareit.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.expections.NotFoundException;
import ru.practicum.shareit.user.UserRepository;

@Slf4j
@Service
@AllArgsConstructor
public class EntityCheckImpl implements EntityCheck {

    private UserRepository repository;

    @Override
    public void isCheckFromSize(int from, int size) {
        log.info("Проверка from {} и size {} вызов из > {} ", from, size, "1");
        if (from < 0 || size < 1) {
            throw new ru.practicum.shareit.exceptions.EntityNotAvailable("Ошибочный параметр \"size\" или \"from\"");
        }
    }

    @Override
    public void isCheckUserId(Long id) {
        log.info("Проверка наличия пользователя с УИН  {} вызов из > {} ", id, "1"); // this.getClass().getSimpleName()
        if (repository.findById(id) == null) {
            throw new NotFoundException("UserServiceImpl isCheckUserId: Пользователь с УИД " + id + " не существует.");
        }
    }

}

