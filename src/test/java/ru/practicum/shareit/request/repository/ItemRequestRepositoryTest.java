package ru.practicum.shareit.request.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

@Slf4j
@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ItemRequestRepositoryTest {

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    User requester1;
    User requester2;
    ItemRequest itemRequest1;
    ItemRequest itemRequest2;
    ItemRequest itemRequest3;

    @BeforeAll
    private void beforeAll() {
        requester1 = new User(1L, "user1", "mail1@mail.ru");
        requester2 = new User(2L, "user2", "mail2@mail.ru");
        userRepository.save(requester1);
        userRepository.save(requester2);
        itemRequest1 = new ItemRequest(1L, "req1", requester1, LocalDateTime.now());
        itemRequest2 = new ItemRequest(2L, "req2", requester2, LocalDateTime.now());
        itemRequest3 = new ItemRequest(3L, "req3", requester1, LocalDateTime.now());
        itemRequestRepository.save(itemRequest1);
        itemRequestRepository.save(itemRequest2);
        itemRequestRepository.save(itemRequest3);
    }

    @Test
    void findAllByRequesterIdOrderByCreatedDesc() {
        List<ItemRequest> res = itemRequestRepository
                .findAllByRequesterIdOrderByCreatedDesc(requester1.getId());
        int resSize = res.size();
        log.info("res - {} , size = {}", res, resSize);
        assertEquals(resSize, 2);
    }

    @Test
    void findAllByRequesterIdOrderByCreatedDesc_userWithoutRequests() {
        List<ItemRequest> res = itemRequestRepository
                .findAllByRequesterIdOrderByCreatedDesc(999L);

        assertEquals(res.size(), 0);
    }

    @Test
    void findRequestsWithoutOwner() {
        Pageable pageable = PageRequest.of(0, 10);
        List<ItemRequest> res = itemRequestRepository
                .findAllByRequesterIdNot(requester2.getId(), pageable);
        int resSize = res.size();
        log.info("res - {} , size = {}", res, resSize);
        assertEquals(resSize, 2);
    }

    @Test
    void findRequestsWithoutOwner_userWithoutRequests() {
        Pageable pageable = PageRequest.of(0, 10);
        List<ItemRequest> res = itemRequestRepository
                .findAllByRequesterIdNot(999L, pageable);
        int resSize = res.size();
        log.info("res - {} , size = {}", res, resSize);
        assertEquals(resSize, 3);
    }
}