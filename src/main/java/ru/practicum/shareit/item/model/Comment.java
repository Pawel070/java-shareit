package ru.practicum.shareit.item.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.user.model.User;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                // УИН комментария;

    @NotBlank
    @NotEmpty
    private String text;            // текст комментария;

    @ManyToOne
    @JoinColumn(name = "item_id", referencedColumnName = "id")
    private Item item;              // Комментируемая вещь;

    @ManyToOne
    @JoinColumn(name = "author_id", referencedColumnName = "id")
    private User author;            // пользователь, оставивший комментарий;

    private LocalDateTime created;  // дата внесения комментария.
}
