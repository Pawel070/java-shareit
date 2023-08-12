package ru.practicum.shareit.item.model;

import javax.persistence.*;

import lombok.*;

import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id; // УИН вещи

    @Column(name = "name", nullable = false)
    private String name; // название

   @Column(name = "description", nullable = false)
    private String description; // описание

    @Column(name = "is_available", nullable = false)
    private Boolean available; // статус доступности аренды

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;     // владелец вещи

    @ManyToOne
    @JoinColumn(name = "request_id")
    private ItemRequest request; // если вещь была создана по запросу, то в поле хранится ссылка на этот запрос

}