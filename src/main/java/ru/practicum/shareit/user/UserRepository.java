package ru.practicum.shareit.user;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.practicum.shareit.user.model.User;


public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByEmail(String email);
}
