package ru.practicum.shareit.request.model;

import javax.persistence.*;
import java.time.LocalDateTime;

import lombok.*;
import ru.practicum.shareit.user.model.User;


@Entity
@Table(name = "requests")
@Getter
@Setter
@RequiredArgsConstructor
@ToString
//@EqualsAndHashCode
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id; // УИН запроса

//    @Column(name = "description")
    private String description; // что просим

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "user_id")
    private User requestor; // кто отправил запрос

//    @Column(name = "creation_date")
    private LocalDateTime created; // дата и времени нового запроса

}

