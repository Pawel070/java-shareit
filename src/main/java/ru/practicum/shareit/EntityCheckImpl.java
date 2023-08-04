package ru.practicum.shareit;

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
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        String messageClass = "";
        if (stackTraceElements.length >= 3) {
            StackTraceElement element = stackTraceElements[2];
            messageClass = element.getClassName() + ":" + element.getMethodName();
        }
        log.info("Проверка from {} и size {} вызов из > {} ", from, size, messageClass);
        if (from < 0 || size < 1) {
            throw new ru.practicum.shareit.exceptions.EntityNotAvailable("Ошибочный параметр \"size\" или \"from\"");
        }
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
        if (!repository.existsById(id)) {
            throw new NotFoundException("UserServiceImpl isCheckUserId: Пользователь с УИД " + id + " не существует.");
        }
    }

}

