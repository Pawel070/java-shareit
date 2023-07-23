package ru.practicum.shareit.item.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.model.Item;

@Getter
@Setter
@AllArgsConstructor
public class CommentDto {

    private Long id;

    @NotEmpty
    @NotBlank
    private String text;

    @JsonIgnore
    private Item item;

    private String authorName;

    private LocalDateTime created;
}
