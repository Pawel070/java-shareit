package ru.practicum.shareit.item.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {

    private Long id;

    @NotBlank
    @NotEmpty(message = "Описание не может быть пустым")
    private String text;

    private String authorName;

    private LocalDateTime created;
}
