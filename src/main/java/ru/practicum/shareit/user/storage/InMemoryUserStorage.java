package ru.practicum.shareit.user.storage;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.expections.UserAlreadyExistsException;
import ru.practicum.shareit.expections.NotFoundException;
import ru.practicum.shareit.user.model.User;

@Slf4j
@Data
@Component
public class InMemoryUserStorage implements UserStorage {

    private Map<Long, User> users = new HashMap<>();
    private Long id = 1L;

    @Override
    public User createUser(User user) {
        if (isUsersMailInBase(user)) {
            throw new UserAlreadyExistsException(user.getEmail() + "\" уже используется другим пользователем.");
        }
        user.setId(id);
        id++;
        users.put(user.getId(), user);
        log.info("Новый пользователь УИН : {}", user.getId());
        return user;
    }

    @Override
    public User update(User user, Long aLong) {
        if (users.containsKey(aLong)) {
            if (!isUsersMailInBase(user) || user.getEmail().equals(users.get(aLong).getEmail())) {
                user.setId(aLong);
                if (user.getName() == null) {
                    user.setName(users.get(aLong).getName());
                }
                if (user.getEmail() == null) {
                    user.setEmail(users.get(aLong).getEmail());
                }
                users.put(user.getId(), user);
                log.info("Пользователь с УИН : {} был изменен", user.getId());
                return user;
            } else {
                throw new UserAlreadyExistsException(user.getEmail() + "\" уже используется другим пользователем.");
            }
        } else {
            throw new NotFoundException("Пользователь с УИН : " + aLong + " не зарегистрирован.");
        }
    }

    @Override
    public User getUser(Long aLong) {
        if (users.containsKey(aLong)) {
            log.info("Пользователь с УИН : {} не зарегистрирован.", aLong);
            return users.get(aLong);
        } else {
            throw new NotFoundException("Пользователь с УИН : " + aLong + " не зарегистрирован.");
        }
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void deleteUser(Long aLong) {
        if (users.containsKey(aLong)) {
            users.remove(aLong);
            log.info("Пользователь с УИН : {} безжалостно удалён.", aLong);
        } else {
            throw new NotFoundException("Пользователь с УИН : " + aLong + " не зарегистрирован.");
        }
    }

    private boolean isUsersMailInBase(User user) {
        List<User> repeats = users.values().stream()
                .filter(u -> u.getEmail().equals(user.getEmail()))
                .collect(Collectors.toList());
        return !repeats.isEmpty();
    }

}