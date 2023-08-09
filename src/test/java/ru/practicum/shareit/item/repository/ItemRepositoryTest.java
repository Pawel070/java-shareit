package ru.practicum.shareit.item.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

@Transactional
@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    User user1;
    User user2;
    Item item1;
    Item item2;
    Item item3;
    ItemRequest request1;
    ItemRequest request2;
    Pageable pageable = PageRequest.of(0, 10);

    @BeforeAll
    public void beforeAll() {
        user1 = new User(1L, "user1", "mail1@mail.ru");
        user2 = new User(2L, "user2", "mail2@mail.ru");
        userRepository.save(user1);
        userRepository.save(user2);
        request1 = new ItemRequest(1L, "req1", user1, LocalDateTime.now());
        request2 = new ItemRequest(2L, "req2", user2, LocalDateTime.now());
        itemRequestRepository.save(request1);
        itemRequestRepository.save(request2);
        item1 = new Item(1L, "itEm1", "caRrot", true, user1, request2);
        item2 = new Item(2L, "item2", "Meowem1 pot", true, user2, request1);
        item3 = new Item(3L, "item3", "carrotem1", false, user2, null);
        itemRepository.save(item1);
        itemRepository.save(item2);
        itemRepository.save(item3);
    }

    @Test
    void findByOwner_Id() {
        List<Item> res = itemRepository.findByOwner_Id(user2.getId(), pageable);

        assertEquals(res.size(), 2);

        assertEquals(res.get(0).getId(), item2.getId());
        assertEquals(res.get(0).getName(), item2.getName());
        assertEquals(res.get(0).getDescription(), item2.getDescription());
        assertEquals(res.get(0).getAvailable(), item2.getAvailable());
        assertEquals(res.get(0).getOwner().toString(), item2.getOwner().toString());

        String strT1 = res.get(0).getRequest().toString();
        strT1 = strT1.substring(0, strT1.length() - 4);
        String strT2 = item2.getRequest().toString();
        strT2 = strT2.substring(0, strT1.length());
        assertEquals(strT1, strT2);

        assertEquals(res.get(1).getId(), item3.getId());
        assertEquals(res.get(1).getName(), item3.getName());
        assertEquals(res.get(1).getDescription(), item3.getDescription());
        assertEquals(res.get(1).getAvailable(), item3.getAvailable());
        assertEquals(res.get(1).getOwner().toString(), item3.getOwner().toString());
        assertEquals(res.get(1).getRequest(), item3.getRequest());
    }

    @Test
    void searchAvailableItems_textInLowerCase() {
        List<Item> res = itemRepository.searchAvailableItems("%carrot%", pageable);

        assertEquals(res.size(), 1);

        assertEquals(res.get(0).getId(), item1.getId());
        assertEquals(res.get(0).getName(), item1.getName());
        assertEquals(res.get(0).getDescription(), item1.getDescription());
        assertEquals(res.get(0).getAvailable(), item1.getAvailable());
        assertEquals(res.get(0).getOwner().toString(), item1.getOwner().toString());

        String strT1 = res.get(0).getRequest().toString();
        strT1 = strT1.substring(0, strT1.length() - 4);
        String strT2 = item1.getRequest().toString();
        strT2 = strT2.substring(0, strT1.length());
        assertEquals(strT1, strT2);
    }

    @Test
    void searchAvailableItems_textInUpperCase() {
        List<Item> res = itemRepository.searchAvailableItems("%CARROT%", pageable);

        assertEquals(res.size(), 1);

        assertEquals(res.get(0).getId(), item1.getId());
        assertEquals(res.get(0).getName(), item1.getName());
        assertEquals(res.get(0).getDescription(), item1.getDescription());
        assertEquals(res.get(0).getAvailable(), item1.getAvailable());
        assertEquals(res.get(0).getOwner().toString(), item1.getOwner().toString());

        String strT1 = res.get(0).getRequest().toString();
        strT1 = strT1.substring(0, strT1.length() - 4);
        String strT2 = item1.getRequest().toString();
        strT2 = strT2.substring(0, strT1.length());
        assertEquals(strT1, strT2);
    }

    @Test
    void searchAvailableItems_textInMixCase() {
        List<Item> res = itemRepository.searchAvailableItems("%CaRRot%", pageable);

        assertEquals(res.size(), 1);

        assertEquals(res.get(0).getId(), item1.getId());
        assertEquals(res.get(0).getName(), item1.getName());
        assertEquals(res.get(0).getDescription(), item1.getDescription());
        assertEquals(res.get(0).getAvailable(), item1.getAvailable());
        assertEquals(res.get(0).getOwner().toString(), item1.getOwner().toString());

        String strT1 = res.get(0).getRequest().toString();
        strT1 = strT1.substring(0, strT1.length() - 4);
        String strT2 = item1.getRequest().toString();
        strT2 = strT2.substring(0, strT1.length());
        assertEquals(strT1, strT2);
    }

    @Test
    void searchAvailableItems_textInNameInDescription() {
        List<Item> res = itemRepository.searchAvailableItems("%eM1%", pageable);

        assertEquals(res.size(), 2);

        assertEquals(res.get(0).getId(), item1.getId());
        assertEquals(res.get(0).getName(), item1.getName());
        assertEquals(res.get(0).getDescription(), item1.getDescription());
        assertEquals(res.get(0).getAvailable(), item1.getAvailable());
        assertEquals(res.get(0).getOwner().toString(), item1.getOwner().toString());

        String strT1 = res.get(0).getRequest().toString();
        strT1 = strT1.substring(0, strT1.length() - 4);
        String strT2 = item1.getRequest().toString();
        strT2 = strT2.substring(0, strT1.length());
        assertEquals(strT1, strT2);

        assertEquals(res.get(1).getId(), item2.getId());
        assertEquals(res.get(1).getName(), item2.getName());
        assertEquals(res.get(1).getDescription(), item2.getDescription());
        assertEquals(res.get(1).getAvailable(), item2.getAvailable());
        assertEquals(res.get(1).getOwner().toString(), item2.getOwner().toString());

        String strT3 = res.get(1).getRequest().toString();
        strT3 = strT3.substring(0, strT3.length() - 4);
        String strT4 = item2.getRequest().toString();
        strT4 = strT4.substring(0, strT3.length());
        assertEquals(strT3, strT4);
    }

    @Test
    void findAllByRequesterId() {
        List<Item> res = itemRepository.findAllByRequest_IdOrderByIdDesc(user1.getId());

        assertEquals(res.size(), 1);

        assertEquals(res.get(0).getId(), item2.getId());
        assertEquals(res.get(0).getName(), item2.getName());
        assertEquals(res.get(0).getDescription(), item2.getDescription());
        assertEquals(res.get(0).getAvailable(), item2.getAvailable());
        assertEquals(res.get(0).getOwner().toString(), item2.getOwner().toString());

        String strT1 = res.get(0).getRequest().toString();
        strT1 = strT1.substring(0, strT1.length() - 4);
        String strT2 = item2.getRequest().toString();
        strT2 = strT2.substring(0, strT1.length());
        assertEquals(strT1, strT2);

        assertEquals(res.get(0).getRequest().getRequester().getId(), user1.getId());
    }
}