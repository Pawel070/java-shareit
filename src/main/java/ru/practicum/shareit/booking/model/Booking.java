package ru.practicum.shareit.booking.model;

import javax.persistence.*;

import java.time.LocalDateTime;

import lombok.*;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // УИН бронирования

    @Column(name = "start_date")
    private LocalDateTime start; // дата начала бронирования

    @Column(name = "end_date")
    private LocalDateTime end; // дата конца бронирования

    @ManyToOne
    @JoinColumn(name = "item_id", referencedColumnName = "id")
    private Item item; // бронируемая вещь

    @ManyToOne
    @JoinColumn(name = "booker_id", referencedColumnName = "id")
    private User booker; // бронирующий пользователь

    @Enumerated(EnumType.STRING)
    private Status status; // статус бронирования
}
