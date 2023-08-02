package ru.practicum.shareit.request.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

import java.time.LocalDateTime;

import lombok.*;

import ru.practicum.shareit.user.model.User;

@Entity
@Table(name = "requests")
@Getter
@Setter
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@Builder
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id; // УИН запроса

    @NotBlank(message = "Пустой запрос - глупый запрос")
    @Column(name = "description")
    private String description; // что просим

    @ManyToOne
    @JoinColumn(name = "requester_id")
    private User requester; // кто отправил запрос

    @Column(name = "creation_date")
    private LocalDateTime created; // дата и времени нового запроса

}

