package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class Item {
    private Long id; // УИН вещи
    private String name; // название
    private String description; // описание
    private Boolean available; // статус доступности аренды
    private Long owner; // владелец вещи
    private Long request; // если вещь была создана по запросу, то в поле хранится ссылка на этот запрос

}