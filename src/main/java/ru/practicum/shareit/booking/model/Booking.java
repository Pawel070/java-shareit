package ru.practicum.shareit.booking.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Data
@AllArgsConstructor
@Builder
public class Booking {
    private Long id; // УИН бронирования
    private final LocalDateTime start; // дата начала бронирования
    private final LocalDateTime end; // дата конца бронирования
    private final Item item; // бронируемая вещь
    private final User booker; // бронирующий пользователь
    private Status status; // статус бронирования
}
