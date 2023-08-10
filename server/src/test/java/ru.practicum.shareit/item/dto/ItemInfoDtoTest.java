package ru.practicum.shareit.item.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import java.util.List;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.user.dto.UserDto;

@JsonTest
class ItemInfoDtoTest {

    @Autowired
    private JacksonTester<ItemInfoDto> json;

    private final LocalDateTime start1 = LocalDateTime.now().minusDays(2);
    private final LocalDateTime end1 = start1.plusDays(1);
    private final LocalDateTime start2 = LocalDateTime.now().plusDays(1);
    private final LocalDateTime end2 = start2.plusDays(1);
    private final CommentDto commentDto = new CommentDto(1L, "text", "author", LocalDateTime.now());

    private final ItemInfoDto answerItemDto = new ItemInfoDto(
            1L,
            "name",
            "description",
            true,
            new UserDto(1L, "userName", "mail@mail.ru"),
            null,
            new BookingInfoDto(1L, 2L, start1, end1),
            new BookingInfoDto(2L, 3L, start2, end2),
            List.of(commentDto));

    @Test
    void itemInfoDto() throws Exception {
        var res = json.write(answerItemDto);

        assertThat(res).hasJsonPath("$.id");
        assertThat(res).hasJsonPath("$.name");
        assertThat(res).hasJsonPath("$.description");
        assertThat(res).hasJsonPath("$.available");
        assertThat(res).hasJsonPath("$.owner");
        assertThat(res).hasJsonPath("$.requestId");
        assertThat(res).hasJsonPath("$.lastBooking");
        assertThat(res).hasJsonPath("$.nextBooking");
        assertThat(res).hasJsonPath("$.comments");

        assertThat(res).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(res).extractingJsonPathStringValue("$.name").isEqualTo("name");
        assertThat(res).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(res).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(res).extractingJsonPathNumberValue("$.owner.id").isEqualTo(1);
        assertThat(res).extractingJsonPathArrayValue("$.requestId").isNull();
        assertThat(res).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(1);
        assertThat(res).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(2);
        assertThat(res).extractingJsonPathArrayValue("$.comments").hasSize(1);
    }

}