package ru.practicum.shareit.request.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import ru.practicum.shareit.user.dto.UserDto;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ItemRequestDto {

    private long id;

    @NotBlank
    @Size(max = 1000)
    private String description;

    private UserDto request;

    private LocalDateTime created;

}

