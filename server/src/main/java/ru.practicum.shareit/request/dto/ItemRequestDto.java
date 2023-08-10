package ru.practicum.shareit.request.dto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDto {

    private Long id;

    @NotBlank(message = "Нет описания запрашиваемой вещи.")
    private String description;

    private LocalDateTime created;

}

