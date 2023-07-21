package ru.practicum.shareit.item.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import ru.practicum.shareit.user.model.User;

@Data
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
