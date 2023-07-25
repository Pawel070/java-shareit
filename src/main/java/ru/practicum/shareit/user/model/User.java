package ru.practicum.shareit.user.model;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import lombok.*;
import org.hibernate.Hibernate;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id; // УИН пользователя

    @Column(name = "name", nullable = false)
    private String name; // имя или логин пользователя

    @Email(message = "Электронная почта пользователя имеет неправильный формат")
    @NotBlank(message = "Электронная почта пользователя отсутствует")
    @Column(name = "email", nullable = false, unique = true)
    private String email; // адрес электронной почты

        @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        User user = (User) o;
        return email.equals(user.email);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
