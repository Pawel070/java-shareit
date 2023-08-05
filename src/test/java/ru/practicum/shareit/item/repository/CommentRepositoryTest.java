package ru.practicum.shareit.item.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.CommentRepository;
import ru.practicum.shareit.item.service.ItemRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    Comment commentary01;
    Comment commentary02;
    Comment commentary03;
    Comment commentary04;
    User user1;
    User user2;
    Item item1;
    Item item2;

    @BeforeAll
    public void beforeAll() {
        user1 = new User(1L, "user1", "mail1@ya.ru");
        user2 = new User(2L, "user2", "mail2@ya.ru");
        userRepository.save(user1);
        userRepository.save(user2);
        item1 = new Item(1L, "item1", "desc1", true, user1, null);
        item2 = new Item(2L, "item2", "desc2", true, user2, null);
        itemRepository.save(item1);
        itemRepository.save(item2);
        commentary01 = new Comment(1L, "text10", item1, user2, LocalDateTime.now());
        commentary02 = new Comment(2L, "text11", item1, user2, LocalDateTime.now());
        commentary03 = new Comment(3L, "text20", item2, user1, LocalDateTime.now());
        commentary04 = new Comment(4L, "text21", item2, user1, LocalDateTime.now());
        commentRepository.save(commentary01);
        commentRepository.save(commentary02);
        commentRepository.save(commentary03);
        commentRepository.save(commentary04);
    }

    @Test
    void findAllByItemId() {
        List<Comment> res = commentRepository.findAllByItem_Id(item1.getId());

        assertEquals(res.size(), 2);

        assertEquals(res.get(0).getId(), commentary01.getId());
        assertEquals(res.get(0).getText(), commentary01.getText());
        assertEquals(res.get(0).getItem().getId(), commentary01.getItem().getId());
        assertEquals(res.get(0).getItem().getName(), commentary01.getItem().getName());
        assertEquals(res.get(0).getItem().getDescription(), commentary01.getItem().getDescription());
        assertEquals(res.get(0).getItem().getAvailable(), commentary01.getItem().getAvailable());
        assertEquals(res.get(0).getItem().getOwner().getId(), commentary01.getItem().getOwner().getId());
        assertEquals(res.get(0).getItem().getOwner().getName(), commentary01.getItem().getOwner().getName());
        assertEquals(res.get(0).getItem().getOwner().getEmail(), commentary01.getItem().getOwner().getEmail());
        assertEquals(res.get(0).getItem().getRequest(), commentary01.getItem().getRequest());
        assertEquals(res.get(0).getAuthor().getId(), commentary01.getAuthor().getId());
        assertEquals(res.get(0).getAuthor().getName(), commentary01.getAuthor().getName());
        assertEquals(res.get(0).getAuthor().getEmail(), commentary01.getAuthor().getEmail());
        assertEquals(res.get(0).getCreated().toString(), commentary01.getCreated().toString());

        assertEquals(res.get(1).getId(), commentary02.getId());
        assertEquals(res.get(1).getText(), commentary02.getText());
        assertEquals(res.get(1).getItem().getId(), commentary02.getItem().getId());
        assertEquals(res.get(1).getItem().getName(), commentary02.getItem().getName());
        assertEquals(res.get(1).getItem().getDescription(), commentary02.getItem().getDescription());
        assertEquals(res.get(1).getItem().getAvailable(), commentary02.getItem().getAvailable());
        assertEquals(res.get(1).getItem().getOwner().getId(), commentary02.getItem().getOwner().getId());
        assertEquals(res.get(1).getItem().getOwner().getName(), commentary02.getItem().getOwner().getName());
        assertEquals(res.get(1).getItem().getOwner().getEmail(), commentary02.getItem().getOwner().getEmail());
        assertEquals(res.get(1).getItem().getRequest(), commentary02.getItem().getRequest());
        assertEquals(res.get(1).getAuthor().getId(), commentary02.getAuthor().getId());
        assertEquals(res.get(1).getAuthor().getName(), commentary02.getAuthor().getName());
        assertEquals(res.get(1).getAuthor().getEmail(), commentary02.getAuthor().getEmail());
        assertEquals(res.get(1).getCreated().toString(), commentary02.getCreated().toString());
    }

    @Test
    void findAllByItemsId() {
        List<Long> itemsId = List.of(2L);

        List<Comment> res = commentRepository.findAllByItemsId(itemsId);

        assertEquals(res.size(), 2);

        assertEquals(res.get(0).getId(), commentary03.getId());
        assertEquals(res.get(0).getText(), commentary03.getText());
        assertEquals(res.get(0).getItem().getId(), commentary03.getItem().getId());
        assertEquals(res.get(0).getItem().getName(), commentary03.getItem().getName());
        assertEquals(res.get(0).getItem().getDescription(), commentary03.getItem().getDescription());
        assertEquals(res.get(0).getItem().getAvailable(), commentary03.getItem().getAvailable());
        assertEquals(res.get(0).getItem().getOwner().getId(), commentary03.getItem().getOwner().getId());
        assertEquals(res.get(0).getItem().getOwner().getName(), commentary03.getItem().getOwner().getName());
        assertEquals(res.get(0).getItem().getOwner().getEmail(), commentary03.getItem().getOwner().getEmail());
        assertEquals(res.get(0).getItem().getRequest(), commentary03.getItem().getRequest());
        assertEquals(res.get(0).getAuthor().getId(), commentary03.getAuthor().getId());
        assertEquals(res.get(0).getAuthor().getName(), commentary03.getAuthor().getName());
        assertEquals(res.get(0).getAuthor().getEmail(), commentary03.getAuthor().getEmail());
        assertEquals(res.get(0).getCreated().toString(), commentary03.getCreated().toString());

        assertEquals(res.get(1).getId(), commentary04.getId());
        assertEquals(res.get(1).getText(), commentary04.getText());
        assertEquals(res.get(1).getItem().getId(), commentary04.getItem().getId());
        assertEquals(res.get(1).getItem().getName(), commentary04.getItem().getName());
        assertEquals(res.get(1).getItem().getDescription(), commentary04.getItem().getDescription());
        assertEquals(res.get(1).getItem().getAvailable(), commentary04.getItem().getAvailable());
        assertEquals(res.get(1).getItem().getOwner().getId(), commentary04.getItem().getOwner().getId());
        assertEquals(res.get(1).getItem().getOwner().getName(), commentary04.getItem().getOwner().getName());
        assertEquals(res.get(1).getItem().getOwner().getEmail(), commentary04.getItem().getOwner().getEmail());
        assertEquals(res.get(1).getItem().getRequest(), commentary04.getItem().getRequest());
        assertEquals(res.get(1).getAuthor().getId(), commentary04.getAuthor().getId());
        assertEquals(res.get(1).getAuthor().getName(), commentary04.getAuthor().getName());
        assertEquals(res.get(1).getAuthor().getEmail(), commentary04.getAuthor().getEmail());
        assertEquals(res.get(1).getCreated().toString(), commentary04.getCreated().toString());
    }
}