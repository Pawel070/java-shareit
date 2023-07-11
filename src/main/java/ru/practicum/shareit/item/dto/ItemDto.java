package ru.practicum.shareit.item.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import jdk.jfr.BooleanFlag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ItemDto {

    private Long id;

    @NotBlank(message = "Название предмета отсутствует.")
    private String name;

    @NotEmpty(message = "Описание товара не может быть пустым.")
    private String description;

    @BooleanFlag
    @NotNull(message = "Статус доступности аренды не может быть нулевым")
    private Boolean available;

    private Long request;
}
