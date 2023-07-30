package ru.practicum.shareit.user.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import lombok.*;

@Getter
@Setter
//@AllArgsConstructor
@Builder
@ToString
public class UserDto {

    private Long id;

//    @NotBlank(message = "Имя или логон пользователя не указан.")
    private String name;

    @Email(message = "Электронная почта пользователя имеет неправильный формат.")
    @NotBlank(message = "Электронная почта пользователя отсутствует.")
    private String email;

}