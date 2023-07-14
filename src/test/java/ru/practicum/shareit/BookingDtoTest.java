package ru.practicum.shareit;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@JsonTest
class BookingDtoTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    private final User owner = new User(1L, "owner", "owner@ya.ru");

    private final UserDto userDto = new UserDto(2L, "user", "mail@ya.ru");

    private final Item item = new Item(
            1L,
            "item",
            "description",
            false,
            1L,
            1L
            );

    private final BookingDto bookingDto = new BookingDto(
            1L,
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(2),
            item,
            owner,
            Status.WAITING
    );

    @Test
    void bookingDto() throws Exception {

        var res = json.write(bookingDto);

        assertThat(res).hasJsonPath("$.id");
        assertThat(res).hasJsonPath("$.start");
        assertThat(res).hasJsonPath("$.end");
        assertThat(res).hasJsonPath("$.item");
        assertThat(res).hasJsonPath("$.booker");
        assertThat(res).hasJsonPath("$.status");

        assertThat(res).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(res).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(res).extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);

    }

}