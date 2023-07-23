package ru.practicum.shareit.user.model;

import javax.persistence.*;
import javax.validation.constraints.Email;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id; // УИН пользователя

    @Column(name = "name", nullable = false)
    private String name; // имя или логин пользователя

    @Email
    @Column(name = "email", nullable = false, unique = true)
    private String email; // адрес электронной почты

}