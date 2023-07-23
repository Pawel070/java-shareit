package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "items")
@Builder
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // УИН вещи

    @NotBlank
    private String name; // название

    private String description; // описание

    private Boolean available; // статус доступности аренды

    private Long request; // если вещь была создана по запросу, то в поле хранится ссылка на этот запрос

}