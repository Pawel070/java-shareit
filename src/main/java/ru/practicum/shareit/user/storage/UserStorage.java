package ru.practicum.shareit.user.storage;


import java.util.List;

import ru.practicum.shareit.user.model.User;

public interface UserStorage {

    User createUser(User user);

    User update(User user, Long id);

    User getUser(Long id);

    List<User> getAllUsers();

    void deleteUser(Long id);

}