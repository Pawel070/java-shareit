package ru.practicum.shareit.item.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import java.time.LocalDateTime;

import lombok.*;

import ru.practicum.shareit.user.model.User;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;                // УИН комментария;

    @NotBlank
    @NotEmpty
    @Column(name = "text", nullable = false)
    private String text;            // текст комментария;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;              // Комментируемая вещь;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;            // пользователь, оставивший комментарий;

    @Column(name = "create_date")
    private LocalDateTime created;  // дата внесения комментария.
}
