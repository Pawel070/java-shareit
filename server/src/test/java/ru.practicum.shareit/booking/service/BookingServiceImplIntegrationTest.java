package ru.practicum.shareit.booking.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import ru.practicum.shareit.State;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingModelDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

@Slf4j
@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceImplIntegrationTest {

    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;
    private final ItemRequestService itemRequestService;
    private long idTest = 100L;

    private BookingDto createBookingDto(LocalDateTime start, LocalDateTime end, long itemId) {
        idTest = idTest + 2;
        return BookingDto.builder()
                .id(idTest - 2)
                .start(start)
                .end(end)
                .itemId(itemId)
                .bookerId(itemId - 1)
                .status(Status.WAITING)
                .build();
    }

    private Item createItem(String name, String description, User user, long requestId) {
        Item item = new Item();
        item.setId(idTest);
        idTest++;
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(true);
        item.setOwner(user);
        item.setRequest(new ItemRequest(requestId, "reqestTest", user, LocalDateTime.now()));
        return item;
    }

    private User createUser(String name, String email) {
        User user = new User();
        user.setId(idTest);
        idTest++;
        user.setName(name);
        user.setEmail(email);
        return user;
    }

    @Test
    void getAllByOwnerAndState() {
        Pageable pageable = PageRequest.of(0, 10);
        User user = createUser("User", "user@mail.ru");
        UserDto userBooker = userService.create(UserMapper.toUserDto(user));
        log.info("User: {}", userBooker);
        ItemRequestInfoDto itemRequestInfoDto = itemRequestService.createItemRequest(userBooker.getId(), ItemRequestMapper
                .toItemRequestDto(new ItemRequest(200L, "reqestTest", UserMapper.toUser(userBooker), LocalDateTime.now())));
        User userOwner = createUser("User1", "user1@mail.ru");
        UserDto userOwnerDto = userService.create(UserMapper.toUserDto(userOwner));
        Item item = createItem("Khren", "BU", userOwner, itemRequestInfoDto.getId());
        log.info("Item: {}", item);
        ItemDto itemDto = itemService.create(ItemMapper.toItemDto(item), userOwnerDto.getId());
        BookingDto bookingDto = createBookingDto(LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(10), itemDto.getId());
        BookingModelDto bookingModelDto = bookingService.create(bookingDto, userBooker.getId());
        log.info("BookingModelDto: {}", bookingModelDto);
        List<BookingModelDto> bookingResponseDtoList = List.of(bookingModelDto);
        Long ownerId = userOwnerDto.getId();
        List<BookingModelDto> getBookingModelDto = bookingService.getAllBookingByOwner(ownerId, String.valueOf(State.ALL), pageable);

        assertThat(getBookingModelDto, notNullValue());
        assertThat(getBookingModelDto, hasSize(1));
    }

}