package ru.practicum.shareit.item.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import jdk.jfr.BooleanFlag;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ItemDto {

    private long id;

    @NotBlank(message = "Название предмета отсутствует.")
    @Size(min = 1, max = 30)
    private String name;

    @NotEmpty(message = "Описание товара не может быть пустым.")
    @Size(min = 1, max = 30)
    private String description;

    @BooleanFlag
    @NotNull(message = "Статус доступности аренды не может быть нулевым")
    private Boolean available;

    private long requestId;

}
